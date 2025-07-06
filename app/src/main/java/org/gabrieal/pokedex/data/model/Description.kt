package org.gabrieal.pokedex.data.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class Description(
    @SerialName("flavor_text")
    val flavorText: String?,
    val language: NamedResource
)