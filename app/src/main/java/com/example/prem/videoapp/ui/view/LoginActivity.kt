package com.example.prem.videoapp.ui.view

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.util.Log
import com.example.prem.videoapp.R
import com.example.prem.videoapp.base.BaseActivity
import com.example.prem.videoapp.data.local.onUserLoggedIn
import com.example.prem.videoapp.util.*
import com.google.android.gms.common.Scopes.*
import com.jaychang.sa.*
import com.jaychang.sa.google.SimpleAuth
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.sdk27.coroutines.onClick

class LoginActivity : BaseActivity() {

    private lateinit var anim: AnimationDrawable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initBackgroundAnim()
        setListeners()
    }

    override fun onResume() {
        super.onResume()
        anim.begin()
    }

    override fun onPause() {
        super.onPause()
        anim.pause()
    }

    private fun initBackgroundAnim() {
        anim = (root_layout.background as AnimationDrawable).apply {
            setEnterFadeDuration(2000)
            setExitFadeDuration(4000)
        }
    }

    private fun setListeners() {
        google_login.onClick {
            SimpleAuth.connectGoogle(arrayListOf(EMAIL, PROFILE), object :
                AuthCallback {
                override fun onSuccess(socialUser: SocialUser?) {
                    socialUser?.run {
                        onUserLoggedIn(this)
                        startActivity(intentFor<HomeActivity>())
                        finish()
                    }
                }

                override fun onCancel() {}

                override fun onError(error: Throwable?) {
                    error?.run {
                        Log.e("connectGoogle", message, this)
                        shoErrorAlertDialog(message ?: getString(R.string.something_is_not_right))
                    }
                }
            })
        }
    }
}