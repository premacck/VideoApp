package com.example.prem.videoapp.ui.controller

import com.example.prem.videoapp.base.BaseActivity
import com.example.prem.videoapp.data.local.Video
import com.example.prem.videoapp.ui.controller.base.EpoxyController
import com.example.prem.videoapp.ui.modelview.NextVideoModelViewModel_
import com.example.prem.videoapp.ui.view.DetailActivity

class NextVideosListController(private val activity: BaseActivity) : EpoxyController<ArrayList<Video>>() {

    override fun buildModels(videosList: ArrayList<Video>?) {
        videosList?.forEach { video ->
            NextVideoModelViewModel_()
                .id(video.id)
                .withVideo(video)
                .onClick { DetailActivity.launch(activity, videosList, video) }
                .addTo(this)
        }
    }
}