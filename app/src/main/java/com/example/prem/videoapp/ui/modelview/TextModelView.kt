package com.example.prem.videoapp.ui.modelview

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.airbnb.epoxy.ModelProp
import com.airbnb.epoxy.ModelView
import com.example.prem.videoapp.R
import com.example.prem.videoapp.util.getDp
import com.example.prem.videoapp.util.setMargin
import kotlinx.android.synthetic.main.item_text_view.view.*

@ModelView(autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
class TextModelView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

    init {
        View.inflate(context, R.layout.item_text_view, this)
    }

    @ModelProp(options = [ModelProp.Option.GenerateStringOverloads])
    fun withText(charSequence: CharSequence?) {
        text_view.text = charSequence
    }

    @ModelProp
    fun withVerticalMargins(margin: Int) {
        text_view.setMargin(null, getDp(margin.toFloat()).toInt(), null, getDp(margin.toFloat()).toInt())
    }

    @ModelProp
    fun withHorizontalMargins(margin: Int) {
        text_view.setMargin(getDp(margin.toFloat()).toInt(), null, getDp(margin.toFloat()).toInt(), null)
    }
}
