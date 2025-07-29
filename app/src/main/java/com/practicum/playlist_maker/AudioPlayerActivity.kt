package com.practicum.playlist_maker

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.appbar.MaterialToolbar
import com.google.gson.Gson
import com.practicum.playlist_maker.model.Track
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class AudioPlayerActivity : AppCompatActivity() {
    companion object {
        const val TRACK_INFORMATION_KEY = "TRACK_INFORMATION_KEY"
    }

    private lateinit var backClickEvent:MaterialToolbar
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

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.audio_player_constraint_layout)) { view, insets ->
            val statusBar = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            view.updatePadding(top = statusBar.top)
            insets
        }
        //region Getting track using Intent
        val trackTransferIntent = intent
        val receivedJsonTrack = trackTransferIntent.getStringExtra(TRACK_INFORMATION_KEY)
        val track = Gson().fromJson(receivedJsonTrack, Track::class.java)
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
            val trackTime = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis.toLong())
            audioTime.text = trackTime
            trackDuration.text = trackTime

            if(track.releaseDate.isNotEmpty()){
                val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX")
                val parsedDate: LocalDate = LocalDate.parse(track.releaseDate, formatter)
                val yearFromFormattedDate = parsedDate.year.toString()
                trackYearCreation.text = yearFromFormattedDate
            }else{
                yearTitle.isVisible=false
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
        }
        //endregion

        //region Listeners
        backClickEvent.setNavigationOnClickListener {
            finish()
        }
        //endregion
    }
}