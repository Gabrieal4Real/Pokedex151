package org.gabrieal.pokedex.data.model

import kotlinx.serialization.Serializable


@Serializable
data class TypeSlot(
    val slot: Int? = null,
    val type: NamedResource? = null
)
