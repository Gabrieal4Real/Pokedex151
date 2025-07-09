package org.gabrieal.pokedex.feature.pokedex.view

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import org.gabrieal.pokedex.R
import org.gabrieal.pokedex.data.model.NamedResource
import org.gabrieal.pokedex.databinding.PokedexItemBinding
import org.gabrieal.pokedex.helpers.util.toSentenceCase

class PokeDexAdapter(
    private val onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<PokeDexAdapter.PokeDexViewHolder>() {

    private var pokemonList: List<NamedResource> = emptyList()
    private var caughtList: Set<NamedResource> = emptySet()
    private var selectedPokemon: NamedResource? = null
    private var shakeAnimatorSet: AnimatorSet? = null

    fun setPokemonList(list: List<NamedResource>) {
        pokemonList = list
        notifyDataSetChanged()
    }

    fun setCaughtList(list: Set<NamedResource>) {
        caughtList = list
        notifyDataSetChanged()
    }

    fun setSelected(pokemon: NamedResource) {
        val previous = pokemonList.indexOf(selectedPokemon)
        val current = pokemonList.indexOf(pokemon)
        selectedPokemon = pokemon
        notifyItemChanged(previous)
        notifyItemChanged(current)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokeDexViewHolder {
        val binding = PokedexItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PokeDexViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PokeDexViewHolder, position: Int) {
        val pokemon = pokemonList[position]

        with(holder.binding) {
            tvPokemonName.text = pokemon.name?.toSentenceCase()

            root.setBackgroundColor(Color.TRANSPARENT)
            ivSelectedArrow.visibility = View.GONE
            ivPokemonCaught.rotation = 0f

            if (pokemon == selectedPokemon) {
                shakeAnimatorSet?.cancel()
                if (pokemon !in caughtList) startShaking(ivPokemonCaught)

                root.setBackgroundColor(
                    ContextCompat.getColor(
                        root.context,
                        R.color.pokedex_selected_blue
                    )
                )
                ivSelectedArrow.visibility = View.VISIBLE
            }

            ivPokemonCaught.setImageResource(
                if (pokemon in caughtList) R.drawable.pokeball else R.drawable.pokeball_not
            )

            ivPokemonCaught.setOnClickListener {
                if (pokemon != selectedPokemon) {
                    root.performClick()
                    return@setOnClickListener
                }
                onItemClickListener.catchingPokemon(pokemon)
            }

            root.setOnClickListener {
                if (pokemon == selectedPokemon) return@setOnClickListener
                setSelected(pokemon)
                pokemon.name?.let { name -> onItemClickListener.onPokemonClick(name) }
            }

            root.setOnLongClickListener {
                onItemClickListener.catchingPokemon(pokemon)
                return@setOnLongClickListener true
            }
        }
    }

    override fun getItemCount(): Int = pokemonList.size

    private fun startShaking(ivPokemonCaught: ImageView) {
        val rotation = ObjectAnimator.ofFloat(ivPokemonCaught, "rotation", -15f, 15f).apply {
            duration = 500
            repeatMode = ObjectAnimator.REVERSE
            repeatCount = ObjectAnimator.INFINITE
        }

        val translationX =
            ObjectAnimator.ofFloat(ivPokemonCaught, "translationX", -5f, 0f, 5f).apply {
                duration = 500
                repeatMode = ObjectAnimator.REVERSE
                repeatCount = ObjectAnimator.INFINITE
            }

        shakeAnimatorSet = AnimatorSet().apply {
            playTogether(rotation, translationX)
            start()
        }
    }

    inner class PokeDexViewHolder(val binding: PokedexItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface OnItemClickListener {
        fun onPokemonClick(name: String)
        fun catchingPokemon(name: NamedResource)
    }
}