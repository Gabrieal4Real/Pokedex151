package org.gabrieal.pokedex.data.model

import kotlinx.serialization.Serializable

@Serializable
data class PokemonDetail(
    val name: String? = null,
    val abilities: List<AbilitySlot>? = null,
    val cries: Cries? = null,
    val sprites: Sprites? = null,
    val types: List<TypeSlot>? = null,
    var description: String? = null
)