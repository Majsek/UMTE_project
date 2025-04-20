package com.example.umte_project.ui.notifications

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.umte_project.data.NotificationEntity
import com.example.umte_project.data.PokemonDatabase
import kotlinx.coroutines.launch

class NotificationsViewModel(application: Application) : AndroidViewModel(application) {

    private val _text = MutableLiveData<String>().apply {
        value = "You have no notifications"
    }
    val text: LiveData<String> = _text

    private val notificationDao = PokemonDatabase.getDatabase(application).notificationDao()

    val notifications = notificationDao.getAllNotifications().asLiveData()

    fun addNotification(message: String) {
        viewModelScope.launch {
            notificationDao.insert(NotificationEntity(message = message))
        }
    }

    fun clearNotifications() {
        viewModelScope.launch {
            notificationDao.clearAll()
        }
    }
}