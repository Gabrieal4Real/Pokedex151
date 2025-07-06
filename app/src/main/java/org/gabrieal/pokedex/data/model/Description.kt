package org.gabrieal.pokedex.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Description(
    @SerialName("flavor_text")
    val flavorText: String?,
    val language: NamedResource
)