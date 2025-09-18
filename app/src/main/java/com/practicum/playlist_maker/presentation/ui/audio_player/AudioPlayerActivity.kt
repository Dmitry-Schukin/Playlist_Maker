package com.practicum.playlist_maker.presentation.ui.audio_player

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import com.bumptech.glide.Glide
import com.google.android.material.appbar.MaterialToolbar
import com.google.gson.Gson
import com.practicum.playlist_maker.R
import com.practicum.playlist_maker.domain.Track
import kotlinx.coroutines.Runnable
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerActivity : AppCompatActivity() {
    companion object {
        const val TRACK_INFORMATION_KEY = "TRACK_INFORMATION_KEY"
        private const val UPDATE_DELAY = 500L
    }
    private var mediaPlayerState = MediaPlayerState.MEDIA_PLAYER_STATE_DEFAULT
    private var mediaPlayer = MediaPlayer()
    private var audioTimeRunnable: Runnable? = null


    private lateinit var track: Track
    private lateinit var backClickEvent: MaterialToolbar
    private lateinit var albumImage: ImageView
    private lateinit var trackName: TextView
    private lateinit var vocalistOrGroup: TextView
    private lateinit var audioTime: TextView
    private lateinit var trackDuration: TextView
    private lateinit var albumName: TextView
    private lateinit var albumNameTitle: TextView
    private lateinit var trackYearCreation: TextView
    private lateinit var yearTitle: TextView
    private lateinit var musicType: TextView
    private lateinit var country: TextView
    private lateinit var playButton: ImageButton
    private lateinit var pauseButton: ImageButton
    private lateinit var mainThreadHandler: Handler


    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_audio_player)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.audio_player_constraint_layout)) { view, insets ->
            val statusBar = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            view.updatePadding(top = statusBar.top)
            insets
        }
        //region Getting track using Intent
        val trackTransferIntent = intent
        val receivedJsonTrack = trackTransferIntent.getStringExtra(TRACK_INFORMATION_KEY)
        track = Gson().fromJson(receivedJsonTrack, Track::class.java)
        //endregion

        backClickEvent = findViewById<MaterialToolbar>(R.id.audio_player_material_toolbar)
        albumImage = findViewById<ImageView>(R.id.big_album_icon)
        trackName = findViewById<TextView>(R.id.song_name_audio_player)
        vocalistOrGroup = findViewById<TextView>(R.id.vocalist_or_group_name)
        audioTime = findViewById<TextView>(R.id.audio_time)
        trackDuration = findViewById<TextView>(R.id.duration_value)
        albumName = findViewById<TextView>(R.id.album_value)
        albumNameTitle = findViewById<TextView>(R.id.album)
        trackYearCreation = findViewById<TextView>(R.id.year_value)
        yearTitle = findViewById<TextView>(R.id.year)
        musicType = findViewById<TextView>(R.id.genre_value)
        country = findViewById<TextView>(R.id.country_value)
        playButton = findViewById<ImageButton>(R.id.play_button)
        pauseButton = findViewById<ImageButton>(R.id.pause_button)
        mainThreadHandler = Handler(Looper.getMainLooper())

        //region Fields filling
        if(track!=null) {
            val artworkUrl512 = track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
            Glide.with(albumImage)
                .load(artworkUrl512)
                .placeholder(R.drawable.placeholder)
                .centerCrop()
                .into(albumImage)

            trackName.text = track.trackName
            vocalistOrGroup.text = track.artistName
            setCurrentAudioTime(0) //set audioTime value
            trackDuration.text = track.trackTimeMillis

            if(track.releaseDate.isNotEmpty()){
                trackYearCreation.text = track.releaseDate
            }else{
                yearTitle.isVisible =false
                trackYearCreation.isVisible=false
            }
            if(track.collectionName.isNotEmpty()) {
                albumName.text = track.collectionName
            } else{
                albumNameTitle.isVisible = false
                albumName.isVisible = false
            }
            musicType.text = track.primaryGenreName
            country.text = track.country

            mediaPlayer.setDataSource(track.previewUrl)
            mediaPlayer.prepareAsync()
        }
        //endregion

        //region Listeners
        backClickEvent.setNavigationOnClickListener {
            finish()
        }
        playButton.setOnClickListener { playbackControl() }
        pauseButton.setOnClickListener { playbackControl() }

        mediaPlayer.setOnPreparedListener {
            playButton.isVisible = true
            pauseButton.isVisible=false
            mediaPlayerState = MediaPlayerState.MEDIA_PLAYER_STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            playButton.isVisible = true
            pauseButton.isVisible=false
            mediaPlayerState = MediaPlayerState.MEDIA_PLAYER_STATE_PREPARED
        }
        //endregion
    }
    private fun setCurrentAudioTime(currentTime: Long){
        val timeFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
        val time = timeFormat.format(currentTime)
        audioTime.text = time
    }
    override fun onPause() {
        super.onPause()
        pausePlayer()
    }
    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        mainThreadHandler.removeCallbacksAndMessages(null)
    }
    private fun startPlayer() {
        mediaPlayer.start()
        pauseButton.isVisible = true
        playButton.isVisible = false
        mediaPlayerState = MediaPlayerState.MEDIA_PLAYER_STATE_PLAYING
        startReadoutCurrentAudioTimeAsync()
    }
    private fun pausePlayer() {
        mediaPlayer.pause()
        playButton.isVisible = true
        pauseButton.isVisible=false
        mediaPlayerState = MediaPlayerState.MEDIA_PLAYER_STATE_PAUSED
        if(audioTimeRunnable!=null){ mainThreadHandler.removeCallbacks(audioTimeRunnable!!) }
    }
    private fun playbackControl() {
        when(mediaPlayerState) {
            MediaPlayerState.MEDIA_PLAYER_STATE_PLAYING -> {
                pausePlayer()
            }
            MediaPlayerState.MEDIA_PLAYER_STATE_PREPARED, MediaPlayerState.MEDIA_PLAYER_STATE_PAUSED -> {
                startPlayer()
            }
            else -> {}
        }
    }
    private fun startReadoutCurrentAudioTimeAsync(){
        audioTimeRunnable=getAudioTimeRunnable()
        mainThreadHandler.post(audioTimeRunnable!!)
    }
    private fun getAudioTimeRunnable(): Runnable {
        return object : Runnable {
            override fun run() {
                    val currentTime = mediaPlayer.currentPosition.toLong()
                    setCurrentAudioTime(currentTime)
                    if(!mediaPlayer.isPlaying)setCurrentAudioTime(0)
                    mainThreadHandler.postDelayed(this, UPDATE_DELAY)
                }
            }

    }

}