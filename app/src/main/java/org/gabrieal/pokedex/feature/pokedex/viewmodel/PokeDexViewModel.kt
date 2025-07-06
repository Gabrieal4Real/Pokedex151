package org.gabrieal.pokedex.feature.pokedex.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.gabrieal.pokedex.data.model.PokemonDetail
import org.gabrieal.pokedex.data.model.PokemonList
import org.gabrieal.pokedex.feature.pokedex.repo.PokeDexRepo

class PokeDexViewModel(private val repo: PokeDexRepo) : ViewModel() {

    private val _pokemonState = MutableStateFlow<PokemonList?>(null)
    val pokemonState: StateFlow<PokemonList?> = _pokemonState

    private val _pokemonDetailState = MutableStateFlow<PokemonDetail?>(null)
    val pokemonDetailState: StateFlow<PokemonDetail?> = _pokemonDetailState

    fun loadPokemonList() = viewModelScope.launch {
        runCatching {
            repo.getPokemons().collect { _pokemonState.value = it }
        }.onFailure {
            Log.e("PokeDexVM", "Error loading Pokémon list", it)
        }
    }

    fun getPokemonByName(name: String) = viewModelScope.launch {
        runCatching {
            repo.getPokemonByName(name).collect { detail ->
                fetchAndAttachDescription(name, detail)
            }
        }.onFailure {
            Log.e("PokeDexVM", "Error loading Pokémon detail: $name", it)
        }
    }

    private fun fetchAndAttachDescription(name: String, detail: PokemonDetail) = viewModelScope.launch {
        runCatching {
            repo.getPokemonDescription(name).collect { species ->
                val rawText = species.flavorTextEntries
                    ?.firstOrNull { it.language.name == "en" }
                    ?.flavorText.orEmpty()

                detail.description = cleanDescription(rawText)
                _pokemonDetailState.value = detail
            }
        }.onFailure {
            Log.e("PokeDexVM", "Error fetching description for $name", it)
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