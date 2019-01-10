package com.example.prem.videoapp.ui.view

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.ContextThemeWrapper
import com.example.prem.videoapp.R
import com.example.prem.videoapp.data.local.getUser
import com.example.prem.videoapp.data.local.logoutUser
import com.example.prem.videoapp.util.onDebouncingClick
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.dialog_profile.*
import org.jetbrains.anko.intentFor

class ProfileDialog(context: Context) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_profile)
        window?.setBackgroundDrawableResource(android.R.color.transparent)

        updateUi()
        name_text_view.text
    }

    private fun updateUi() {
        val user = context.getUser()

        Picasso.get().load(user.profilePictureUrl).into(profile_picture)
        name_text_view.text = user.fullName
        email_text_view.text = user.email

        logout_btn.onDebouncingClick {
            context.logoutUser()
            ((context as ContextThemeWrapper).baseContext as Activity).run {
                startActivity(intentFor<LoginActivity>())
                finish()
            }
        }
    }
}