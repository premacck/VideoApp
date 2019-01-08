package com.example.prem.videoapp

import android.app.Application
import com.google.gson.Gson

class VideoApp : Application() {

    companion object {
        var GSON: Gson = Gson()
    }
}