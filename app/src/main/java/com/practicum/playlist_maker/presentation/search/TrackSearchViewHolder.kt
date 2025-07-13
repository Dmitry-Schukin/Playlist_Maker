package com.practicum.playlist_maker.presentation.search

import android.annotation.SuppressLint
import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlist_maker.R
import com.practicum.playlist_maker.model.Track
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale

class TrackSearchViewHolder (parent: ViewGroup) :
    RecyclerView.ViewHolder(LayoutInflater.from(parent.context)
    .inflate(R.layout.track_list_model, parent, false))  {

    private val trackImage: ImageView = itemView.findViewById(R.id.album_art)
    private val vocalistName: TextView = itemView.findViewById(R.id.vocalist_name)
    private val songName: TextView = itemView.findViewById(R.id.song_name)
    private val trackTime: TextView = itemView.findViewById(R.id.song_time)

    @SuppressLint("SetTextI18n")
    fun bind(model: Track) {
        fun dpToPx (dp: Float, context: Context): Int{
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                context.resources.displayMetrics).toInt()
        }
        val cornerRadius = dpToPx(2f, itemView.context)

        Glide.with(trackImage)
            .load(model.artworkUrl100)
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(RoundedCorners(cornerRadius))
            .into(trackImage)
        vocalistName.text = model.artistName
        songName.text = model.trackName
        trackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(model.trackTimeMillis.toLong())
    }

}