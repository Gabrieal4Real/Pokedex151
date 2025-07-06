package org.gabrieal.pokedex.data.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class DescriptionList(
    @SerialName("flavor_text_entries")
    val flavorTextEntries: List<Description>?
)