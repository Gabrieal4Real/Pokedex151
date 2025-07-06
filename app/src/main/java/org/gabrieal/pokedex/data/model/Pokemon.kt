package org.gabrieal.pokedex.data.model

import kotlinx.serialization.Serializable

@Serializable
data class PokemonResponse(
    val count: Int?,
    val results: List<Pokemon>?
)

@Serializable
data class Pokemon(
    val name: String?,
    val url: String?
)