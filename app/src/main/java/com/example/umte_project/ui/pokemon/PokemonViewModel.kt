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
    }

    private val _text = MutableLiveData<String>().apply {
        value = "This is Pokemon Fragment"
    }
    val text: LiveData<String> = _text

    suspend fun insertPokemon(pokemon: PokemonEntity) {
        viewModelScope.launch {
            pokemonDao.insertPokemon(pokemon)
            //loadPokemon() // už není potřeba, protože změna se projeví sama
        }
    }
}
