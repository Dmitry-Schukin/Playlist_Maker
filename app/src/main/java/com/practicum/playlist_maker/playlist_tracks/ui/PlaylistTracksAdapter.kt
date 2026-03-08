package com.practicum.playlist_maker.playlist_tracks.ui

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlist_maker.search.domain.model.Track

class PlaylistTracksAdapter (val clickListener: PlaylistTrackClickListener,
                             val longClickListener: OnItemLongClickListener) : RecyclerView.Adapter<PlaylistTracksViewHolder>(){

    var trackList = ArrayList<Track>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlaylistTracksViewHolder = PlaylistTracksViewHolder.Companion.from(parent)

    override fun onBindViewHolder(
        holder: PlaylistTracksViewHolder,
        position: Int
    ) {
        holder.bind(trackList[position])

        holder.itemView.setOnClickListener {
            clickListener.onTrackClick(trackList.get(position))
        }
        holder.itemView.setOnLongClickListener {
            longClickListener.onItemLongClick(trackList.get(position))
            true
        }
    }

    override fun getItemCount(): Int {
        return trackList.size
    }

    fun interface PlaylistTrackClickListener {
        fun onTrackClick(tracks: Track)
    }
    fun interface OnItemLongClickListener {
        fun onItemLongClick(tracks: Track)
    }
}