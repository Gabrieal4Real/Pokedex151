package org.gabrieal.pokedex.feature.pokedex.repo

import kotlinx.coroutines.flow.Flow
import org.gabrieal.pokedex.data.model.PokemonResponse

interface PokeDexRepo {

    suspend fun getPokemons(): Flow<PokemonResponse>
}