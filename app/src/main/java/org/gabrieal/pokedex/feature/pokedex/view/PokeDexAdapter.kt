package org.gabrieal.pokedex.feature.pokedex.view

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import org.gabrieal.pokedex.R
import org.gabrieal.pokedex.data.model.NamedResource
import org.gabrieal.pokedex.util.toSentenceCase

class PokeDexAdapter(private var pokemonList: List<NamedResource>? = null, private val onItemClickListener: OnItemClickListener) : RecyclerView.Adapter<PokeDexAdapter.PokeDexViewHolder>() {

    private var selectedPosition: Int = -1

    fun setPokemonList(pokemonList: List<NamedResource>?) {
        this.pokemonList = pokemonList
        notifyDataSetChanged()
    }

    fun setSelectedPosition(position: Int) {
        val prevPosition = selectedPosition
        selectedPosition = position

        notifyItemChanged(prevPosition)
        notifyItemChanged(position)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PokeDexViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.pokedex_item, parent, false
        )

        return PokeDexViewHolder(view)
    }

    override fun onBindViewHolder(holder: PokeDexViewHolder, position: Int) {
        val pokemon = pokemonList?.get(position)
        holder.tvPokemonName.text = pokemon?.name?.toSentenceCase()

        holder.itemView.setBackgroundColor(Color.TRANSPARENT)

        if (selectedPosition == position) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.pokedex_selected_blue))
        }

        holder.itemView.setOnClickListener {
            pokemon?.name?.let { name ->
                setSelectedPosition(position)
                onItemClickListener.onPokemonClick(name)
            }
        }
    }

    override fun getItemCount(): Int {
        return pokemonList?.size ?: 0
    }

    inner class PokeDexViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvPokemonName: TextView = itemView.findViewById(R.id.tv_pokemon_name)
    }

    interface OnItemClickListener {
        fun onPokemonClick(name: String)
    }
}