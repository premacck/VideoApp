package com.example.prem.videoapp.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.SpannableString
import android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.annotation.ColorRes
import com.example.prem.videoapp.R
import org.jetbrains.anko.toast
import java.util.regex.Pattern
import java.util.regex.Pattern.*

const val URL_REGEX =
    ("(?:^|[\\W])((ht|f)tp(s?):\\/\\/|www\\.)(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*$~@!:/{};']*)")
val URL_PATTERN: Pattern = Pattern.compile(URL_REGEX, CASE_INSENSITIVE or MULTILINE or DOTALL)

fun SpannableString.color(context: Context, @ColorRes colorRes: Int, start: Int = 0, end: Int = length): SpannableString {
    setSpan(ForegroundColorSpan(context.findColor(colorRes)), start, end, SPAN_EXCLUSIVE_EXCLUSIVE)
    return this
}

fun CharSequence.formatLinks(activity: Activity): SpannableString {
    val formattedString = if (this is SpannableString) this else SpannableString(this)
    val matcher = URL_PATTERN.matcher(this)
    while (matcher.find()) {
        formattedString.toClickableWebLink(activity, matcher.start(), matcher.end())
    }
    return formattedString
}

fun CharSequence.toClickableWebLink(activity: Activity, start: Int = 0, end: Int = length): SpannableString {
    val spannableString: SpannableString = if (this is SpannableString) this else SpannableString(this)
    spannableString.clickAction(start, end) {
        activity.run {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(substring(start, end).replace("\n","")))
            if ((intent.resolveActivity(packageManager) != null)) {
                startActivity(browserIntent)
            } else toast("No App found to open web links!")
        }
    }.color(activity, R.color.dark_sky_blue, start, end)
    return spannableString
}

fun SpannableString.clickAction(start: Int = 0, end: Int = length, action: () -> Unit): SpannableString {
    setSpan(object : ClickableSpan() {
        override fun onClick(widget: View) {
            action()
        }

        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.isUnderlineText = true
        }
    }, start, end, SPAN_EXCLUSIVE_EXCLUSIVE)
    return this
}
