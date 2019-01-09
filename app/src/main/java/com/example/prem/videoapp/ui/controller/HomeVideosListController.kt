package com.example.prem.videoapp.ui.controller

import com.airbnb.epoxy.AutoModel
import com.example.prem.videoapp.data.local.Video
import com.example.prem.videoapp.ui.controller.base.EpoxyController3
import com.example.prem.videoapp.ui.modelview.TextModelViewModel_
import com.example.prem.videoapp.ui.modelview.VideoThumbModelViewModel_

class HomeVideosListController : EpoxyController3<List<Video>, Boolean, String>() {

    @AutoModel
    lateinit var textModelView: TextModelViewModel_

    override fun buildModels(videosList: List<Video>?, isError: Boolean, errorMessage: String?) {
        textModelView.withText(errorMessage)
            .withVerticalMargins(100)
            .addIf((videosList.isNullOrEmpty() || isError) && errorMessage.isNullOrEmpty(), this)

        videosList?.forEach { video ->
            VideoThumbModelViewModel_()
                .id(video.id)
                .withVideo(video)
                .addTo(this)
        }
    }
}