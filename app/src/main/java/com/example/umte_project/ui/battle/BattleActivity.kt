package com.example.umte_project.ui.battle

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.umte_project.R

class BattleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_battle)

        val pokemonName = intent.getStringExtra("pokemonName") ?: "Unknown"
        findViewById<TextView>(R.id.battleText).text = "Bojuješ proti $pokemonName!"
    }
}
