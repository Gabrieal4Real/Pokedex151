package org.gabrieal.pokedex.util


fun String.toSentenceCase(): String { // capitalize the first word
    return this.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
}