package com.example.prem.videoapp.ui.modelview

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.*
import com.airbnb.epoxy.*
import com.example.prem.videoapp.R
import com.example.prem.videoapp.data.local.Video
import com.squareup.picasso.Picasso
import org.jetbrains.anko.find

@ModelView(autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
class VideoThumbModelView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

    var video_thumbnail: ImageView
    var video_title: TextView
    var video_description: TextView

    init {
        View.inflate(context, R.layout.item_video_thumbnail, this)
        video_thumbnail = find(R.id.video_thumbnail)
        video_title = find(R.id.video_title)
        video_description = find(R.id.video_description)
    }

    @ModelProp
    fun withVideo(video: Video) {
        Picasso.get().load(video.thumb).into(video_thumbnail)
        video_title.text = video.title
        video_description.text = video.description
    }
}