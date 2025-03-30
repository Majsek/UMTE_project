package com.example.umte_project.data

import androidx.room.*
import com.example.umte_project.data.PokemonEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface PokemonDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPokemon(pokemon: PokemonEntity)

    @Delete
    suspend fun deletePokemon(pokemon: PokemonEntity)

    //tohle asi ani nepoužívám nikde
    @Query("SELECT * FROM pokemon_table")
    fun getAllPokemon(): Flow<List<PokemonEntity>>

    @Query("SELECT * FROM pokemon_table ORDER BY id LIMIT 1")
    suspend fun getFirstPokemon(): PokemonEntity?


    @Query("SELECT COUNT(*) FROM pokemon_table")
    suspend fun getPokemonCount(): Int


}
