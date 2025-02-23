package com.example.umte_project.ui.pokemon

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.umte_project.data.PokemonDAO
import com.example.umte_project.data.PokemonDatabase
import com.example.umte_project.data.PokemonEntity
import com.example.umte_project.data.PokemonJSON
import kotlinx.coroutines.launch

class PokemonViewModel(application: Application) : AndroidViewModel(application) {
    private val pokemonDao: PokemonDAO

    private val _pokemonList = MutableLiveData<List<PokemonEntity>>()
    val pokemonList: LiveData<List<PokemonEntity>> = _pokemonList

    init {
        val database = PokemonDatabase.getDatabase(application)
        pokemonDao = database.pokemonDao()
        loadPokemon()
    }

    private val _text = MutableLiveData<String>().apply {
        value = "This is Pokemon Fragment"
    }
    val text: LiveData<String> = _text

    fun loadPokemon() {
        viewModelScope.launch {
            _pokemonList.postValue(pokemonDao.getAllPokemon())
        }
    }

    suspend fun insertPokemon(pokemon: PokemonEntity) {
        viewModelScope.launch {
            pokemonDao.insertPokemon(pokemon)
            loadPokemon() // Po přidání hned znovu načteme data
        }
    }
}
