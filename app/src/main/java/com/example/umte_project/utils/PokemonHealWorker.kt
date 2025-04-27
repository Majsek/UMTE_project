package com.example.umte_project.utils

import android.app.Application
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.umte_project.MainActivity
import com.example.umte_project.data.PokemonDatabase
import com.example.umte_project.ui.notifications.NotificationsViewModel
import dagger.hilt.android.internal.Contexts.getApplication

class PokemonHealWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val db = PokemonDatabase.getDatabase(applicationContext)
        val dao = db.pokemonDao()

        val pokemons = dao.getAllPokemonOnce()
        val now = System.currentTimeMillis()

        for (pokemon in pokemons) {
            val elapsedSeconds = (now - pokemon.lastUpdated) / 1000

            var healedHP = 0
            if (!pokemon.isFighter) {
                healedHP = (elapsedSeconds * 1).toInt()
            }

            val newHP = minOf(100, pokemon.hp + healedHP)

            if (newHP > pokemon.hp) {
                dao.updateHP(pokemon.id, newHP, now)

                if (newHP == 100 && pokemon.hp < 100) {
                    // Voláme tvoji funkci na notifikaci
                    sendHealedNotification(applicationContext, pokemon.name)
                }
            }
        }

        return Result.success()
    }


    companion object {
        fun sendHealedNotification(context: Context,pokemonName: String) {

            // Intent na otevření MainActivity
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra("pokemon_name", pokemonName) // volitelně – můžeš pak rozparsovat konkrétního Pokémona
            }

            val pendingIntent = PendingIntent.getActivity(
                context,
                pokemonName.hashCode(),  // unikátní requestCode
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val builder = NotificationCompat.Builder(context, "HEAL_CHANNEL")
                .setSmallIcon(android.R.drawable.star_on)
                .setContentTitle("Pokémon is fully healed!")
                .setContentText("$pokemonName is now at 100% HP 🎉")
                .setContentIntent(pendingIntent) // <- Tady to přidáš
                .setAutoCancel(true) // Notifikace zmizí po kliknutí
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            val notificationViewModel = NotificationsViewModel(application = Application())
            notificationViewModel.addNotification("$pokemonName is now at 100% HP 🎉")


            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(pokemonName.hashCode(), builder.build())
        }
        }
    }
