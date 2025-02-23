package com.example.umte_project.data

import com.google.gson.annotations.SerializedName

data class Pokemon(
    val name: String,
    val sprites: Sprites,
)

data class Sprites(
    val front_default: String,
    val other: Other
)

data class Other (
    val home: Home,
    @SerializedName("official-artwork")
    val official_artwork: Official_artwork
)

data class Home (
    val front_default: String
)


data class Official_artwork (
    val front_default: String
)
