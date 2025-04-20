package com.example.umte_project.ui.battle

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
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
    private lateinit var playerPokemon1: PokemonEntity
    private lateinit var playerPokemon2: PokemonEntity
    private lateinit var playerPokemon3: PokemonEntity
    private lateinit var playerPokemon4: PokemonEntity
    private lateinit var playerPokemon5: PokemonEntity

    private var fighterIndex = 0

    private lateinit var playerFighters: ArrayList<PokemonEntity>


    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
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


        // Získání údajů wildPokemon
        wildPokemon = intent.getParcelableExtra("wildPokemon")!!
        progressBarWild.max = 100
        progressBarWild.progress = 100 // Wild Pokémon má plné HP
        progressBarPlayer.max = 100

        pokemonViewModel.fighterPokemonList.observe(this) { fighters ->
            playerFighters = fighters as ArrayList<PokemonEntity>

        if (wildPokemon != null) {
            // Načti obrázky pomocí Glide
            loadPokemonImage(wildPokemon.imageUrl, imagePokemonWild)

            loadNextFighter()

//            battleText.text = "${playerFighters.get(fighterIndex).name} VS ${wildPokemon.name}!"
//            loadPokemonImage(playerFighters.get(fighterIndex).imageUrl, imagePokemonPlayer)
//            progressBarPlayer.progress = playerFighters.get(fighterIndex).hp
        }




        // Event listener na tlačítko útoku
        buttonAttack.setOnClickListener {
            attackWildPokemon()
        }
        }
    }

    private fun loadNextFighter(){
        if(fighterIndex >= playerFighters.size) {
            return
        }
        battleText.text = "${playerFighters.get(fighterIndex).name} VS ${wildPokemon.name}!"
        loadPokemonImage(playerFighters.get(fighterIndex).imageUrl, imagePokemonPlayer)
        progressBarPlayer.progress = playerFighters.get(fighterIndex).hp
    }

    // Simulace útoku na divokého Pokémona
    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    private fun attackWildPokemon() {
        if (progressBarWild.progress > 10) {
            val damage = 8

            wildPokemon.hp = maxOf(0, wildPokemon.hp - damage)
            playerFighters.get(fighterIndex).hp = maxOf(0, playerFighters.get(fighterIndex).hp - damage)

            progressBarWild.progress = wildPokemon.hp
            progressBarPlayer.progress = playerFighters.get(fighterIndex).hp



            pokemonViewModel.updateHP(playerFighters.get(fighterIndex))

        }
        else {
            insertPokemonIntoDatabase()

            val resultIntent = Intent()
            resultIntent.putExtra("wasCaught", "true")
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }

        if (progressBarWild.progress <= 10){
            battleText.text = "${playerFighters.get(fighterIndex).name} defeated ${wildPokemon.name}!"
            buttonAttack.text = "Catch ${wildPokemon.name}!"
        }else{
            if(playerFighters.get(fighterIndex).hp <= 5){
                battleText.text = "${playerFighters.get(fighterIndex).name} fainted!"

                Log.d("TAG", "PŘED ZVÝŠENÍM:")
                Log.d("TAG", "Hodnota fighterIndex: $fighterIndex")
                Log.d("TAG","Velikost seznamu: ${playerFighters.size}")
                fighterIndex++
                Log.d("TAG", "PO ZVÝŠENÍM:")
                Log.d("TAG", "Hodnota fighterIndex: $fighterIndex")
                Log.d("TAG","Velikost seznamu: ${playerFighters.size}")

                if(fighterIndex >= playerFighters.size) {
                    Log.d("TAG","Vypni se!")
                    val resultIntent = Intent()
                    resultIntent.putExtra("wasCaught", "false")
                    setResult(Activity.RESULT_OK, resultIntent)
                    finish()
                } else{
                    Log.d("TAG","Načti dalšího")
                    loadNextFighter()
                }
                //buttonAttack.visibility = View.GONE
            }
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
