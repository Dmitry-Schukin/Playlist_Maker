package com.practicum.playlist_maker.settings.data.impl

import com.practicum.playlist_maker.settings.domain.api.ExternalNavigator
import com.practicum.playlist_maker.settings.domain.model.EmailData

class ExternalNavigatorImpl: ExternalNavigator {
    override fun shareLink(): String {
        return "https://practicum.yandex.ru/android-developer/?from=catalog"
    }

    override fun openEmail(): EmailData {
        return EmailData(
            "iliakirsanov5321@gmail.com",
            "Сообщение разработчикам и разработчицам приложения Playlist Maker",
            "Спасибо разработчикам и разработчицам за крутое приложение!"
        )
    }

    override fun openLink(): String {
        return "https://yandex.ru/legal/practicum_offer"
    }

}