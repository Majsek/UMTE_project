package com.example.umte_project.ui.battle

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.umte_project.R
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

    private lateinit var wildPokemonName: String
    private lateinit var playerPokemonName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_battle)

        // Inicializace UI prvků
        battleText = findViewById(R.id.battleText)
        progressBarWild = findViewById(R.id.progressBar_wild)
        progressBarPlayer = findViewById(R.id.progressBar_player)
        imagePokemonWild = findViewById(R.id.image_pokemon_wild)
        imagePokemonPlayer = findViewById(R.id.image_pokemon_player)
        buttonAttack = findViewById(R.id.button_attack)

        // Získání údajů o Pokémonech
        wildPokemonName = intent.getStringExtra("pokemonName") ?: "Unknown"
        playerPokemonName = intent.getStringExtra("playerPokemon") ?: "Unknown"

        // Nastavení textu
        battleText.text = "$playerPokemonName VS $wildPokemonName!"

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
        }
        else {
            insertPokemonIntoDatabase()
        }

        if (progressBarWild.progress <= 10){
            battleText.text = "$playerPokemonName defeated $wildPokemonName!"
            buttonAttack.text = "Catch $wildPokemonName!"
        }
    }

    private fun insertPokemonIntoDatabase() {
        lifecycleScope.launch {
//            pokemonViewModel.insertPokemon(newPokemon) // Uložení do DB
//            battleText.text = "$pokemonName byl chycen!"
        }
    }
}
