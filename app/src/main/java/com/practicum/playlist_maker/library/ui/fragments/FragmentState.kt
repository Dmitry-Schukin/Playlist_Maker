package com.practicum.playlist_maker.library.ui.fragments

import com.practicum.playlist_maker.search.domain.model.Track

sealed interface FragmentState{
    data class Content(
        val list: List<Track>
    ) : FragmentState
    data class Empty(
        val message: String
    ) : FragmentState
}