package org.gabrieal.pokedex.feature.pokedex.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.gabrieal.pokedex.data.model.PokemonResponse
import org.gabrieal.pokedex.feature.pokedex.repo.PokeDexRepo

class PokeDexViewModel(private val pokeDexRepo: PokeDexRepo) : ViewModel() {

    private val _pokemonState = MutableStateFlow<PokemonResponse?>(null)
    val pokemonState: StateFlow<PokemonResponse?> = _pokemonState

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
}