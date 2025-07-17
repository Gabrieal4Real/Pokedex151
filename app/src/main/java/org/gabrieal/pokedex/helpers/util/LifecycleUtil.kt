package org.gabrieal.pokedex.helpers.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


inline fun <T, R> Flow<T>.collectFieldIn(
    scope: CoroutineScope,
    crossinline selector: (T) -> R,
    crossinline collector: suspend (R) -> Unit
) {
    scope.launch {
        this@collectFieldIn
            .map(selector)
            .distinctUntilChanged()
            .collect { collector(it) }
    }
}