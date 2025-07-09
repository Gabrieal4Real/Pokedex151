package org.gabrieal.pokedex.feature.pokedex.viewmodel

import org.gabrieal.pokedex.data.model.PokemonDetail
import org.gabrieal.pokedex.data.model.PokemonList

data class PokeDexUiState(
    val pokemonList: PokemonList? = null,
    val pokemonDetail: PokemonDetail? = null,
    val isCatchMode: Boolean = false
)
