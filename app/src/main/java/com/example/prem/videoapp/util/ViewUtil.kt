package com.example.prem.videoapp.util

import android.animation.ValueAnimator
import android.content.Context
import android.util.TypedValue
import android.view.*
import android.view.MotionEvent.*
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.app.AlertDialog
import com.example.prem.videoapp.R
import kotlinx.coroutines.*
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.sdk27.coroutines.onTouch

@Suppress("DeferredResultUnused")
fun Context?.doAfterDelay(delayMillis: Long, action: () -> Unit) {
    GlobalScope.async {
        delay(delayMillis)
        this@doAfterDelay?.run { runOnUiThread { action() } }
    }
}

fun View.setMargin(left: Int? = null, top: Int? = null, right: Int? = null, bottom: Int? = null) {
    layoutParams = (layoutParams as? ViewGroup.MarginLayoutParams)?.apply {
        left?.let { leftMargin = getDp(it.toFloat()).toInt(); }
        top?.let { topMargin = getDp(it.toFloat()).toInt() }
        right?.let { rightMargin = getDp(it.toFloat()).toInt() }
        bottom?.let { bottomMargin = getDp(it.toFloat()).toInt() }
    }
}

fun Context.getDp(dp: Float): Float = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics)

fun View.getDp(dp: Float): Float = context.getDp(dp)

fun View.makeVisible() {
    if (visibility != View.VISIBLE) visibility = View.VISIBLE
}

fun View.makeInvisible() {
    if (visibility != View.INVISIBLE) visibility = View.INVISIBLE
}

fun View.makeGone() {
    if (visibility != View.GONE) visibility = View.GONE
}

fun Context.shoErrorAlertDialog(errorMessage: String) {
    AlertDialog.Builder(this)
        .setTitle("Error!")
        .setMessage(errorMessage)
        .setCancelable(true)
        .setPositiveButton("Okay") { dialog, _ -> dialog.dismiss() }
        .show()
}

fun View.onDebouncingClick(action: () -> Unit) {
    setOnClickListener {
        if (isEnabled) {
            isEnabled = false
            action()
            postDelayed({ isEnabled = true }, 100)
        }
    }
}

fun View.onReducingClick(launchDelay: Long = 100, action: () -> Unit) {
    var originPoint: ArrayList<Float> = ArrayList()
    onTouch { _, event ->
        when (event.action) {
            ACTION_DOWN -> {
                originPoint = arrayListOf(event.rawX, event.rawY)
                animatorOf(R.animator.reduce_size).start()
            }
            ACTION_CANCEL, ACTION_UP -> animatorOf(R.animator.original_size).start()
            ACTION_MOVE -> {
                try {
                    if (originPoint.isEmpty()) originPoint = arrayListOf(event.rawX, event.rawY)

                    if (!originPoint.isEmpty()) {
                        val deltaX = Math.abs(originPoint[0] - event.rawX)
                        val deltaY = Math.abs(originPoint[1] - event.rawY)
                        if (deltaX > 1 && deltaY > 1) {
                            animatorOf(R.animator.original_size).start()
                        }
                    }
                } catch (e: Exception) {
                    animatorOf(R.animator.original_size).start()
                }
            }
        }
    }
    onDebouncingClick { this.context.doAfterDelay(launchDelay) { action() } }
}

fun View.onElevatingClick(launchDelay: Long = 100, elevateBy: Float = 8f, action: () -> Unit) {
    lateinit var originPoint: ArrayList<Float>
    val originalElevation = elevation
    val finalElevation = elevation + getDp(elevateBy)
    val elevateUpAnimation = ValueAnimator.ofFloat(originalElevation, finalElevation).setDuration(80)
    elevateUpAnimation.interpolator = DecelerateInterpolator()
    elevateUpAnimation.addUpdateListener { elevation = it.animatedValue as Float }

    val elevateDownAnimation = ValueAnimator.ofFloat(finalElevation, originalElevation).setDuration(100)
    elevateDownAnimation.addUpdateListener { elevation = it.animatedValue as Float }
    elevateDownAnimation.interpolator = DecelerateInterpolator()
    elevateDownAnimation.startDelay = 80
    onTouch { _, event ->
        when (event.action) {
            ACTION_DOWN -> {
                originPoint = arrayListOf(event.rawX, event.rawY)
                elevateUpAnimation.start()
            }
            ACTION_CANCEL, ACTION_UP -> elevateDownAnimation.start()
            ACTION_MOVE -> {
                if (originPoint.isEmpty()) originPoint = arrayListOf(event.rawX, event.rawY)

                if (!originPoint.isEmpty()) {
                    val deltaX = Math.abs(originPoint[0] - event.rawX)
                    val deltaY = Math.abs(originPoint[1] - event.rawY)
                    if (deltaX > 1 && deltaY > 1) {
                        elevateDownAnimation.start()
                    }
                }
            }
        }
    }
    onDebouncingClick { this.context.doAfterDelay(launchDelay) { action() } }
}
