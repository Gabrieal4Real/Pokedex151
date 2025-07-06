package org.gabrieal.pokedex.feature.pokedex.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.gabrieal.pokedex.data.model.PokemonDetail
import org.gabrieal.pokedex.data.model.PokemonList
import org.gabrieal.pokedex.feature.pokedex.repo.PokeDexRepo

class PokeDexViewModel(private val pokeDexRepo: PokeDexRepo) : ViewModel() {

    private val _pokemonState = MutableStateFlow<PokemonList?>(null)
    val pokemonState: StateFlow<PokemonList?> = _pokemonState

    private val _pokemonDetailState = MutableStateFlow<PokemonDetail?>(null)
    val pokemonDetailState: StateFlow<PokemonDetail?> = _pokemonDetailState

    fun loadPokemonList() {
        viewModelScope.launch {
            try {
                pokeDexRepo.getPokemons().collect {
                    _pokemonState.value = it
                }
            } catch (e: Exception) {
                Log.e("PokeViewModel", "Failed to fetch pokemons", e)
            }
        }
    }

    fun getPokemonByName(name: String) {
        viewModelScope.launch {
            try {
                pokeDexRepo.getPokemonByName(name).collect {
                    getPokemonDescription(name, it)
                }
            } catch (e: Exception) {
                Log.e("PokeViewModel", "Failed to fetch pokemon", e)
            }
        }
    }

    fun getPokemonDescription(name: String, pokemonDetail: PokemonDetail) {
        viewModelScope.launch {
            try {
                pokeDexRepo.getPokemonDescription(name).collect { it ->
                    val cleanedText = it.flavorTextEntries?.first { it.language.name == "en" }?.flavorText
                        ?.replace("\u000C", "")
                        ?.replace("\u00AD\n", "")
                        ?.replace("\u00AD", "")
                        ?.replace(" -\n", " - ")
                        ?.replace("-\n", "-")
                        ?.replace("\n", " ")

                    pokemonDetail.description = cleanedText
                    _pokemonDetailState.value = pokemonDetail
                }
            } catch (e: Exception) {
                Log.e("PokeViewModel", "Failed to fetch description", e)
            }
        }
    }
}