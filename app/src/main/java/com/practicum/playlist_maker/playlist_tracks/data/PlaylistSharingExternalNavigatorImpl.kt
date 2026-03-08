package com.practicum.playlist_maker.playlist_tracks.data

import android.content.Context
import com.practicum.playlist_maker.R
import com.practicum.playlist_maker.library.domain.model.Playlist
import com.practicum.playlist_maker.playlist_tracks.domain.PlaylistSharingExternalNavigator
import com.practicum.playlist_maker.search.domain.model.Track

class PlaylistSharingExternalNavigatorImpl(private val context: Context): PlaylistSharingExternalNavigator {
    override fun sharePlaylist(
        playlist: Playlist,
        tracks: List<Track>
    ): String {
        val trackCount = playlist.trackCount.toString() + " "+ getTrackForms(playlist.trackCount)
        var trackList = ""
        if(tracks.isNotEmpty()) {
            for((index, value) in tracks.withIndex()){
                val trackInString = "\n${index+1}.${value.artistName}-${value.trackName}(${value.trackTimeMillis})"
                trackList=trackList+trackInString
            }
        }
        val playlistInfo = "${playlist.playlistName}\n${playlist.playlistDescription}\n${trackCount}${trackList}"
        return playlistInfo
    }
    fun getTrackForms(number:Int):String{
        val forms = mutableListOf<String>(
            context.getString(R.string.track),
            context.getString(R.string.track_a),
            context.getString(R.string.track_ov))
        val lastDigit = number % 10
        val lastTwoDigits = number % 100
        return when {
            // 1, 21, 31, 41... (кроме 11)
            lastDigit == 1 && lastTwoDigits != 11 -> forms[0] // 1 трек
            // 2, 3, 4, 22, 23, 24... (кроме 12, 13, 14)
            lastDigit in 2..4 && lastTwoDigits !in 12..14 -> forms[1] // 2 трека, 3 трека, 4 трека
            // Все остальные случаи: 0, 5-20, 25-30...
            else -> forms[2] // 0 треков, 5 треков, 10 треков, 12 треков
        }
    }
}