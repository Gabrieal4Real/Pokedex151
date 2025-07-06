package org.gabrieal.pokedex.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Cries(
    val latest: String? = null,
    val legacy: String? = null
)