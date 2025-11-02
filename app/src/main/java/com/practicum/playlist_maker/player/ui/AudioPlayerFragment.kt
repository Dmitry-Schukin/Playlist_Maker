package com.practicum.playlist_maker.player.ui

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.practicum.playlist_maker.R
import com.practicum.playlist_maker.databinding.FragmentAudioPlayerBinding
import com.practicum.playlist_maker.player.domain.model.MediaPlayerState
import com.practicum.playlist_maker.search.domain.model.Track
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class AudioPlayerFragment: Fragment() {


    private val viewModel:AudioPlayerViewModel by viewModel {
        parametersOf(url)
    }
    private var _binding: FragmentAudioPlayerBinding? = null
    private val binding get() = _binding!!
    private lateinit var track: Track
    private lateinit var url: String
    private lateinit var mainThreadHandler: Handler

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentAudioPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //region Start State
        binding.playButton.isVisible = true
        binding.pauseButton.isVisible=false
        //endregion

        //region Getting track using Parcelable

        track = if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.TIRAMISU) {
            requireArguments().getParcelable(TRACK_INFORMATION_KEY, Track::class.java)
        }else{
            @Suppress("DEPRECATION")
            requireArguments().getParcelable<Track>(TRACK_INFORMATION_KEY)
        }?: return

        url = track.previewUrl

        viewModel.observeStateAndTime().observe(viewLifecycleOwner){
            changeButtonState(it.state== MediaPlayerState.MEDIA_PLAYER_STATE_PLAYING)
            binding.audioTime.text= it.getTimerValue()
        }

        //endregion

        mainThreadHandler = Handler(Looper.getMainLooper())

        //region Fields filling
        if(track!=null) {
            val artworkUrl512 = track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
            Glide.with(binding.bigAlbumIcon)
                .load(artworkUrl512)
                .placeholder(R.drawable.placeholder)
                .centerCrop()
                .into(binding.bigAlbumIcon)

            binding.songNameAudioPlayer.text = track.trackName
            binding.vocalistOrGroupName.text = track.artistName

            binding.durationValue.text = track.trackTimeMillis

            if(track.releaseDate.isNotEmpty()){
                binding.yearValue.text = track.releaseDate
            }else{
                binding.year.isVisible =false
                binding.yearValue.isVisible=false
            }
            if(track.collectionName.isNotEmpty()) {
                binding.albumValue.text = track.collectionName
            } else{
                binding.album.isVisible = false
                binding.albumValue.isVisible = false
            }
            binding.genreValue.text = track.primaryGenreName
            binding.countryValue.text = track.country
        }
        //endregion
        //region Listeners
        binding.audioPlayerMaterialToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        binding.playButton.setOnClickListener {
            viewModel.startPlayer()
            viewModel.onPlayButtonClicked()
        }
        binding.pauseButton.setOnClickListener {
            viewModel.pausePlayer()
            viewModel.onPlayButtonClicked() }


        //endregion

    }
    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.pauseTimer()
        viewModel.release()
        _binding = null
    }
    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    private fun changeButtonState(isPlaying: Boolean) {
        if (isPlaying){
            binding.playButton.isVisible = false
            binding.pauseButton.isVisible=true
        }else {
            binding.playButton.isVisible = true
            binding.pauseButton.isVisible=false
        }
    }
    companion object {
        const val TRACK_INFORMATION_KEY = "TRACK_INFORMATION_KEY"
    }
}