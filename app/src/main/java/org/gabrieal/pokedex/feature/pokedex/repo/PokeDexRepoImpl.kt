package org.gabrieal.pokedex.feature.pokedex.repo

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import org.gabrieal.pokedex.data.model.PokemonResponse
import org.gabrieal.pokedex.data.network.APIService

class PokeDexRepoImpl(val apiService: APIService): PokeDexRepo {

    override suspend fun getPokemons(): Flow<PokemonResponse> {
        return flow {
            emit(apiService.fetchPokemon())
        }.catch { e ->
            println("Error fetching pokemons: ${e.message}")
            throw e
        }
    }

}