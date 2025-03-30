package com.example.umte_project.ui.starter

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.umte_project.MainActivity
import com.example.umte_project.R
import com.example.umte_project.data.PokemonDatabase
import com.example.umte_project.data.PokemonEntity
import kotlinx.coroutines.launch

class StarterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_starter)

        val starter1 = findViewById<Button>(R.id.starter1)
        val starter2 = findViewById<Button>(R.id.starter2)
        val starter3 = findViewById<Button>(R.id.starter3)

        starter1.setOnClickListener { selectStarter("Bulbasaur") }
        starter2.setOnClickListener { selectStarter("Charmander") }
        starter3.setOnClickListener { selectStarter("Squirtle") }
    }

    private fun selectStarter(pokemonName: String) {
        lifecycleScope.launch {
            val starterPokemon = PokemonEntity(name = pokemonName, imageUrl = getStarterImage(pokemonName))
            val database = PokemonDatabase.getDatabase(this@StarterActivity).pokemonDao()
            database.insertPokemon(starterPokemon)

            // Po uložení zavři aktivitu
            startActivity(Intent(this@StarterActivity, MainActivity::class.java))
            finish()
        }
    }

    private fun getStarterImage(name: String): String {
        return when (name) {
            "Bulbasaur" -> "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png"
            "Charmander" -> "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/4.png"
            "Squirtle" -> "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/7.png"
            else -> ""
        }
    }
}
