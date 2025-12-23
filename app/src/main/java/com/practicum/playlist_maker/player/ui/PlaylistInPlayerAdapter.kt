package com.practicum.playlist_maker.player.ui

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlist_maker.library.domain.model.Playlist

class PlaylistInPlayerAdapter(val clickListener: PlaylistClickListener) : RecyclerView.Adapter<PlaylistInPlayerViewHolder> () {

    var playlists = ArrayList<Playlist>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlaylistInPlayerViewHolder = PlaylistInPlayerViewHolder.Companion.from(parent)

    override fun onBindViewHolder(
        holder: PlaylistInPlayerViewHolder,
        position: Int
    ) {
        holder.bind(playlists[position])
        holder.itemView.setOnClickListener { clickListener.onPlaylistClick(playlists.get(position)) }
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

    fun interface PlaylistClickListener {
        fun onPlaylistClick(playlists: Playlist)
    }
}