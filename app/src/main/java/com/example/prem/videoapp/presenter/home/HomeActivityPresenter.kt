package com.example.prem.videoapp.presenter.home

import android.content.Context
import com.example.prem.videoapp.presenter.BasePresenter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.net.HttpURLConnection.HTTP_OK

class HomeActivityPresenter private constructor(context: Context) : BasePresenter {

    private val listener: HomePresenterListener by lazy {
        (context as? HomePresenterListener)
            ?: throw RuntimeException(context.toString() + " must implement HomePresenterListener")
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

    fun getVideos(isRefreshing: Boolean): Disposable = listener.restApi().getVideos()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnSubscribe { if (!isRefreshing) listener.onRequestStarted() }
        .doOnTerminate { listener.onRequestDone() }
        .subscribe({ response ->
            when (response.code()) {
                HTTP_OK -> response.body()?.run { listener.handleResponse(this) } ?: listener.handleError(response)
                else -> listener.handleError(response)
            }
        }, { error -> listener.handleError(error) })

    override fun dispose() {
        INSTANCE = null
    }
}