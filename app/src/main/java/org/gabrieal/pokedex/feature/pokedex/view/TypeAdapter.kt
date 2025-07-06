package org.gabrieal.pokedex.feature.pokedex.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import org.gabrieal.pokedex.R
import org.gabrieal.pokedex.data.model.TypeSlot
import org.gabrieal.pokedex.databinding.PokemonTypeItemBinding
import org.gabrieal.pokedex.helpers.enums.PokemonType

class TypeAdapter(
    private var typeList: List<TypeSlot>? = null
) : RecyclerView.Adapter<TypeAdapter.TypeViewHolder>() {

    fun setPokemonType(types: List<TypeSlot>?) {
        typeList = types
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TypeViewHolder {
        val binding = PokemonTypeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TypeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TypeViewHolder, position: Int) {
        val typeSlot = typeList?.getOrNull(position) ?: return
        val context = holder.itemView.context

        val typeName = typeSlot.type?.name.orEmpty()
        val colorRes = PokemonType.fromName(typeName)?.color ?: R.color.black

        with(holder.binding) {
            tvPokemonType.text = typeName.uppercase()
            cvPokemonType.setCardBackgroundColor(ContextCompat.getColor(context, colorRes))
        }
    }

    override fun getItemCount(): Int = typeList?.size ?: 0

    inner class TypeViewHolder(val binding: PokemonTypeItemBinding) :
        RecyclerView.ViewHolder(binding.root)
}