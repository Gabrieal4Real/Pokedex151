package org.gabrieal.pokedex.data.model

import kotlinx.serialization.Serializable

@Serializable
data class PokemonList(
    val count: Int? = null,
    val results: List<NamedResource>? = null
)