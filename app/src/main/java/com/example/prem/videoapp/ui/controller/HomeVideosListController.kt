package com.example.prem.videoapp.ui.controller

import com.example.prem.videoapp.data.local.Video
import com.example.prem.videoapp.ui.controller.base.EpoxyController3

class HomeVideosListController : EpoxyController3<List<Video>, Boolean, String>() {

    override fun buildModels(videosList: List<Video>?, isDataAvailable: Boolean?, errorMessage: String?) {

    }
}