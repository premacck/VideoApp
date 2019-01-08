package com.example.prem.videoapp.ui.view

import android.os.Bundle
import com.android.volley.VolleyError
import com.example.prem.videoapp.R
import com.example.prem.videoapp.base.BaseActivity
import com.example.prem.videoapp.data.local.Video
import com.example.prem.videoapp.presenter.HomeActivityPresenter

class HomeActivity : BaseActivity() {

    private lateinit var presenter: HomeActivityPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        presenter = HomeActivityPresenter.getInstance(this)

        presenter.getVideos(this::handleResponse, this::handleError)
    }

    private fun handleResponse(videoList: List<Video>) {
    }

    private fun handleError(error: VolleyError) {

    }

    private fun updateUi() {

    }
}
