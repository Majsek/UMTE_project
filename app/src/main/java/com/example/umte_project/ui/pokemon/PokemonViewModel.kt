package com.example.umte_project.ui.pokemon

import android.app.Application
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.umte_project.MainActivity
import com.example.umte_project.data.PokemonDAO
import com.example.umte_project.data.PokemonDatabase
import com.example.umte_project.data.PokemonEntity
import com.example.umte_project.data.PokemonJSON
import com.example.umte_project.utils.PokemonHealWorker
import kotlinx.coroutines.launch

class PokemonViewModel(application: Application) : AndroidViewModel(application) {
    private val pokemonDao: PokemonDAO

    val pokemonList: LiveData<List<PokemonEntity>>
    val fighterPokemonList: LiveData<List<PokemonEntity>>


    init {
        val database = PokemonDatabase.getDatabase(application)
        pokemonDao = database.pokemonDao()

        // Streamujeme změny z databáze přímo do LiveData
        pokemonList = pokemonDao.getAllPokemon().asLiveData()
        refreshPokemonList() // automaticky při startu appky

        fighterPokemonList = pokemonDao.getAllFighterPokemon().asLiveData()

    }

    private val _text = MutableLiveData<String>().apply {
        value = ""
    }
    val text: LiveData<String> = _text

    suspend fun insertPokemon(pokemon: PokemonEntity) {
        viewModelScope.launch {
            pokemonDao.insertPokemon(pokemon)
            //loadPokemon() // už není potřeba, protože změna se projeví sama
        }
    }

    suspend fun getFirstPokemon(): PokemonEntity? {
        return pokemonDao.getFirstPokemon()
    }

    suspend fun getPokemonCount(): Int {
        return pokemonDao.getPokemonCount()
    }

    fun updateHP(pokemon: PokemonEntity) {
        viewModelScope.launch {
            pokemonDao.updateHP(
                id = pokemon.id,
                hp = pokemon.hp,
                lastUpdated = System.currentTimeMillis()
            )
        }
    }

    fun calculateHealedHP(pokemon: PokemonEntity): Int {
        val now = System.currentTimeMillis()
        val elapsedMillis = now - pokemon.lastUpdated

        val elapsedSeconds = elapsedMillis / 1000
        val healedHP = (elapsedSeconds * 5).toInt() // 5% za sekundu

        return minOf(100, pokemon.hp + healedHP)
    }



    fun refreshPokemonList() {
        viewModelScope.launch {
            val pokemons = pokemonDao.getAllPokemonOnce() // vytvoříme si níž
            pokemons.forEach { pokemon ->
                val healedHP = calculateHealedHP(pokemon)
                if (healedHP != pokemon.hp) {
                    pokemonDao.updateHP(
                        id = pokemon.id,
                        hp = healedHP,
                        lastUpdated = System.currentTimeMillis()
                    )

                    if (healedHP == 100 && pokemon.hp < 100) {
                        PokemonHealWorker.sendHealedNotification(getApplication(),pokemon.name)
                    }
                }
            }
        }
    }

    fun updateIsFighter(id: Int, checked: Boolean) {
        viewModelScope.launch {
            pokemonDao.updateIsFighter(id, checked)
        }
    }

    fun getAllFighterPokemon(): LiveData<List<PokemonEntity>> {
        return pokemonDao.getAllFighterPokemon().asLiveData()
    }
}
