package org.gabrieal.pokedex.feature.pokedex.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.gabrieal.pokedex.data.model.PokemonDetail
import org.gabrieal.pokedex.data.model.PokemonList
import org.gabrieal.pokedex.feature.pokedex.repo.PokeDexRepo
import org.gabrieal.pokedex.feature.pokedex.viewmodel.PokeDexUiState

class PokeDexViewModel(private val repo: PokeDexRepo) : ViewModel() {

    private val _uiState = MutableStateFlow(PokeDexUiState())
    val uiState: StateFlow<PokeDexUiState> = _uiState

    fun setCatchMode(catchMode: Boolean) {
        _uiState.value = _uiState.value.copy(isCatchMode = catchMode)
    }

    fun loadPokemonList() = viewModelScope.launch {
        runCatching {
            repo.getPokemons().collect {
                _uiState.value = _uiState.value.copy(pokemonList = it)
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