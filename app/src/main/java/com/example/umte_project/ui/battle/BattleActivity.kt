package com.example.umte_project.ui.battle

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.umte_project.R

class BattleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_battle)

        val wildPokemonName = intent.getStringExtra("pokemonName") ?: "Unknown"
        val playerPokemonName = intent.getStringExtra("playerPokemon") ?: "Unknown"

        findViewById<TextView>(R.id.battleText).text = "$playerPokemonName VS $wildPokemonName!"
    }
}
