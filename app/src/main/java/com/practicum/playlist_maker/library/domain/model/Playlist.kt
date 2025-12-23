package com.practicum.playlist_maker.library.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class Playlist(
    val playlistId: Long,
    val playlistName: String,
    val playlistDescription: String,
    val imagePath: String,
    val trackIdList: List<String>,
    val trackCount: Int
): Parcelable {
    companion object{
        fun addTrackToPlaylist(playlist: Playlist, trackId: String): Playlist{
            val newList = playlist.trackIdList.toMutableList()
            newList.add(trackId)
            var newCount = playlist.trackCount
            newCount++
            val newPlaylist = Playlist(
                playlist.playlistId,
                playlist.playlistName,
                playlist.playlistDescription,
                playlist.imagePath,
                newList,
                newCount
            )
            return newPlaylist
        }
    }


}