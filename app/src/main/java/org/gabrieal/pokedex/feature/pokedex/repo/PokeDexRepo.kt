package org.gabrieal.pokedex.feature.pokedex.repo

import kotlinx.coroutines.flow.Flow
import org.gabrieal.pokedex.data.model.DescriptionList
import org.gabrieal.pokedex.data.model.PokemonDetail
import org.gabrieal.pokedex.data.model.PokemonList

interface PokeDexRepo {

    suspend fun getPokemons(): Flow<PokemonList>
    suspend fun getPokemonByName(name: String): Flow<PokemonDetail>
    suspend fun getPokemonDescription(name: String): Flow<DescriptionList>
}