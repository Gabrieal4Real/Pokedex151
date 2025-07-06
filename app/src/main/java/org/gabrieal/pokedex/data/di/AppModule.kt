package org.gabrieal.pokedex.data.di

import org.gabrieal.pokedex.data.network.APIService
import org.gabrieal.pokedex.data.network.httpClient
import org.gabrieal.pokedex.feature.pokedex.repo.PokeDexRepo
import org.gabrieal.pokedex.feature.pokedex.repo.PokeDexRepoImpl
import org.gabrieal.pokedex.feature.pokedex.viewmodel.PokeDexViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { httpClient }
    single { APIService(get()) }


    // Repo
    single <PokeDexRepo>{ PokeDexRepoImpl(get()) }

    //ViewModel
    viewModel { PokeDexViewModel(get()) }
}