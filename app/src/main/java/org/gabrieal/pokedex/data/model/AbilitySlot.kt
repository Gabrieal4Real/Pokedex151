package org.gabrieal.pokedex.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AbilitySlot(
    val ability: NamedResource? = null,
    @SerialName("is_hidden")
    val isHidden: Boolean? = null,
    val slot: Int? = null
)