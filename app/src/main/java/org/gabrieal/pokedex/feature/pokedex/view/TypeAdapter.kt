package org.gabrieal.pokedex.feature.pokedex.view

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import org.gabrieal.pokedex.R
import org.gabrieal.pokedex.data.model.NamedResource
import org.gabrieal.pokedex.data.model.TypeSlot
import org.gabrieal.pokedex.helpers.enums.PokemonType
import org.gabrieal.pokedex.helpers.util.toSentenceCase

class TypeAdapter(
    private var pokemonType: List<TypeSlot>?,
) : RecyclerView.Adapter<TypeAdapter.PokeDexViewHolder>() {


    fun setPokemonType(pokemonType: List<TypeSlot>?) {
        this.pokemonType = pokemonType
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PokeDexViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.pokemon_type_item, parent, false
        )

        return PokeDexViewHolder(view)
    }

    override fun onBindViewHolder(holder: PokeDexViewHolder, position: Int) {
        val pokemonType = pokemonType?.get(position)
        holder.cvPokemonType.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, PokemonType.fromName(pokemonType?.type?.name ?: "")?.color ?: R.color.black))
        holder.tvPokemonType.text = pokemonType?.type?.name?.uppercase()
    }

    override fun getItemCount(): Int {
        return pokemonType?.size ?: 0
    }

    inner class PokeDexViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val cvPokemonType: CardView = itemView.findViewById(R.id.cv_pokemon_type)
        val tvPokemonType: TextView = itemView.findViewById(R.id.tv_pokemon_type)
    }
}