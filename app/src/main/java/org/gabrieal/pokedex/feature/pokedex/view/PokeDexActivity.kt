package org.gabrieal.pokedex.feature.pokedex.view

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import org.gabrieal.pokedex.BaseActivity
import org.gabrieal.pokedex.R
import org.gabrieal.pokedex.databinding.ActivityPokedexBinding
import org.gabrieal.pokedex.feature.pokedex.viewmodel.PokeDexViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PokeDexActivity : BaseActivity() {
    private val viewModel: PokeDexViewModel by viewModel()

    private var binding: ActivityPokedexBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onBindData() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_pokedex)
        binding?.lifecycleOwner = this
    }

    override fun setupUI() {
        setupInsets(binding?.llRoot)
    }

    override fun setupViewModel() {
        viewModel.loadPokemonList()
    }

    override fun observeResponses() {

    }
}