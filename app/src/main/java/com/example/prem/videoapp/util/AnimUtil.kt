package com.example.prem.videoapp.util

import android.graphics.drawable.AnimationDrawable

fun AnimationDrawable.begin() {
    if (!isRunning) start()
}

fun AnimationDrawable.pause() {
    if (isRunning) stop()
}