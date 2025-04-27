package com.example.umte_project.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "pokemon_table")
data class PokemonEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val imageUrl: String,
    var hp: Int = 100,
    val lastUpdated: Long = System.currentTimeMillis(),
    var isFighter: Boolean = false,
    //val attack: Int,
    //val defense: Int
) : Parcelable
