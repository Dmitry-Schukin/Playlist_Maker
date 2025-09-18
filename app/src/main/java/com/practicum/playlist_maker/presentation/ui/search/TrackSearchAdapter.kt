package com.practicum.playlist_maker.presentation.ui.search

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlist_maker.domain.Track
import com.practicum.playlist_maker.presentation.ui.search.TrackSearchViewHolder

class TrackSearchAdapter (val clickListener: TrackClickListener) : RecyclerView.Adapter<TrackSearchViewHolder> (){

    var trackList = ArrayList<Track>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackSearchViewHolder =
        TrackSearchViewHolder(parent)


    override fun onBindViewHolder(holder: TrackSearchViewHolder, position: Int) {
        holder.bind(trackList[position])
        holder.itemView.setOnClickListener { clickListener.onTrackClick(trackList.get(position)) }
    }

    override fun getItemCount(): Int {
        return trackList.size
    }
    fun interface TrackClickListener {
        fun onTrackClick(tracks: Track)
    }
}