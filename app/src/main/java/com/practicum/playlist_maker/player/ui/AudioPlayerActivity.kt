package com.practicum.playlist_maker.player.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.practicum.playlist_maker.R
import com.practicum.playlist_maker.databinding.ActivityAudioPlayerBinding
import com.practicum.playlist_maker.search.domain.model.Track

class AudioPlayerActivity : AppCompatActivity() {
    companion object {
        const val TRACK_INFORMATION_KEY = "TRACK_INFORMATION_KEY"
    }

    private  var viewModel: AudioPlayerViewModel? = null
    private lateinit var bindingAudioPlayerActivity: ActivityAudioPlayerBinding
    private lateinit var track: Track
    private lateinit var mainThreadHandler: Handler


    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingAudioPlayerActivity = ActivityAudioPlayerBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(bindingAudioPlayerActivity.root)
        ViewCompat.setOnApplyWindowInsetsListener(bindingAudioPlayerActivity.root) { view, insets ->
            val statusBar = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            view.updatePadding(top = statusBar.top)
            insets
        }

        //region Start State
        bindingAudioPlayerActivity.playButton.isVisible = true
        bindingAudioPlayerActivity.pauseButton.isVisible=false
        //endregion

        //region Getting track using Intent
        val trackTransferIntent = intent
        val receivedJsonTrack = trackTransferIntent.getStringExtra(TRACK_INFORMATION_KEY)
        track = Gson().fromJson(receivedJsonTrack, Track::class.java)
        viewModel = ViewModelProvider(
            this,
            AudioPlayerViewModel.Companion.getFactory(track.previewUrl)
        )
            .get(AudioPlayerViewModel::class.java)

        viewModel?.observePlayerState()?.observe(this) {
            changeButtonState(it == MediaPlayerState.MEDIA_PLAYER_STATE_PLAYING)
        }

        viewModel?.observeProgressTime()?.observe(this) {
            bindingAudioPlayerActivity.audioTime.text= it
        }
        //endregion



        mainThreadHandler = Handler(Looper.getMainLooper())

        //region Fields filling
        if(track!=null) {
            val artworkUrl512 = track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
            Glide.with(bindingAudioPlayerActivity.bigAlbumIcon)
                .load(artworkUrl512)
                .placeholder(R.drawable.placeholder)
                .centerCrop()
                .into(bindingAudioPlayerActivity.bigAlbumIcon)

            bindingAudioPlayerActivity.songNameAudioPlayer.text = track.trackName
            bindingAudioPlayerActivity.vocalistOrGroupName.text = track.artistName

            bindingAudioPlayerActivity.durationValue.text = track.trackTimeMillis

            if(track.releaseDate.isNotEmpty()){
                bindingAudioPlayerActivity.yearValue.text = track.releaseDate
            }else{
                bindingAudioPlayerActivity.year.isVisible =false
                bindingAudioPlayerActivity.yearValue.isVisible=false
            }
            if(track.collectionName.isNotEmpty()) {
                bindingAudioPlayerActivity.albumValue.text = track.collectionName
            } else{
                bindingAudioPlayerActivity.album.isVisible = false
                bindingAudioPlayerActivity.albumValue.isVisible = false
            }
            bindingAudioPlayerActivity.genreValue.text = track.primaryGenreName
            bindingAudioPlayerActivity.countryValue.text = track.country


        }
        //endregion

        //region Listeners
        bindingAudioPlayerActivity.audioPlayerMaterialToolbar.setNavigationOnClickListener {
            finish()
        }
        bindingAudioPlayerActivity.playButton.setOnClickListener {
            viewModel?.startPlayer()
            viewModel?.onPlayButtonClicked()
        }
        bindingAudioPlayerActivity.pauseButton.setOnClickListener {
            viewModel?.pausePlayer()
            viewModel?.onPlayButtonClicked() }


        //endregion
    }
    private fun changeButtonState(isPlaying: Boolean) {
       if (isPlaying){
           bindingAudioPlayerActivity.playButton.isVisible = false
           bindingAudioPlayerActivity.pauseButton.isVisible=true
       }else {
           bindingAudioPlayerActivity.playButton.isVisible = true
           bindingAudioPlayerActivity.pauseButton.isVisible=false
       }
    }

    override fun onPause() {
        super.onPause()
        viewModel?.onPause()
    }

}