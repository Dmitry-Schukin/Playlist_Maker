package com.practicum.playlist_maker.library.ui.fragments.playlists

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlist_maker.library.domain.model.Playlist
import com.practicum.playlist_maker.library.ui.fragments.playlists.PlaylistViewHolder

class PlaylistAdapter (val clickListener: PlaylistClickListener) : RecyclerView.Adapter<PlaylistViewHolder> () {

    var playlists = ArrayList<Playlist>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder =
        PlaylistViewHolder.Companion.from(parent)

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(playlists[position])
        holder.itemView.setOnClickListener { clickListener.onPlaylistClick(playlists.get(position)) }
    }
    override fun getItemCount(): Int {
        return playlists.size
    }
    fun interface PlaylistClickListener {
        fun onPlaylistClick(tracks: Playlist)
    }
}