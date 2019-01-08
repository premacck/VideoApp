package com.example.prem.videoapp.ui.modelview

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.*
import com.airbnb.epoxy.*
import com.example.prem.videoapp.R
import com.example.prem.videoapp.util.setMargin
import org.jetbrains.anko.find

@ModelView(autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
class TextModelView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

    lateinit var text_view: TextView

    init {
        View.inflate(context, R.layout.item_text_view, this)
        text_view = find(R.id.text_view)
    }

    @ModelProp(options = [ModelProp.Option.GenerateStringOverloads])
    fun withText(charSequence: CharSequence?) {
        text_view.text = charSequence
    }

    @ModelProp
    fun withVerticalMargins(margin: Int) {
        text_view.setMargin(null, margin, null, margin)
    }

    @ModelProp
    fun withHorizontalMargins(margin: Int) {
        text_view.setMargin(margin, null, margin, null)
    }
}
