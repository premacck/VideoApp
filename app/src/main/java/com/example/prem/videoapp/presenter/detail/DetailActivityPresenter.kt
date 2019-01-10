package com.example.prem.videoapp.presenter.detail

import com.example.prem.videoapp.base.BaseActivity
import com.example.prem.videoapp.data.local.Video
import com.example.prem.videoapp.presenter.BasePresenter
import com.example.prem.videoapp.util.getExoPlayer
import com.example.prem.videoapp.util.resolveCurrentPosition
import com.example.prem.videoapp.util.withExtractorMediaSource

class DetailActivityPresenter private constructor() : BasePresenter {

    companion object {
        @Volatile
        private var INSTANCE: DetailActivityPresenter? = null

        fun getInstance() = INSTANCE ?: synchronized(this) {
            INSTANCE ?: DetailActivityPresenter().also {
                INSTANCE = it
            }
        }
    }

    fun getExoPlayer(activity: BaseActivity, currentVideo: Video) = activity.getExoPlayer()
        .withExtractorMediaSource(activity, currentVideo.url)
        .resolveCurrentPosition(activity, currentVideo.id)

    override fun dispose() {
        INSTANCE = null
    }
}