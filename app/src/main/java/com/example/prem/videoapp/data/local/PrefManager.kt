package com.example.prem.videoapp.data.local

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.example.prem.videoapp.R
import com.jaychang.sa.SocialUser

private const val USER_ID = "userId"
private const val ACCESS_TOKEN = "accessToken"
private const val PROFILE_PICTURE_URL = "profilePictureUrl"
private const val USER_NAME = "username"
private const val FULL_NAME = "fullName"
private const val EMAIL = "email"
private const val VIDEO_PLAYBACK_TIME = "videoPlaybackTime_"

private fun Context.getSharedPrefs(): SharedPreferences =
    getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE)

fun Context.onUserLoggedIn(socialUser: SocialUser) {
    getSharedPrefs().edit()
        .putString(USER_ID, socialUser.userId)
        .putString(ACCESS_TOKEN, socialUser.accessToken)
        .putString(PROFILE_PICTURE_URL, socialUser.profilePictureUrl)
        .putString(FULL_NAME, socialUser.fullName)
        .putString(EMAIL, socialUser.email)
        .apply()
}

fun Context.getUser(): SocialUser {
    val sharedPrefs = getSharedPrefs()
    return SocialUser().apply {
        userId = sharedPrefs.getString(USER_ID, null)
        accessToken = sharedPrefs.getString(ACCESS_TOKEN, null)
        profilePictureUrl = sharedPrefs.getString(PROFILE_PICTURE_URL, null)
        fullName = sharedPrefs.getString(FULL_NAME, null)
        email = sharedPrefs.getString(EMAIL, null)
    }
}

fun Context.isUserLoggedIn(): Boolean = !getSharedPrefs().getString(ACCESS_TOKEN, null).isNullOrEmpty()

fun Context.logoutUser() {
    getSharedPrefs().edit()
        .remove(USER_ID)
        .remove(ACCESS_TOKEN)
        .remove(PROFILE_PICTURE_URL)
        .remove(USER_NAME)
        .remove(FULL_NAME)
        .remove(EMAIL)
        .apply()
}

fun Context.saveVideoPlayBackTime(id: String, currentPosition: Long) =
    getSharedPrefs().edit().putLong(VIDEO_PLAYBACK_TIME + id, currentPosition).apply()

fun Context.getSavedVideoPlaybackTime(id: String): Long = getSharedPrefs().getLong(VIDEO_PLAYBACK_TIME + id, 0)