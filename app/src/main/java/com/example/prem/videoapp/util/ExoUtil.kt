package com.example.prem.videoapp.util

import android.content.Context
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import com.example.prem.videoapp.R
import com.example.prem.videoapp.base.BaseActivity
import com.example.prem.videoapp.data.local.getSavedVideoPlaybackTime
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util

fun Player.isPlaying() = playWhenReady

fun Player.play() {
    playWhenReady = true
}

fun Player.pause(): Long {
    playWhenReady = false
    return currentPosition
}

fun Player.toggle() {
    playWhenReady = !playWhenReady
}

fun SimpleExoPlayer.addPlayPauseListener(
    playerView: PlayerView?,
    playPauseBtn: View? = null,
    loadingProgressBar: ProgressBar? = null,
    onEndedAction: () -> Unit = {}
): SimpleExoPlayer {
    addListener(object : Player.EventListener {
        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            val active = playbackState > Player.STATE_IDLE && playbackState < Player.STATE_ENDED
            playerView?.keepScreenOn = active

            when (playbackState) {
                Player.STATE_IDLE -> playPauseBtn?.makeVisible()
                Player.STATE_BUFFERING -> {
                    playPauseBtn?.makeGone()
                    loadingProgressBar?.makeVisible()
                }
                Player.STATE_READY -> {
                    if (playWhenReady) {
                        playPauseBtn?.makeGone()
                    } else playPauseBtn?.makeVisible()
                    loadingProgressBar?.makeGone()
                }
                Player.STATE_ENDED -> onEndedAction()
            }
        }
    })
    return this
}

fun Context.getExoPlayer(): SimpleExoPlayer = ExoPlayerFactory.newSimpleInstance(this, DefaultTrackSelector())

fun SimpleExoPlayer.withExtractorMediaSource(context: Context, videoUrl: String): SimpleExoPlayer {
    prepare(
        ExtractorMediaSource.Factory(
            DefaultDataSourceFactory(context, Util.getUserAgent(context, context.getString(R.string.app_name)))
        ).createMediaSource(Uri.parse(videoUrl))
    )
    return this
}

fun SimpleExoPlayer.resolveCurrentPosition(activity: BaseActivity, id: Long): SimpleExoPlayer {
    seekTo(activity.getSavedVideoPlaybackTime(id.toString()))
    return this
}

fun SimpleExoPlayer.withContainer(container: ViewGroup?): SimpleExoPlayer {
    container?.onDebouncingClick { toggle() }
    return this
}

fun SimpleExoPlayer.applyTo(playerView: PlayerView): SimpleExoPlayer {
    playerView.player = this
    return this
}
