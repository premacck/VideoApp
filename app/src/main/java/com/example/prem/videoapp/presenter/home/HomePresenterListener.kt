package com.example.prem.videoapp.presenter.home

import com.android.volley.VolleyError
import com.example.prem.videoapp.data.local.Video

interface HomePresenterListener {

    fun onRequestStarted()

    fun onRequestDone()

    fun handleResponse(videoList: ArrayList<Video>)

    fun handleError(error: VolleyError)
}