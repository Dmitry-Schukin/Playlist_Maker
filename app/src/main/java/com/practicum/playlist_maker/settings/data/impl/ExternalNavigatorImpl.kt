package com.practicum.playlist_maker.settings.data.impl

import com.practicum.playlist_maker.settings.domain.api.ExternalNavigator
import com.practicum.playlist_maker.settings.domain.model.EmailData
import com.practicum.playlist_maker.R
import com.practicum.playlist_maker.creator.Creator

class ExternalNavigatorImpl: ExternalNavigator {
    override fun shareLink(): String {
        return Creator.context.getString(R.string.url_practicum_course)
    }

    override fun openEmail(): EmailData {
        return EmailData(
            Creator.context.getString(R.string.my_mail),
                Creator.context.getString(R.string.mail_subject),
                    Creator.context.getString(R.string.mail_text)
        )
    }

    override fun openLink(): String {
        return Creator.context.getString(R.string.url_practicum_offer)
    }

}