package org.gabrieal.pokedex.helpers.util


fun String.toSentenceCase(): String { // capitalize the first word
    return this.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
}