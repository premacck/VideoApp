package com.example.prem.videoapp.util

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.ColorRes
import androidx.appcompat.app.AlertDialog
import com.example.prem.videoapp.VideoApp
import com.example.prem.videoapp.base.BaseActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import org.jetbrains.anko.runOnUiThread

@Suppress("DeferredResultUnused")
fun Context?.doAfterDelay(delayMillis: Long, action: () -> Unit) {
    GlobalScope.async {
        delay(delayMillis)
        this@doAfterDelay?.run { runOnUiThread { action() } }
    }
}

fun BaseActivity.setStatusBarColor(color: Int) {
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    window.statusBarColor = color
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

@Suppress("DEPRECATION")
fun Context.findColor(@ColorRes colorRes: Int) = resources.getColor(colorRes)

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

fun BaseActivity.getVideoApplication() = application as VideoApp