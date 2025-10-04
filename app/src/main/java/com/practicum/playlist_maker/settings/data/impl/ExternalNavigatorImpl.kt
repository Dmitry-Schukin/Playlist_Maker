package com.practicum.playlist_maker.settings.data.impl

import android.content.Context
import com.practicum.playlist_maker.settings.domain.api.ExternalNavigator
import com.practicum.playlist_maker.settings.domain.model.EmailData
import com.practicum.playlist_maker.R

class ExternalNavigatorImpl(private val context: Context): ExternalNavigator {
    override fun shareLink(): String {
        return context.getString(R.string.url_practicum_course)
    }

    override fun openEmail(): EmailData {
        return EmailData(
            context.getString(R.string.my_mail),
                context.getString(R.string.mail_subject),
                    context.getString(R.string.mail_text)
        )
    }

    override fun openLink(): String {
        return context.getString(R.string.url_practicum_offer)
    }

}