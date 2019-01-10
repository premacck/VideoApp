package com.example.prem.videoapp.ui.view

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import androidx.palette.graphics.Palette
import com.example.prem.videoapp.R
import com.example.prem.videoapp.base.BaseActivity
import com.example.prem.videoapp.data.local.Video
import com.example.prem.videoapp.data.local.saveVideoPlayBackTime
import com.example.prem.videoapp.ui.controller.NextVideosController
import com.example.prem.videoapp.util.*
import com.google.android.exoplayer2.SimpleExoPlayer
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_detail.*
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class DetailActivity : BaseActivity() {

    private lateinit var videosList: ArrayList<Video>
    private lateinit var currentVideo: Video
    private lateinit var nextVideoController: NextVideosController
    private var player: SimpleExoPlayer? = null

    companion object {
        fun launch(from: BaseActivity, videosList: ArrayList<Video>, currentVideo: Video) = from.run {
            val intent = Intent(this, DetailActivity::class.java).apply {
                putExtra(getString(R.string.videos), videosList)
                putExtra(getString(R.string.current_video), currentVideo)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
            startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        resolveIntentParams()
        initToolbar()
        initRecyclerView()
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_left_arrow)
            title = null
        }
    }

    override fun onStart() {
        super.onStart()
        updateUi()
    }

    override fun onPause() {
        super.onPause()
        val currentPosition = player?.pause() ?: 0
        saveVideoPlayBackTime(currentVideo.id.toString(), currentPosition)
        if (Build.VERSION.SDK_INT <= 23) releaseExoPlayer()
    }

    override fun onStop() {
        super.onStop()
        if (Build.VERSION.SDK_INT > 23) releaseExoPlayer()
    }

    private fun releaseExoPlayer() {
        player?.release()
        player = null
    }

    private fun initRecyclerView() {
        nextVideoController = NextVideosController(this)
        next_videos_list.setController(nextVideoController)
    }

    private fun resolveIntentParams() {
        intent?.run {
            videosList = getParcelableArrayListExtra<Video>(getString(R.string.videos))
            videosList.shuffle()
            currentVideo = getParcelableExtra(getString(R.string.current_video))
        }
    }

    private fun updateUi() {
        video_loading_progress.makeVisible()
        doAsync {
            videosList.remove(currentVideo)
            val thumb = Picasso.get().load(currentVideo.thumb).get()
            uiThread {
                applyColors(Palette.from(thumb).generate())

                video_loading_progress.makeGone()
                video_player_container.setAspectRatio(thumb.width / thumb.height.toFloat())

                video_title.isSelected = true
                video_title.text = currentVideo.title
                video_description.text = currentVideo.description

                prepareCurrentVideo()
                nextVideoController.setData(videosList)
            }
        }
    }

    private fun applyColors(palette: Palette) {
        val dominantDarkColor = palette.getDarkVibrantColor(findColor(R.color.primary_dark))
        setStatusBarColor(dominantDarkColor)

        toolbar.backgroundColor = dominantDarkColor
        video_player_container.background = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(dominantDarkColor, Color.TRANSPARENT)
        )
    }

    private fun prepareCurrentVideo() {
        player = getExoPlayer()
            .withExtractorMediaSource(this, currentVideo.url)
            .withContainer(video_player_container)
            .applyTo(video_player)
            .resolveCurrentPosition(this, currentVideo.id)
            .addPlayPauseListener(video_player, play_pause, video_loading_progress, this::onCurrentVideoEnded)
    }

    private fun onCurrentVideoEnded() {
        videosList.add(currentVideo)
        launch(this, videosList, videosList[0])
    }
}