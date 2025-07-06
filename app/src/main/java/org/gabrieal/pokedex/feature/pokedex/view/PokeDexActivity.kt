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
import org.gabrieal.pokedex.helpers.enums.Filter
import org.gabrieal.pokedex.helpers.util.toSentenceCase
import org.koin.androidx.viewmodel.ext.android.viewModel

class PokeDexActivity : BaseActivity() {
    private val viewModel: PokeDexViewModel by viewModel()
    private lateinit var binding: ActivityPokedexBinding

    private var selectedPokemon: PokemonDetail? = null

    private val pokeDexAdapter = PokeDexAdapter(object : PokeDexAdapter.OnItemClickListener {
        override fun onPokemonClick(name: String) {
            binding.lottieAnimation.visibility = View.VISIBLE
            binding.llPokedex.visibility = View.GONE
            viewModel.getPokemonByName(name)
        }
    })

    private val typeAdapter = TypeAdapter(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onBindData() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_pokedex)
        binding.lifecycleOwner = this
    }

    override fun setupUI() {
        setupRecyclerViews()
        setupFilterButtons()
        setupSpriteButtons()

        getSystemBarHeights(binding.root) { statusBar, navBar ->
            binding.llRoot.setPadding(0, statusBar, 0, 0)
            binding.llPhysicalButtons.setPadding(32, 48, 32, 32 + navBar)
        }
    }

    private fun setupRecyclerViews() {
        binding.rvPokedex.apply {
            layoutManager = LinearLayoutManager(this@PokeDexActivity)
            adapter = pokeDexAdapter
        }

        binding.rvPokedexTypes.apply {
            layoutManager = LinearLayoutManager(this@PokeDexActivity)
            adapter = typeAdapter
        }
    }

    private fun setupFilterButtons() = with(binding) {
        btnAllPokedex.tvButtonText.text = Filter.ALL.displayName
        btnCaughtPokedex.tvButtonText.text = Filter.CAUGHT.displayName
        btnMissingPokedex.tvButtonText.text = Filter.MISSING.displayName

        btnAllPokedex.root.setOnClickListener { pokeDexAdapter.setFilter(Filter.ALL) }
        btnCaughtPokedex.root.setOnClickListener { pokeDexAdapter.setFilter(Filter.CAUGHT) }
        btnMissingPokedex.root.setOnClickListener { pokeDexAdapter.setFilter(Filter.MISSING) }
    }

    private fun setupSpriteButtons() = with(binding) {
        btnNormal.setOnClickListener {
            loadPokemonSprite(selectedPokemon?.sprites?.frontDefault)
        }

        btnShiny.setOnClickListener {
            loadPokemonSprite(selectedPokemon?.sprites?.frontShiny)
        }
    }

    override fun setupViewModel() {
        binding.lottieAnimation.visibility = View.VISIBLE
        binding.llPokedex.visibility = View.GONE
        viewModel.loadPokemonList()
    }

    override fun observeResponses() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.pokemonState.collect { response ->
                        response?.results?.let { pokeDexAdapter.setPokemonList(it) }
                    }
                }

                launch {
                    viewModel.pokemonDetailState.collect { detail ->
                        detail?.let { showPokemonDetail(it) }
                    }
                }
            }
        }
    }

    private fun showPokemonDetail(detail: PokemonDetail) = with(binding) {
        selectedPokemon = detail

        lottieAnimation.visibility = View.GONE
        llPokedex.visibility = View.VISIBLE
        llBottomButtons.visibility = View.VISIBLE

        typeAdapter.setPokemonType(detail.types)
        tvPokedexName.text = detail.name?.toSentenceCase()
        tvPokedexDescription.text = detail.description

        loadPokemonSprite(detail.sprites?.frontDefault)
    }

    private fun loadPokemonSprite(url: String?) {
        Glide.with(this).load(url).into(binding.ivPokedex)
    }
}