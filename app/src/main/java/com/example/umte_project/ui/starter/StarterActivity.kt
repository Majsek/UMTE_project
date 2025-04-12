package com.example.umte_project.ui.starter

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.umte_project.MainActivity
import com.example.umte_project.R
import com.example.umte_project.api.RetrofitClient
import com.example.umte_project.data.PokemonDatabase
import com.example.umte_project.data.PokemonEntity
import com.example.umte_project.data.PokemonJSON
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StarterActivity : AppCompatActivity() {

    private lateinit var starter1: Button
    private lateinit var starter2: Button
    private lateinit var starter3: Button
    private lateinit var starter1Image: ImageView
    private lateinit var starter2Image: ImageView
    private lateinit var starter3Image: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_starter)

        // Nastavení fullscreenu
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                )

        starter1 = findViewById(R.id.starter1)
        starter2 = findViewById(R.id.starter2)
        starter3 = findViewById(R.id.starter3)
        starter1Image = findViewById(R.id.starter1Image)
        starter2Image = findViewById(R.id.starter2Image)
        starter3Image = findViewById(R.id.starter3Image)

        loadStarterPokemon(1, starter1, starter1Image) // Bulbasaur
        loadStarterPokemon(4, starter2, starter2Image) // Charmander
        loadStarterPokemon(7, starter3, starter3Image) // Squirtle
    }

    private fun loadStarterPokemon(id: Int, button: Button, imageView: ImageView) {
        RetrofitClient.instance.getPokemon(id.toString()).enqueue(object : Callback<PokemonJSON> {
            override fun onResponse(call: Call<PokemonJSON>, response: Response<PokemonJSON>) {
                if (response.isSuccessful) {
                    val pokemon = response.body()
                    if (pokemon != null) {
                        val imageUrl = pokemon.sprites.other.officialArtwork.frontDefault
                        button.text = pokemon.name.capitalize()
                        Glide.with(this@StarterActivity).load(imageUrl).into(imageView)

                        button.setOnClickListener {
                            selectStarter(pokemon.name, imageUrl)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<PokemonJSON>, t: Throwable) {
                button.text = "Error"
            }
        })
    }

    private fun selectStarter(pokemonName: String, imageUrl: String) {
        lifecycleScope.launch {
            val starterPokemon = PokemonEntity(name = pokemonName, imageUrl = imageUrl)
            val database = PokemonDatabase.getDatabase(this@StarterActivity).pokemonDao()
            database.insertPokemon(starterPokemon)

            startActivity(Intent(this@StarterActivity, MainActivity::class.java))
            finish()
        }
    }
}
