package com.practicum.playlist_maker.settings.domain.impl

import com.practicum.playlist_maker.settings.domain.api.ExternalNavigator
import com.practicum.playlist_maker.settings.domain.api.SharingInteractor
import com.practicum.playlist_maker.settings.domain.model.EmailData

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator,
) : SharingInteractor {
    override fun shareApp():String {
        return externalNavigator.shareLink()
    }
    override fun openSupport(): EmailData {
        return externalNavigator.openEmail()
    }
    override fun openTerms(): String {
        return externalNavigator.openLink()
    }

}