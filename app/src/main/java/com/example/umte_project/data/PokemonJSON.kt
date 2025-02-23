package com.example.umte_project.data

import com.google.gson.annotations.SerializedName

data class PokemonJSON(
    val id: Int,  // Unique pokemon ID
    val name: String,
    val sprites: Sprites
)

data class Sprites(
    @SerializedName("front_default") val frontDefault: String,
    val other: Other,
)

data class Other (
    val home: Home,
    @SerializedName("official-artwork") val officialArtwork: OfficialArtwork
)

data class Home (
    @SerializedName("front_default") val frontDefault: String
)

data class OfficialArtwork (
    @SerializedName("front_default") val frontDefault: String
)
