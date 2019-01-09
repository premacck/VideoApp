package com.example.prem.videoapp.ui.view

import android.os.Bundle
import com.android.volley.VolleyError
import com.example.prem.videoapp.R
import com.example.prem.videoapp.base.BaseActivity
import com.example.prem.videoapp.data.local.Video
import com.example.prem.videoapp.presenter.home.*
import com.example.prem.videoapp.ui.controller.HomeVideosListController
import com.example.prem.videoapp.util.*
import kotlinx.android.synthetic.main.activity_main.*

class HomeActivity : BaseActivity(), HomePresenterListener {

    private lateinit var presenter: HomeActivityPresenter
    private lateinit var videosController: HomeVideosListController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        presenter = HomeActivityPresenter.getInstance(this)

        initRecyclerView()

        presenter.getVideos()
    }

    private fun initRecyclerView() {
        videosController = HomeVideosListController()
        videos_list.setController(videosController)
    }

    override fun handleResponse(videoList: List<Video>) = videosController.setData(videoList, false, null)

    override fun handleError(error: VolleyError) =
        videosController.setData(null, true, error.message ?: error.localizedMessage ?: error.networkResponse.statusCode.toString())

    override fun onRequestStarted() {
        shimmer_layout.startShimmerAnimation()
        shimmer_view.makeVisible()
    }

    override fun onRequestDone() {
        shimmer_layout.stopShimmerAnimation()
        shimmer_view.makeGone()
    }
}
