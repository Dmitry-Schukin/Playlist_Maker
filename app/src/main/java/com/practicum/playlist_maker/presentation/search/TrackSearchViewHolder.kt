package com.practicum.playlist_maker.presentation.search

import android.annotation.SuppressLint
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlist_maker.R
import com.practicum.playlist_maker.model.Track
import com.bumptech.glide.Glide

class TrackSearchViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val trackImage: ImageView = itemView.findViewById(R.id.album_art)
    private val vocalistName: TextView = itemView.findViewById(R.id.vocalist_name)
    private val songName: TextView = itemView.findViewById(R.id.song_name)
    private val trackTime: TextView = itemView.findViewById(R.id.song_time)

    @SuppressLint("SetTextI18n")
    fun bind(model: Track) {
        Glide.with(trackImage)
            .load(model.artworkUrl100)
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .into(trackImage)
        vocalistName.text = model.artistName
        songName.text = model.trackName
        trackTime.text = model.trackTime
    }
}