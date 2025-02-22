package com.example.umte_project.ui.pokemon

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PokemonViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Pokemon Fragment"
    }
    val text: LiveData<String> = _text
}