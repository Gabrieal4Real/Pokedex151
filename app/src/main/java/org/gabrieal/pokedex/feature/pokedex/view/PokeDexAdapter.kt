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

    private var pokemonList = emptyList<NamedResource>()
    private var caughtList = emptySet<NamedResource>()
    private var selectedPokemon: NamedResource? = null

    fun setPokemonList(list: List<NamedResource>) {
        pokemonList = list
        notifyDataSetChanged()
    }

    fun setCaughtList(list: Set<NamedResource>) {
        caughtList = list
        caughtList.forEach { notifyItemChanged(pokemonList.indexOf(it)) }
    }

    fun setSelected(pokemon: NamedResource) {
        val prev = pokemonList.indexOf(selectedPokemon)
        val curr = pokemonList.indexOf(pokemon)
        selectedPokemon = pokemon
        if (prev != -1) notifyItemChanged(prev)
        if (curr != -1) notifyItemChanged(curr)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        PokeDexViewHolder(
            PokedexItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: PokeDexViewHolder, position: Int) {
        val pokemon = pokemonList[position]
        val binding = holder.binding

        with(binding) {
            tvPokemonName.text = pokemon.name?.toSentenceCase()

            resetView(ivPokemonCaught, binding)

            if (pokemon == selectedPokemon) {
                startShaking(ivPokemonCaught)
                root.setBackgroundColor(ContextCompat.getColor(root.context, R.color.pokedex_selected_blue))
                ivSelectedArrow.visibility = View.VISIBLE
            }

            ivPokemonCaught.setImageResource(
                if (pokemon in caughtList) R.drawable.pokeball else R.drawable.pokeball_not
            )

            ivPokemonCaught.setOnClickListener {
                if (pokemon != selectedPokemon) {
                    root.performClick()
                } else {
                    onItemClickListener.catchingPokemon(pokemon)
                }
            }

            root.setOnClickListener {
                if (pokemon != selectedPokemon) {
                    setSelected(pokemon)
                    pokemon.name?.let(onItemClickListener::onPokemonClick)
                }
            }

            root.setOnLongClickListener {
                onItemClickListener.catchingPokemon(pokemon)
                true
            }
        }
    }

    override fun getItemCount() = pokemonList.size

    private fun resetView(iv: ImageView, binding: PokedexItemBinding) {
        binding.root.setBackgroundColor(Color.TRANSPARENT)
        binding.ivSelectedArrow.visibility = View.GONE

        (iv.getTag(R.id.iv_pokemon_caught_tag) as? AnimatorSet)?.cancel()
        iv.setTag(R.id.iv_pokemon_caught_tag, null)
        iv.rotation = 0f
        iv.translationX = 0f
    }

    private fun startShaking(view: ImageView) {
        (view.getTag(R.id.iv_pokemon_caught_tag) as? AnimatorSet)?.cancel()

        val rotation = ObjectAnimator.ofFloat(view, "rotation", -15f, 15f).apply {
            duration = 500
            repeatMode = ObjectAnimator.REVERSE
            repeatCount = ObjectAnimator.INFINITE
        }

        val translation = ObjectAnimator.ofFloat(view, "translationX", -5f, 0f, 5f).apply {
            duration = 500
            repeatMode = ObjectAnimator.REVERSE
            repeatCount = ObjectAnimator.INFINITE
        }

        AnimatorSet().apply {
            playTogether(rotation, translation)
            start()
            view.setTag(R.id.iv_pokemon_caught_tag, this)
        }
    }

    inner class PokeDexViewHolder(val binding: PokedexItemBinding) : RecyclerView.ViewHolder(binding.root)

    interface OnItemClickListener {
        fun onPokemonClick(name: String)
        fun catchingPokemon(name: NamedResource)
    }
}