package org.gabrieal.pokedex.data.model

import kotlinx.serialization.Serializable


@Serializable
data class NamedResource(
    val name: String? = null,
    val url: String? = null
)