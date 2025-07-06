package org.gabrieal.pokedex.feature.pokedex.view

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import org.gabrieal.pokedex.R
import org.gabrieal.pokedex.data.model.NamedResource
import org.gabrieal.pokedex.databinding.PokedexItemBinding
import org.gabrieal.pokedex.helpers.enums.Filter
import org.gabrieal.pokedex.helpers.util.toSentenceCase

class PokeDexAdapter(
    private val onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<PokeDexAdapter.PokeDexViewHolder>() {

    private var fullList: List<NamedResource> = emptyList()
    private var filteredList: List<NamedResource> = emptyList()
    private var caughtList: MutableSet<NamedResource> = mutableSetOf()
    private var selectedPokemon: NamedResource? = null

    fun setPokemonList(list: List<NamedResource>) {
        fullList = list
        filteredList = list
        notifyDataSetChanged()
    }

    fun setSelected(pokemon: NamedResource) {
        val previous = filteredList.indexOf(selectedPokemon)
        val current = filteredList.indexOf(pokemon)
        selectedPokemon = pokemon
        notifyItemChanged(previous)
        notifyItemChanged(current)
    }

    fun setFilter(filter: Filter) {
        filteredList = when (filter) {
            Filter.ALL -> fullList
            Filter.CAUGHT -> fullList.filter { it in caughtList }
            Filter.MISSING -> fullList.filter { it !in caughtList }
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokeDexViewHolder {
        val binding = PokedexItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PokeDexViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PokeDexViewHolder, position: Int) {
        val pokemon = filteredList[position]

        with(holder.binding) {
            tvPokemonName.text = pokemon.name?.toSentenceCase()

            root.setBackgroundColor(
                if (pokemon == selectedPokemon)
                    ContextCompat.getColor(root.context, R.color.pokedex_selected_blue)
                else Color.TRANSPARENT
            )

            ivPokemonCaught.setImageResource(
                if (pokemon in caughtList) R.drawable.pokeball else R.drawable.pokeball_not
            )

            ivPokemonCaught.setOnClickListener {
                toggleCaughtStatus(pokemon, position)
            }

            root.setOnClickListener {
                setSelected(pokemon)
                pokemon.name?.let { name -> onItemClickListener.onPokemonClick(name) }
            }
        }
    }

    override fun getItemCount(): Int = filteredList.size

    private fun toggleCaughtStatus(pokemon: NamedResource, position: Int) {
        if (!caughtList.add(pokemon)) caughtList.remove(pokemon)
        notifyItemChanged(position)
    }

    inner class PokeDexViewHolder(val binding: PokedexItemBinding) : RecyclerView.ViewHolder(binding.root)

    interface OnItemClickListener {
        fun onPokemonClick(name: String)
    }
}