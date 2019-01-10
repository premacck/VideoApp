package com.example.prem.videoapp.presenter.home

import com.example.prem.videoapp.data.local.Video
import com.example.prem.videoapp.data.remote.RestApi
import retrofit2.Response

interface HomePresenterListener {

    fun restApi(): RestApi

    fun onRequestStarted()

    fun onRequestDone()

    fun handleResponse(videoList: ArrayList<Video>)

    fun handleError(response: Response<ArrayList<Video>>)

    fun handleError(error: Throwable)
}