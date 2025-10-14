package com.practicum.playlist_maker.library.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.google.android.material.tabs.TabLayoutMediator
import com.practicum.playlist_maker.databinding.ActivityMediaLibraryBinding
import com.practicum.playlist_maker.R

class MediaLibraryActivity : AppCompatActivity() {
    private lateinit var bindingMediaLibraryActivity: ActivityMediaLibraryBinding
    private lateinit var tabMediator: TabLayoutMediator
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
        bindingMediaLibraryActivity.mediaMaterialToolbar.setNavigationOnClickListener {
            finish()
        }
        bindingMediaLibraryActivity.viewPager.adapter = MediaViewPagerAdapter(supportFragmentManager, lifecycle)
        tabMediator = TabLayoutMediator(bindingMediaLibraryActivity.tabLayout,
            bindingMediaLibraryActivity.viewPager) { tab, position ->
            when(position) {
                0 -> tab.text=getString(R.string.favorites_tracks)
                1 -> tab.text=getString(R.string.playlists)
            }
        }
        tabMediator.attach()
    }
    override fun onDestroy() {
        super.onDestroy()
        tabMediator.detach()
    }
}