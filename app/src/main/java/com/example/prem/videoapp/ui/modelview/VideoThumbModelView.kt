package com.example.prem.videoapp.ui.modelview

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.RelativeLayout
import com.airbnb.epoxy.ModelProp
import com.airbnb.epoxy.ModelView
import com.example.prem.videoapp.R
import com.example.prem.videoapp.data.local.Video
import com.example.prem.videoapp.ui.view.HomeActivity.Companion.screenSize
import com.example.prem.videoapp.util.onDebouncingClick
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
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
        Picasso.get().load(video.thumb).into(object : Target {
            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
            override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {}
            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                bitmap?.run {
                    val width = screenSize[0]
                    val params = video_thumbnail.layoutParams as RelativeLayout.LayoutParams
                    params.height = width * bitmap.height / bitmap.width
                    video_thumbnail.layoutParams = params
                    video_thumbnail.setImageBitmap(bitmap)
                }
            }
        })
        video_title.text = video.title
        video_description.text = video.description
    }

    @ModelProp(options = [ModelProp.Option.DoNotHash])
    fun onClick(action: () -> Unit) = root_layout.onDebouncingClick(action)
}