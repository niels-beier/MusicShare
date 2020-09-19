package de.shuzo.musicshare

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log

class MusicPlayer(private val context: Context) : MediaPlayer() {

    private val tag = "MusicShare/MusicPlayer"

    private val mediaPlayer = MediaPlayer()

    fun startPlaying(source: Uri) {
        Log.d(tag, "startPlaying: source: $source")
        mediaPlayer.reset()
        mediaPlayer.setDataSource(context, source)
        mediaPlayer.prepare()
        mediaPlayer.start()
    }

    override fun stop() {
        mediaPlayer.stop()
    }

    override fun pause() {
        mediaPlayer.pause()
    }

    override fun start() {
        mediaPlayer.start()
    }
}