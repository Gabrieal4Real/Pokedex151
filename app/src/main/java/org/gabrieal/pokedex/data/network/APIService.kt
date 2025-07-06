package org.gabrieal.pokedex.data.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import org.gabrieal.pokedex.data.model.PokemonResponse

class APIService(private val httpClient: HttpClient) {
    suspend fun fetchPokemon(): PokemonResponse {
        val response: HttpResponse = httpClient.get("https://pokeapi.co/api/v2/pokemon?limit=151")
        return response.body()
    }
}