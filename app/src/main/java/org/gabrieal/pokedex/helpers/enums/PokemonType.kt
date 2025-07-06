package org.gabrieal.pokedex.helpers.enums

import org.gabrieal.pokedex.R

enum class PokemonType(val displayName: String, val color: Int) {
    NORMAL("Normal", R.color.type_normal),
    FIRE("Fire", R.color.type_fire),
    WATER("Water", R.color.type_water),
    ELECTRIC("Electric", R.color.type_electric),
    GRASS("Grass", R.color.type_grass),
    ICE("Ice", R.color.type_ice),
    FIGHTING("Fighting", R.color.type_fighting),
    POISON("Poison", R.color.type_poison),
    GROUND("Ground", R.color.type_ground),
    FLYING("Flying", R.color.type_flying),
    PSYCHIC("Psychic", R.color.type_psychic),
    BUG("Bug", R.color.type_bug),
    ROCK("Rock", R.color.type_rock),
    GHOST("Ghost", R.color.type_ghost),
    DRAGON("Dragon", R.color.type_dragon),
    DARK("Dark", R.color.type_dark),
    STEEL("Steel", R.color.type_steel),
    FAIRY("Fairy", R.color.type_fairy);

    companion object {
        fun fromName(name: String): PokemonType? {
            return entries.find { it.name.equals(name, ignoreCase = true) }
        }
    }
}
