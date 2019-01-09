package com.example.prem.videoapp.util

import android.animation.*
import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.view.View
import androidx.annotation.AnimatorRes

fun AnimationDrawable.begin() {
    if (!isRunning) start()
}

fun AnimationDrawable.pause() {
    if (isRunning) stop()
}

fun Context.animatorOf(@AnimatorRes animatorRes: Int): Animator = AnimatorInflater.loadAnimator(this, animatorRes)

fun View.animatorOf(@AnimatorRes animatorRes: Int): Animator {
    val animator = context.animatorOf(animatorRes)
    animator.setTarget(this)
    return animator
}
