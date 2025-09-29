package com.practicum.playlist_maker.library.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.practicum.playlist_maker.databinding.ActivityMediaLibraryBinding

class MediaLibraryActivity : AppCompatActivity() {
    private lateinit var bindingMediaLibraryActivity: ActivityMediaLibraryBinding
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingMediaLibraryActivity = ActivityMediaLibraryBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(bindingMediaLibraryActivity.root)

        ViewCompat.setOnApplyWindowInsetsListener(bindingMediaLibraryActivity.root) { view, insets ->
            val statusBar = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            view.updatePadding(top = statusBar.top)
            insets
        }
    }
}