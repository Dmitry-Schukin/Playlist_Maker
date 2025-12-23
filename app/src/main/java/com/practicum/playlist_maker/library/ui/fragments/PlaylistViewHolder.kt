package com.practicum.playlist_maker.library.ui.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Environment
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlist_maker.R
import com.practicum.playlist_maker.databinding.PlaylistGridModelBinding
import com.practicum.playlist_maker.library.domain.model.Playlist
import java.io.File

class PlaylistViewHolder (private val binding: PlaylistGridModelBinding) :
    RecyclerView.ViewHolder(binding.root) {

    companion object {
        fun from(parent: ViewGroup): PlaylistViewHolder{
            val inflater = LayoutInflater.from(parent.context)
            val binding = PlaylistGridModelBinding.inflate(inflater, parent, false)
            return PlaylistViewHolder(binding)
        }
    }

    @SuppressLint("SetTextI18n")
    fun bind(model: Playlist) {
        fun dpToPx(dp: Float, context: Context): Int {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                context.resources.displayMetrics
            ).toInt()
        }

        val cornerRadius = dpToPx(8f, itemView.context)

        Glide.with(binding.playlistImage)
            .load(model.imagePath)
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(RoundedCorners(cornerRadius))
            .into(binding.playlistImage)
        binding.playlistName.text = model.playlistName
        binding.trackCount.text = model.trackCount.toString()+" "+getTrackString(model.trackCount)
    }
    fun getTrackString(number:Int):String{
        var forms =mutableListOf<String>("трек","трека","треков")
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