package com.example.prem.videoapp.presenter.home

import android.content.Context
import com.android.volley.*
import com.android.volley.toolbox.*
import com.example.prem.videoapp.VideoApp.Companion.GSON
import com.example.prem.videoapp.data.local.Video
import com.google.gson.reflect.TypeToken
import org.jetbrains.anko.*

class HomeActivityPresenter constructor(context: Context) {

    private val listener: HomePresenterListener by lazy {
        (context as? HomePresenterListener)
            ?: throw RuntimeException(context.toString() + " must implement HomePresenterListener")
    }
    private val requestQueue: RequestQueue by lazy {
        Volley.newRequestQueue(context.applicationContext)
    }

    companion object {
        @Volatile
        private var INSTANCE: HomeActivityPresenter? = null

        fun getInstance(context: Context) = INSTANCE ?: synchronized(this) {
            INSTANCE ?: HomeActivityPresenter(context).also {
                INSTANCE = it
            }
        }
    }

    fun getVideos() {
        listener.onRequestStarted()
        doAsync {
            val videosRequest = JsonArrayRequest(Request.Method.GET,
                "https://interview-e18de.firebaseio.com/media.json",
                null,
                Response.Listener { response ->
                    uiThread {
                        listener.onRequestDone()
                        listener.handleResponse(
                            GSON.fromJson(
                                response.toString(),
                                object : TypeToken<List<Video>>() {}.type
                            )
                        )
                    }
                },
                Response.ErrorListener { error ->
                    uiThread {
                        listener.onRequestDone()
                        listener.handleError(error)
                    }
                })
            addToRequestQueue(videosRequest)
        }
    }

    private fun <T> addToRequestQueue(req: Request<T>) {
        requestQueue.add(req)
    }
}