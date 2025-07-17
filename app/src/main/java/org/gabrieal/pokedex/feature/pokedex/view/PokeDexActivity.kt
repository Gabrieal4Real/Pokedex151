package org.gabrieal.pokedex.feature.pokedex.view

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.animation.BounceInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch
import org.gabrieal.pokedex.BaseActivity
import org.gabrieal.pokedex.R
import org.gabrieal.pokedex.data.model.NamedResource
import org.gabrieal.pokedex.data.model.PokemonDetail
import org.gabrieal.pokedex.databinding.ActivityPokedexBinding
import org.gabrieal.pokedex.feature.pokedex.viewmodel.PokeDexViewModel
import org.gabrieal.pokedex.helpers.enums.Filter
import org.gabrieal.pokedex.helpers.util.MusicPlayerUtil
import org.gabrieal.pokedex.helpers.util.collectFieldIn
import org.gabrieal.pokedex.helpers.util.dp
import org.gabrieal.pokedex.helpers.util.toSentenceCase
import org.koin.androidx.viewmodel.ext.android.viewModel

class PokeDexActivity : BaseActivity() {
    private val viewModel: PokeDexViewModel by viewModel()
    private lateinit var binding: ActivityPokedexBinding

    private var velocityTracker: VelocityTracker? = null
    private var lastX = 0f
    private var lastY = 0f

    private val clickSoundMP: MediaPlayer by lazy {
        MusicPlayerUtil.create(this, R.raw.click_sound)
    }
    private val successSoundMP: MediaPlayer by lazy {
        MusicPlayerUtil.create(this, R.raw.pokeball_success)
    }

    private val pokeDexAdapter = PokeDexAdapter(object : PokeDexAdapter.OnItemClickListener {
        override fun onPokemonClick(name: String) {
            binding.lottieAnimation.visibility = View.VISIBLE
            binding.llPokedex.visibility = View.GONE

            playSound(clickSoundMP)
            viewModel.getPokemonByName(name)
        }

        override fun catchingPokemon(name: NamedResource) {
            viewModel.setCatchingPokemon(name)
        }
    })

    private val typeAdapter = TypeAdapter(null)

    override fun onBindData() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_pokedex)
        binding.lifecycleOwner = this
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun setupUI() = with(binding) {
        rvPokedex.layoutManager = LinearLayoutManager(this@PokeDexActivity)
        rvPokedex.adapter = pokeDexAdapter

        rvPokedexTypes.layoutManager = LinearLayoutManager(this@PokeDexActivity)
        rvPokedexTypes.adapter = typeAdapter

        btnAllPokedex.tvButtonText.text = Filter.ALL.displayName
        btnCaughtPokedex.tvButtonText.text = Filter.CAUGHT.displayName
        btnMissingPokedex.tvButtonText.text = Filter.MISSING.displayName

        btnAllPokedex.root.setOnClickListener { viewModel.setFilter(Filter.ALL) }
        btnCaughtPokedex.root.setOnClickListener { viewModel.setFilter(Filter.CAUGHT) }
        btnMissingPokedex.root.setOnClickListener { viewModel.setFilter(Filter.MISSING) }

        btnNormal.setOnClickListener {
            playSound(clickSoundMP)
            loadPokemonSprite(viewModel.uiState.value.pokemonDetail?.sprites?.frontDefault)
        }

        btnShiny.setOnClickListener {
            playSound(clickSoundMP)
            loadPokemonSprite(viewModel.uiState.value.pokemonDetail?.sprites?.frontShiny)
        }

        btnPokeballThrow.setOnClickListener {
            ivPokeballThrow.visibility = View.VISIBLE
            tvPokeballThrow.visibility = View.GONE
            btnPokeballThrow.visibility = View.GONE

            viewModel.uiState.value.catchingPokemon?.let { pokemon ->
                viewModel.toggleCaughtStatus(pokemon)
            }
        }

        flPokeballThrow.setOnTouchListener { _, event -> handleTouch(event) }

        getSystemBarHeights(root) { statusBar, navBar ->
            tvAppName.setPadding(12.dp, 12.dp + statusBar, 12.dp, 12.dp)
            llPhysicalButtons.setPadding(12.dp, 12.dp, 12.dp, 12.dp + navBar)
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
                with(viewModel.uiState) {
                    collectFieldIn(this@repeatOnLifecycle, { it.pokemonDetail }) {
                        it?.let(::showPokemonDetail)
                    }

                    collectFieldIn(this@repeatOnLifecycle, { it.filteredList }) {
                        pokeDexAdapter.setPokemonList(it)
                    }

                    collectFieldIn(this@repeatOnLifecycle, { it.caughtList }) {
                        pokeDexAdapter.setCaughtList(it)
                    }

                    collectFieldIn(this@repeatOnLifecycle, { it.isCatchMode to it.pokemonDetail }) {
                        toggleCatchModeVisibility(it.first, it.second)
                    }
                }
            }
        }
    }

    private fun toggleCatchModeVisibility(isCatchMode: Boolean, detail: PokemonDetail?) =
        with(binding) {
            if (isCatchMode) {
                rlTopScreen.visibility = View.GONE
                rlBottomScreen.visibility = View.GONE
                flPokemonCatch.visibility = View.VISIBLE
                flPokeballThrow.visibility = View.VISIBLE

                Toast.makeText(this@PokeDexActivity, "GET READY TO CATCH!", Toast.LENGTH_SHORT)
                    .show()
                return
            }
            llPokedex.visibility = View.GONE
            llBottomButtons.visibility = View.GONE
            lottieAnimation.visibility = View.VISIBLE

            detail?.let {
                llPokedex.visibility = View.VISIBLE
                llBottomButtons.visibility = View.VISIBLE
                lottieAnimation.visibility = View.GONE
            }

            flPokemonCatch.visibility = View.GONE
            flPokeballThrow.visibility = View.GONE

            rlTopScreen.visibility = View.VISIBLE
            rlBottomScreen.visibility = View.VISIBLE
        }

    private fun showPokemonDetail(detail: PokemonDetail) = with(binding) {
        typeAdapter.setPokemonType(detail.types)
        tvPokedexName.text = detail.name?.toSentenceCase()
        tvPokedexDescription.text = detail.description
        tvPokedexAbilities.text =
            detail.abilities?.joinToString("\n") { "\u2022 ${it.ability?.name?.toSentenceCase()}" }

        loadPokemonSprite(detail.sprites?.frontDefault)
    }

    private fun loadPokemonSprite(url: String?) = with(binding) {
        Glide.with(this@PokeDexActivity).load(url).into(ivPokedex)
        Glide.with(this@PokeDexActivity).load(url).into(ivPokemonCatch)
    }

    private fun playSound(mediaPlayer: MediaPlayer) {
        MusicPlayerUtil.play(mediaPlayer)
    }

    private fun handleTouch(event: MotionEvent): Boolean = with(binding) {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                velocityTracker = VelocityTracker.obtain()
                velocityTracker?.addMovement(event)
                lastX = event.rawX
                lastY = event.rawY
                true
            }

            MotionEvent.ACTION_MOVE -> {
                velocityTracker?.addMovement(event)
                val dx = event.rawX - lastX
                val dy = event.rawY - lastY
                ivPokeballThrow.translationX += dx
                ivPokeballThrow.translationY += dy
                lastX = event.rawX
                lastY = event.rawY
                true
            }

            MotionEvent.ACTION_UP -> {
                velocityTracker?.apply {
                    addMovement(event)
                    computeCurrentVelocity(1000)
                    handleThrow()
                    recycle()
                }
                velocityTracker = null
                true
            }

            else -> false
        }
    }

    private fun handleThrow() = with(binding) {
        if (ivPokeballThrow.y + ivPokeballThrow.translationY >= flPokeballThrow.top) {
            ivPokeballThrow.animate().translationX(0f).translationY(0f).setDuration(300).start()
            return
        }

        ivPokeballThrow.visibility = View.GONE
        launchBallIntoTopScreen()
    }

    private fun launchBallIntoTopScreen() = with(binding) {
        val newBall = ImageView(this@PokeDexActivity).apply {
            setImageResource(R.drawable.pokeball)
            layoutParams = FrameLayout.LayoutParams(60.dp, 60.dp)
            x = (flPokemonCatch.width - 60.dp) / 2f
            y = flPokemonCatch.height.toFloat()
        }

        flPokemonCatch.addView(newBall)

        val targetX = ivPokemonCatch.x + ivPokemonCatch.width / 4f - newBall.width
        val targetY = ivPokemonCatch.y + ivPokemonCatch.height / 4f - newBall.height

        newBall.animate()
            .x(targetX)
            .y(targetY)
            .setDuration(800)
            .setInterpolator(BounceInterpolator())
            .withEndAction {
                playImpactEffectAt(targetX, targetY, newBall)
                ivPokeballThrow.translationX = 0f
                ivPokeballThrow.translationY = 0f
                playSound(successSoundMP)
            }
            .start()
    }

    private fun playImpactEffectAt(x: Float, y: Float, newBall: ImageView) = with(binding) {
        val impact = ImageView(this@PokeDexActivity).apply {
            setImageResource(R.drawable.smoke)
            layoutParams = FrameLayout.LayoutParams(80.dp, 80.dp)
            this.x = x
            this.y = y
        }

        flPokemonCatch.addView(impact)
        impact.animate()
            .alpha(0f)
            .scaleX(4f)
            .scaleY(4f)
            .setDuration(2000)
            .withStartAction {
                ivPokemonCatch.visibility = View.GONE
            }
            .withEndAction {
                flPokemonCatch.removeView(impact)
                flPokemonCatch.removeView(newBall)
                ivPokemonCatch.visibility = View.VISIBLE
                tvPokeballThrow.visibility = View.VISIBLE
                btnPokeballThrow.visibility = View.VISIBLE
            }
            .start()
    }

    override fun onDestroy() {
        super.onDestroy()
        clickSoundMP.release()
        successSoundMP.release()
    }
}