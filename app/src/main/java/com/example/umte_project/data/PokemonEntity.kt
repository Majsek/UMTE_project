package com.example.umte_project.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pokemon_table")
data class PokemonEntity(
    @PrimaryKey val id: Int,  // Unique pokemon ID
    val name: String,
    val imageUrl: String // Můžeme uložit URL obrázku přímo
)
