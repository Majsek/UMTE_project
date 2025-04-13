package com.example.umte_project.ui.pokemon

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.umte_project.data.PokemonDAO
import com.example.umte_project.data.PokemonDatabase
import com.example.umte_project.data.PokemonEntity
import com.example.umte_project.data.PokemonJSON
import kotlinx.coroutines.launch

class PokemonViewModel(application: Application) : AndroidViewModel(application) {
    private val pokemonDao: PokemonDAO

    val pokemonList: LiveData<List<PokemonEntity>>

    init {
        val database = PokemonDatabase.getDatabase(application)
        pokemonDao = database.pokemonDao()

        // Streamujeme změny z databáze přímo do LiveData
        pokemonList = pokemonDao.getAllPokemon().asLiveData()
        refreshPokemonList() // automaticky při startu appky
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
        val healedHP = (elapsedSeconds * 1).toInt() // 1% za sekundu

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
                }
            }
        }
    }



}
