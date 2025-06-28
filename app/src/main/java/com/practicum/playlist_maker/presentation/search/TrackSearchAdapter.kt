package com.practicum.playlist_maker.presentation.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlist_maker.R
import com.practicum.playlist_maker.model.Track

class TrackSearchAdapter (
    private val trackList: List<Track>
) : RecyclerView.Adapter<TrackSearchViewHolder> (){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackSearchViewHolder = TrackSearchViewHolder(parent)


    override fun onBindViewHolder(holder: TrackSearchViewHolder, position: Int) {
        holder.bind(trackList[position])
    }

    override fun getItemCount(): Int {
        return trackList.size
    }
}