package org.gabrieal.pokedex.helpers.util

import android.content.Context
import android.media.MediaPlayer

object MusicPlayerUtil {
    fun create(context: Context, resId: Int): MediaPlayer = MediaPlayer.create(context, resId)

    fun play(mediaPlayer: MediaPlayer, volume: Float = 0.05f) {
        mediaPlayer.setVolume(volume, volume)
        mediaPlayer.seekTo(0)
        mediaPlayer.start()
    }

    fun release(mediaPlayer: MediaPlayer?) {
        mediaPlayer?.release()
    }
}
