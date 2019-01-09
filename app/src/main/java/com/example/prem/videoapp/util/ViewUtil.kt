package com.example.prem.videoapp.util

import android.content.Context
import android.util.TypedValue
import android.view.*

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
