package com.practicum.playlist_maker.search.ui

import android.annotation.SuppressLint
import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlist_maker.R
import com.practicum.playlist_maker.databinding.TrackListModelBinding
import com.practicum.playlist_maker.search.domain.model.Track

class TrackSearchViewHolder (private val binding: TrackListModelBinding) :
    RecyclerView.ViewHolder(binding.root)  {

    companion object {
        fun from(parent: ViewGroup):TrackSearchViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = TrackListModelBinding.inflate(inflater, parent, false)
            return TrackSearchViewHolder(binding)
        }
    }
    @SuppressLint("SetTextI18n")
    fun bind(model: Track) {
        fun dpToPx (dp: Float, context: Context): Int{
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                context.resources.displayMetrics).toInt()
        }
        val cornerRadius = dpToPx(2f, itemView.context)

        Glide.with(binding.albumArt)
            .load(model.artworkUrl100)
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(RoundedCorners(cornerRadius))
            .into(binding.albumArt)
        binding.vocalistName.text = model.artistName
        binding.songName.text = model.trackName
        binding.songTime.text = model.trackTimeMillis
    }

}