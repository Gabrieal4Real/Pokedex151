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
import org.gabrieal.pokedex.helpers.util.toSentenceCase
import org.koin.androidx.viewmodel.ext.android.viewModel

class PokeDexActivity : BaseActivity() {
    private val viewModel: PokeDexViewModel by viewModel()
    private lateinit var binding: ActivityPokedexBinding

    private var velocityTracker: VelocityTracker? = null
    private var lastX = 0f
    private var lastY = 0f

    private val clickSoundMP: MediaPlayer by lazy { MediaPlayer.create(this, R.raw.click_sound) }
    private val successSoundMP: MediaPlayer by lazy { MediaPlayer.create(this, R.raw.pokeball_success) }
    private var pokemonDetails: Pair<NamedResource, Int>? = null

    private val pokeDexAdapter = PokeDexAdapter(object : PokeDexAdapter.OnItemClickListener {
        override fun onPokemonClick(name: String) {
            playSound(clickSoundMP)
            viewModel.getPokemonByName(name)
            binding.lottieAnimation.visibility = View.VISIBLE
            binding.llPokedex.visibility = View.GONE
        }

        override fun catchingPokemon(name: NamedResource, position: Int) {
            pokemonDetails = name to position
            viewModel.setCatchMode(true)
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

        btnAllPokedex.root.setOnClickListener { pokeDexAdapter.setFilter(Filter.ALL) }
        btnCaughtPokedex.root.setOnClickListener { pokeDexAdapter.setFilter(Filter.CAUGHT) }
        btnMissingPokedex.root.setOnClickListener { pokeDexAdapter.setFilter(Filter.MISSING) }

        btnNormal.setOnClickListener {
            playSound(clickSoundMP)
            loadPokemonSprite(viewModel.pokemonDetailState.value?.sprites?.frontDefault)
        }

        btnShiny.setOnClickListener {
            playSound(clickSoundMP)
            loadPokemonSprite(viewModel.pokemonDetailState.value?.sprites?.frontShiny)
        }

        btnPokeballThrow.setOnClickListener {
            viewModel.setCatchMode(false)
            pokemonDetails?.let { pokeDexAdapter.toggleCaughtStatus(it.first, it.second) }
        }

        flPokeballThrow.setOnTouchListener { _, event -> handleTouch(event) }

        getSystemBarHeights(root) { statusBar, navBar ->
            llRoot.setPadding(0, statusBar, 0, 0)
            llPhysicalButtons.setPadding(32, 48, 32, 32 + navBar)
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
                    viewModel.pokemonState.collect { it?.results?.let(pokeDexAdapter::setPokemonList) }
                }

                launch {
                    viewModel.pokemonDetailState.collect { it?.let(::showPokemonDetail) }
                }

                launch {
                    viewModel.isCatchMode.collect(::toggleCatchModeVisibility)
                }
            }
        }
    }

    private fun toggleCatchModeVisibility(isCatchMode: Boolean) = with(binding) {
        ivPokeballThrow.visibility = if (isCatchMode) View.VISIBLE else View.GONE
        flPokemonCatch.visibility = if (isCatchMode) View.VISIBLE else View.GONE
        flPokeballThrow.visibility = if (isCatchMode) View.VISIBLE else View.GONE
        rvPokedex.visibility = if(!isCatchMode) View.VISIBLE else View.GONE

        if (isCatchMode) {
            Toast.makeText(this@PokeDexActivity, "GET READY TO CATCH!", Toast.LENGTH_SHORT).show()
            lottieAnimation.visibility = View.GONE
            llPokedex.visibility = View.GONE
            llBottomButtons.visibility = View.GONE
        } else {
            tvPokeballThrow.visibility = View.GONE
            btnPokeballThrow.visibility = View.GONE
            llPokedex.visibility = if (viewModel.pokemonDetailState.value == null) View.GONE else View.VISIBLE
            llBottomButtons.visibility = if (viewModel.pokemonDetailState.value == null) View.GONE else View.VISIBLE
            lottieAnimation.visibility = if (viewModel.pokemonDetailState.value == null) View.VISIBLE else View.GONE
        }
    }

    private fun showPokemonDetail(detail: PokemonDetail) = with(binding) {
        toggleCatchModeVisibility(viewModel.isCatchMode.value)
        typeAdapter.setPokemonType(detail.types)
        tvPokedexName.text = detail.name?.toSentenceCase()
        tvPokedexDescription.text = detail.description
        tvPokedexAbilities.text =
            detail.abilities?.joinToString("\n") { "• ${it.ability?.name?.toSentenceCase()}" }

        loadPokemonSprite(detail.sprites?.frontDefault)
    }

    private fun loadPokemonSprite(url: String?) {
        Glide.with(this).load(url).into(binding.ivPokedex)
        Glide.with(this).load(url).into(binding.ivPokemonCatch)
    }

    private fun playSound(mediaPlayer: MediaPlayer) {
        mediaPlayer.setVolume(0.05f, 0.05f)
        mediaPlayer.seekTo(0)
        mediaPlayer.start()
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
        if (ivPokeballThrow.y + ivPokeballThrow.translationY < flPokeballThrow.top) {
            ivPokeballThrow.visibility = View.GONE
            launchBallIntoTopScreen()
        } else {
            ivPokeballThrow.animate().translationX(0f).translationY(0f).setDuration(300).start()
        }
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

    private val Int.dp: Int
        get() = (this * resources.displayMetrics.density).toInt()

    override fun onDestroy() {
        super.onDestroy()
        clickSoundMP.release()
        successSoundMP.release()
    }
}