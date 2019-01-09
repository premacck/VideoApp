package com.example.prem.videoapp.ui.view

import android.os.Bundle
import com.example.prem.videoapp.base.BaseActivity
import com.example.prem.videoapp.data.local.isUserLoggedIn
import org.jetbrains.anko.intentFor

class SplashScreenActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (isUserLoggedIn()) {
            startActivity(intentFor<HomeActivity>())
        } else {
            startActivity(intentFor<LoginActivity>())
        }
        finish()
    }
}