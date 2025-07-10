package org.gabrieal.pokedex.feature.pokedex.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.gabrieal.pokedex.data.model.NamedResource
import org.gabrieal.pokedex.data.model.PokemonDetail
import org.gabrieal.pokedex.data.model.PokemonList
import org.gabrieal.pokedex.feature.pokedex.repo.PokeDexRepo
import org.gabrieal.pokedex.helpers.enums.Filter

class PokeDexViewModel(private val repo: PokeDexRepo) : ViewModel() {

    private val _uiState = MutableStateFlow(PokeDexUiState())
    val uiState: StateFlow<PokeDexUiState> = _uiState

    fun setCatchingPokemon(pokemon: NamedResource) {
        _uiState.value = _uiState.value.copy(
            catchingPokemon = pokemon,
            isCatchMode = true
        )
    }

    fun setFilter(filter: Filter) {
        if (filter == _uiState.value.selectedFilter || _uiState.value.isCatchMode) return

        val fullList = _uiState.value.pokemonList?.results ?: emptyList()
        val caughtSet = _uiState.value.caughtList
        val filtered = when (filter) {
            Filter.ALL -> fullList
            Filter.CAUGHT -> fullList.filter { it in caughtSet }
            Filter.MISSING -> fullList.filter { it !in caughtSet }
        }
        _uiState.value = _uiState.value.copy(
            selectedFilter = filter,
            filteredList = filtered
        )
    }

    fun setPokemonList(list: List<NamedResource>) {
        _uiState.value = _uiState.value.copy(
            pokemonList = PokemonList(results = list),
            filteredList = list
        )
    }

    fun toggleCaughtStatus(pokemon: NamedResource) {
        val current = _uiState.value.caughtList.toMutableSet()
        if (pokemon in current) current.remove(pokemon) else current.add(pokemon)
        _uiState.value =
            _uiState.value.copy(caughtList = current, catchingPokemon = null, isCatchMode = false)
    }

    fun loadPokemonList() = viewModelScope.launch {
        runCatching {
            repo.getPokemons().collect {
                setPokemonList(it.results ?: emptyList())
            }
        }.onFailure {
            // Optionally handle errors in UI state
        }
    }

    fun getPokemonByName(name: String) = viewModelScope.launch {
        runCatching {
            repo.getPokemonByName(name).collect { detail ->
                fetchAndAttachDescription(name, detail)
            }
        }.onFailure {
            // Optionally handle errors in UI state
        }
    }

    private fun fetchAndAttachDescription(name: String, detail: PokemonDetail) =
        viewModelScope.launch {
            runCatching {
                repo.getPokemonDescription(name).collect { species ->
                    val rawText = species.flavorTextEntries
                        ?.firstOrNull { it.language.name == "en" }
                        ?.flavorText.orEmpty()

                    detail.description = cleanDescription(rawText)
                    _uiState.value = _uiState.value.copy(pokemonDetail = detail)
                }
            }.onFailure {
                // Optionally handle errors in UI state
            }
        }

    private fun cleanDescription(text: String): String {
        return text
            .replace("\u000C", "")
            .replace("\u00AD\n", "")
            .replace("\u00AD", "")
            .replace(" -\n", " - ")
            .replace("-\n", "-")
            .replace("\n", " ")
    }
}