package com.practicum.playlist_maker.library.ui.fragments

sealed interface FragmentState{
    data class Content(
        val list: List<Any>
    ) : FragmentState

    data class Error(
        val errorMessage: String
    ) : FragmentState

    data class Empty(
        val message: String
    ) : FragmentState
}