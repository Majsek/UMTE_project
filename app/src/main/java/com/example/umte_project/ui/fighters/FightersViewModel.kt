package com.example.umte_project.ui.fighters

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FightersViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Fighters Fragment"
    }
    val text: LiveData<String> = _text
}