package org.gabrieal.pokedex.feature.pokedex.viewmodel

import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.gabrieal.pokedex.data.model.*
import org.gabrieal.pokedex.feature.pokedex.repo.PokeDexRepo
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PokeDexViewModelTest {

    private lateinit var viewModel: PokeDexViewModel
    private lateinit var repo: PokeDexRepo

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repo = mockk()
        viewModel = PokeDexViewModel(repo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadPokemonList should update pokemonState`() = runTest {
        // Given
        val fakeList = PokemonList(results = listOf(NamedResource(name = "bulbasaur")))
        coEvery { repo.getPokemons() } returns flowOf(fakeList)

        // When
        viewModel.loadPokemonList()
        advanceUntilIdle()

        // Then
        assertEquals(fakeList, viewModel.pokemonState.value)
    }

    @Test
    fun `getPokemonByName should update pokemonDetailState with description`() = runTest {
        // Given
        val name = "bulbasaur"
        val detail = PokemonDetail(name = name, sprites = null, types = listOf())
        val description = DescriptionList(
            flavorTextEntries = listOf(
                Description(
                    flavorText = "Seed Pokémon.\nVery cool.",
                    language = NamedResource(name = "en")
                )
            )
        )

        coEvery { repo.getPokemonByName(name) } returns flowOf(detail)
        coEvery { repo.getPokemonDescription(name) } returns flowOf(description)

        // When
        viewModel.getPokemonByName(name)
        advanceUntilIdle()

        // Then
        val result = viewModel.pokemonDetailState.value
        assertNotNull(result)
        assertEquals("Seed Pokémon. Very cool.", result?.description)
    }

    @Test
    fun `getPokemonByName should handle missing description gracefully`() = runTest {
        // Given
        val name = "missingno"
        val detail = PokemonDetail(name = name)
        val description = DescriptionList(flavorTextEntries = emptyList())

        coEvery { repo.getPokemonByName(name) } returns flowOf(detail)
        coEvery { repo.getPokemonDescription(name) } returns flowOf(description)

        // When
        viewModel.getPokemonByName(name)
        advanceUntilIdle()

        // Then
        val result = viewModel.pokemonDetailState.value
        assertNotNull(result)
        assertEquals("", result?.description)
    }
}
