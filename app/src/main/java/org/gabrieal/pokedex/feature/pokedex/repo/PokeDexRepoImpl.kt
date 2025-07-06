package org.gabrieal.pokedex.feature.pokedex.repo

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import org.gabrieal.pokedex.data.model.PokemonDetail
import org.gabrieal.pokedex.data.model.PokemonList
import org.gabrieal.pokedex.data.network.APIService

class PokeDexRepoImpl(val apiService: APIService) : PokeDexRepo {

    override suspend fun getPokemons(): Flow<PokemonList> {
        return flow {
            emit(apiService.fetchPokemon())
        }.catch { e ->
            println("Error fetching pokemons: ${e.message}")
            throw e
        }
    }

    override suspend fun getPokemonByName(name: String): Flow<PokemonDetail> {
        return flow {
            emit(apiService.fetchPokemonByName(name))
        }.catch { e ->
            println("Error fetching pokemon: ${e.message}")
            throw e
        }
    }
}