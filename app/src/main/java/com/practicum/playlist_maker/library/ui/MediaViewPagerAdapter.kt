package com.practicum.playlist_maker.library.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.practicum.playlist_maker.library.ui.fragments.FavoritesTracksFragment
import com.practicum.playlist_maker.library.ui.fragments.PlaylistFragment

class MediaViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle)
    : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0-> FavoritesTracksFragment.newInstance()
            else -> PlaylistFragment.newInstance()
        }
    }

    override fun getItemCount(): Int {
        return 2
    }
}