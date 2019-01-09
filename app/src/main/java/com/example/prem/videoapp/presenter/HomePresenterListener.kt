package com.example.prem.videoapp.presenter

import com.android.volley.VolleyError
import com.example.prem.videoapp.data.local.Video

interface HomePresenterListener {

    fun onRequestStarted()

    fun onRequestDone()

    fun handleResponse(videoList: List<Video>)

    fun handleError(error: VolleyError)
}