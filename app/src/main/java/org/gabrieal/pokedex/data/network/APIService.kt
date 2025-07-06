package org.gabrieal.pokedex.data.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import org.gabrieal.pokedex.data.model.DescriptionList
import org.gabrieal.pokedex.data.model.PokemonDetail
import org.gabrieal.pokedex.data.model.PokemonList

class APIService(private val httpClient: HttpClient) {
    suspend fun fetchPokemon(): PokemonList {
        val response: HttpResponse = httpClient.get("https://pokeapi.co/api/v2/pokemon?limit=151")
        return response.body()
    }

    suspend fun fetchPokemonByName(name: String): PokemonDetail {
        val response: HttpResponse = httpClient.get("https://pokeapi.co/api/v2/pokemon/$name")
        return response.body()
    }

    suspend fun fetchPokemonDescription(name: String): DescriptionList {
        val response: HttpResponse = httpClient.get("https://pokeapi.co/api/v2/pokemon-species/$name")
        return response.body()
    }
}