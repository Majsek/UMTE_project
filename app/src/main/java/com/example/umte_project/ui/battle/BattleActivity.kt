package com.example.umte_project.ui.battle

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.umte_project.R
import com.example.umte_project.data.PokemonEntity
import com.example.umte_project.databinding.FragmentPokemonBinding
import com.example.umte_project.ui.pokemon.PokemonViewModel
import kotlinx.coroutines.launch

class BattleActivity : AppCompatActivity() {
    // ViewModel pro práci s databází
    private val pokemonViewModel: PokemonViewModel by viewModels()

    // Deklarace proměnných pro UI prvky
    private lateinit var battleText: TextView
    private lateinit var progressBarWild: ProgressBar
    private lateinit var progressBarPlayer: ProgressBar
    private lateinit var imagePokemonWild: ImageView
    private lateinit var imagePokemonPlayer: ImageView
    private lateinit var buttonAttack: Button

    private lateinit var wildPokemon: PokemonEntity
    private lateinit var playerPokemon: PokemonEntity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_battle)

        // Nastavení fullscreenu
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                )

        // Inicializace UI prvků
        battleText = findViewById(R.id.battleText)
        progressBarWild = findViewById(R.id.progressBar_wild)
        progressBarPlayer = findViewById(R.id.progressBar_player)
        imagePokemonWild = findViewById(R.id.image_pokemon_wild)
        imagePokemonPlayer = findViewById(R.id.image_pokemon_player)
        buttonAttack = findViewById(R.id.button_attack)

        // Získání údajů o Pokémonech
        wildPokemon = intent.getParcelableExtra("wildPokemon")!!
        playerPokemon = intent.getParcelableExtra("playerPokemon")!!


        if (wildPokemon != null && playerPokemon != null) {
            battleText.text = "${playerPokemon.name} VS ${wildPokemon.name}!"

            // Načti obrázky pomocí Glide
            loadPokemonImage(wildPokemon.imageUrl, imagePokemonWild)
            loadPokemonImage(playerPokemon.imageUrl, imagePokemonPlayer)
        }

        // Nastavení defaultních hodnot progress barů (HP)
        progressBarWild.max = 100
        progressBarWild.progress = 100 // Wild Pokémon má plné HP
        progressBarPlayer.max = 100
        progressBarPlayer.progress = 100 // Hráčův Pokémon má plné HP

        // TODO: Nastavit obrázky Pokémonů podle jejich jména (můžeš použít Glide/Picasso nebo drawable)
        // imagePokemonWild.setImageResource(R.drawable.pokemon_wild)
        // imagePokemonPlayer.setImageResource(R.drawable.pokemon_player)

        // Event listener na tlačítko útoku
        buttonAttack.setOnClickListener {
            attackWildPokemon()
        }
    }

    // Simulace útoku na divokého Pokémona
    private fun attackWildPokemon() {
        if (progressBarWild.progress > 10) {
            progressBarWild.progress -= 9 // Snížení HP o 20
            progressBarPlayer.progress -= 8 // Snížení HP o 20
        }
        else {
            insertPokemonIntoDatabase()

            val resultIntent = Intent()
            resultIntent.putExtra("wasCaught", "true")
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }

        if (progressBarWild.progress <= 10){
            battleText.text = "${playerPokemon.name} defeated ${wildPokemon.name}!"
            buttonAttack.text = "Catch ${wildPokemon.name}!"
        }
    }//

    private fun insertPokemonIntoDatabase() {
        lifecycleScope.launch {
            pokemonViewModel.insertPokemon(wildPokemon)
            battleText.text = "${wildPokemon.name} byl chycen!"
        }
    }


    private fun loadPokemonImage(url: String, imageView: ImageView) {
        Glide.with(this)
            .load(url)
            .placeholder(R.drawable.placeholder) // Obrázek při načítání
            .error(R.drawable.error) // Obrázek pokud načtení selže
            .into(imageView)
    }
}
