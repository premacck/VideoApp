package com.example.prem.videoapp.presenter.home

import android.content.Context
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.net.HttpURLConnection.HTTP_OK

class HomeActivityPresenter constructor(context: Context) {

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

    fun getVideos(): Disposable = listener.restApi().getVideos()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnSubscribe { listener.onRequestStarted() }
        .doOnTerminate { listener.onRequestDone() }
        .subscribe({ response ->
            when (response.code()) {
                HTTP_OK -> response.body()?.run { listener.handleResponse(this) } ?: listener.handleError(response)
                else -> listener.handleError(response)
            }
        }, { error -> listener.handleError(error) })

    fun dispose() {
        INSTANCE = null
    }
}