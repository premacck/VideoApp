package com.example.prem.videoapp.data.remote

import com.example.prem.videoapp.data.local.Video
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.GET

interface RestApi {

    @GET("media.json")
    fun getVideos(): Observable<Response<ArrayList<Video>>>
}