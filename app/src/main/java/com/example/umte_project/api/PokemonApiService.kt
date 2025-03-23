package com.example.umte_project.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import com.example.umte_project.data.PokemonJSON

interface PokemonApiService {
    @GET("pokemon/{name}")
    fun getPokemon(@Path("name") pokemonName: String): Call<PokemonJSON>
}
