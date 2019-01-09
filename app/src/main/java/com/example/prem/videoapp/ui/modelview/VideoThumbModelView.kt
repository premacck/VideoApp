package com.example.prem.videoapp.ui.modelview

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.airbnb.epoxy.ModelProp
import com.airbnb.epoxy.ModelView
import com.example.prem.videoapp.R
import com.example.prem.videoapp.data.local.Video
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_video_thumbnail.view.*

@ModelView(autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
class VideoThumbModelView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

    init {
        View.inflate(context, R.layout.item_video_thumbnail, this)
    }

    @ModelProp
    fun withVideo(video: Video) {
        Picasso.get().load(video.thumb).into(video_thumbnail)
        video_title.text = video.title
        video_description.text = video.description
    }
}