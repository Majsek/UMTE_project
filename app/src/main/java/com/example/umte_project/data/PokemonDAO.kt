package com.example.umte_project.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
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

    @Query("SELECT * FROM pokemon_table WHERE isFighter = 1")
    fun getAllFighterPokemon(): Flow<List<PokemonEntity>>

    @Query("SELECT * FROM pokemon_table ORDER BY id LIMIT 1")
    suspend fun getFirstPokemon(): PokemonEntity?

    @Query("SELECT COUNT(*) FROM pokemon_table")
    suspend fun getPokemonCount(): Int

    @Query("UPDATE pokemon_table SET hp = :hp, lastUpdated = :lastUpdated WHERE id = :id")
    suspend fun updateHP(id: Int, hp: Int, lastUpdated: Long)

    @Query("SELECT * FROM pokemon_table")
    suspend fun getAllPokemonOnce(): List<PokemonEntity>

    @Query("UPDATE pokemon_table SET isFighter = :isFighter WHERE id = :id")
    suspend fun updateIsFighter(id: Int, isFighter: Boolean)

    @Query("SELECT COUNT(*) FROM pokemon_table WHERE isFighter = 1")
    suspend fun countFighters(): Int

    @Query("UPDATE pokemon_table SET lastUpdated = :lastUpdated WHERE id = :id")
    suspend fun updateLastUpdated(id: Int, lastUpdated: Long)
}
