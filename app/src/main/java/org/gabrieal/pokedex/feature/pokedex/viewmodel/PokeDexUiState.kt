package org.gabrieal.pokedex.feature.pokedex.viewmodel

import org.gabrieal.pokedex.data.model.NamedResource
import org.gabrieal.pokedex.data.model.PokemonDetail
import org.gabrieal.pokedex.data.model.PokemonList
import org.gabrieal.pokedex.helpers.enums.Filter

data class PokeDexUiState(
    val pokemonList: PokemonList? = null,
    val pokemonDetail: PokemonDetail? = null,
    val isCatchMode: Boolean = false,
    val catchingPokemon: NamedResource? = null,
    val selectedFilter: Filter = Filter.ALL,
    val filteredList: List<NamedResource> = emptyList(),
    val caughtList: Set<NamedResource> = emptySet()
)
