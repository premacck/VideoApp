package com.example.prem.videoapp.ui.view

import android.os.Bundle
import com.example.prem.videoapp.R
import com.example.prem.videoapp.base.BaseActivity
import com.example.prem.videoapp.data.local.Video
import com.example.prem.videoapp.data.local.getUserProfilePicture
import com.example.prem.videoapp.data.remote.RestApi
import com.example.prem.videoapp.presenter.home.HomeActivityPresenter
import com.example.prem.videoapp.presenter.home.HomePresenterListener
import com.example.prem.videoapp.ui.controller.HomeVideosController
import com.example.prem.videoapp.util.*
import com.squareup.picasso.Picasso
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_home.*
import retrofit2.Response

class HomeActivity : BaseActivity(), HomePresenterListener {

    private lateinit var presenter: HomeActivityPresenter
    private lateinit var videosController: HomeVideosController
    private lateinit var subscription: Disposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setStatusBarColor(findColor(R.color.primary))
        presenter = HomeActivityPresenter.getInstance(this)

        prepareToolbar()
        swipe_refresh_layout.setOnRefreshListener { getVideos(true) }
        initRecyclerView()
        getVideos(false)
    }

    private fun prepareToolbar() {
        Picasso.get().load(getUserProfilePicture()).into(profile_picture)
        profile_picture.onDebouncingClick { ProfileDialog(this).show() }
    }

    private fun getVideos(isRefreshing: Boolean) {
        subscription = presenter.getVideos(isRefreshing)
    }

    private fun initRecyclerView() {
        videosController = HomeVideosController(this)
        videos_list.adapter = videosController.adapter
    }

    override fun restApi(): RestApi = getVideoApplication().restApi

    override fun handleResponse(videoList: ArrayList<Video>) = videosController.setData(videoList, false, null)

    override fun handleError(error: Throwable) =
        videosController.setData(null, true, error.message ?: error.localizedMessage)

    override fun handleError(response: Response<ArrayList<Video>>) =
        videosController.setData(null, true, "Error ${response.code()}: ${response.message() ?: getString(R.string.something_is_not_right)}")

    override fun onRequestStarted() {
        shimmer_layout.startShimmerAnimation()
        shimmer_view.makeVisible()
    }

    override fun onRequestDone() {
        if (swipe_refresh_layout.isRefreshing) swipe_refresh_layout.isRefreshing = false
        shimmer_layout.stopShimmerAnimation()
        shimmer_view.makeGone()
        videos_list.makeVisible()
    }

    override fun onDestroy() {
        presenter.dispose()
        if (!subscription.isDisposed) subscription.dispose()
        super.onDestroy()
    }
}
