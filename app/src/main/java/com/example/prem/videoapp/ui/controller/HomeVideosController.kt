package com.example.prem.videoapp.ui.controller

import com.airbnb.epoxy.AutoModel
import com.example.prem.videoapp.base.BaseActivity
import com.example.prem.videoapp.data.local.Video
import com.example.prem.videoapp.ui.controller.base.EpoxyController3
import com.example.prem.videoapp.ui.modelview.TextModelViewModel_
import com.example.prem.videoapp.ui.modelview.VideoThumbModelViewModel_
import com.example.prem.videoapp.ui.view.DetailActivity

class HomeVideosController(private val activity: BaseActivity) : EpoxyController3<ArrayList<Video>, Boolean, String>() {

    @AutoModel
    lateinit var textModelView: TextModelViewModel_

    override fun buildModels(videosList: ArrayList<Video>?, isError: Boolean, errorMessage: String?) {
        textModelView
            .withText(errorMessage)
            .withVerticalMargins(100)
            .addIf((videosList.isNullOrEmpty() || isError) && errorMessage.isNullOrEmpty(), this)

        videosList?.forEach { video ->
            VideoThumbModelViewModel_()
                .id(video.id)
                .withVideo(video)
                .onClick { DetailActivity.launch(activity, videosList, video) }
                .addTo(this)
        }
    }
}