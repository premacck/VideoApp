package com.example.prem.videoapp.data.local

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Video(
    var id: Long,
    var description: String,
    var title: String,
    var thumb: String,
    var url: String
) : Parcelable