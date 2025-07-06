package org.gabrieal.pokedex.feature.pokedex.view

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch
import org.gabrieal.pokedex.BaseActivity
import org.gabrieal.pokedex.R
import org.gabrieal.pokedex.data.model.PokemonDetail
import org.gabrieal.pokedex.databinding.ActivityPokedexBinding
import org.gabrieal.pokedex.feature.pokedex.viewmodel.PokeDexViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PokeDexActivity : BaseActivity() {
    private val viewModel: PokeDexViewModel by viewModel()
    private var binding: ActivityPokedexBinding? = null

    private var selectedPokemon: PokemonDetail? = null

    private var pokeDexAdapter = PokeDexAdapter(null, object : PokeDexAdapter.OnItemClickListener {
        override fun onPokemonClick(name: String) {
            binding?.lottieAnimation?.visibility = View.VISIBLE
            binding?.ivPokedex?.visibility = View.GONE
            viewModel.getPokemonByName(name)
        }
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onBindData() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_pokedex)
        binding?.lifecycleOwner = this
    }

    override fun setupUI() {
        setupInsets(binding?.llRoot)
        setupRecyclerView()

        binding?.btnNormal?.setOnClickListener {
            if(selectedPokemon == null) {
                return@setOnClickListener
            }

            loadPokemonSprite(selectedPokemon?.sprites?.frontDefault)
        }

        binding?.btnShiny?.setOnClickListener {
            if(selectedPokemon == null) {
                return@setOnClickListener
            }

            loadPokemonSprite(selectedPokemon?.sprites?.frontShiny)
        }
    }

    private fun setupRecyclerView() {
        binding?.rvPokedex?.layoutManager = LinearLayoutManager(this)
        binding?.rvPokedex?.adapter = pokeDexAdapter
    }

    override fun setupViewModel() {
        binding?.lottieAnimation?.visibility = View.VISIBLE
        binding?.ivPokedex?.visibility = View.GONE
        viewModel.loadPokemonList()
    }

    override fun observeResponses() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.pokemonState.collect { pokemonList ->
                        pokeDexAdapter.setPokemonList(pokemonList?.results)
                    }
                }

                launch {
                    viewModel.pokemonDetailState.collect { pokemonDetail ->
                        pokemonDetail?.let {
                            binding?.lottieAnimation?.visibility = View.GONE
                            binding?.ivPokedex?.visibility = View.VISIBLE

                            selectedPokemon = pokemonDetail
                            loadPokemonSprite(pokemonDetail.sprites?.frontDefault)
                        }
                    }
                }
            }
        }
    }

    private fun loadPokemonSprite(frontDefault: String?) {
        binding?.ivPokedex?.let {
            Glide.with(this)
                .load(frontDefault)
                .into(it)
        }
    }
}