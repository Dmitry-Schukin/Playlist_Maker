package com.practicum.playlist_maker.library.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlist_maker.R
import com.practicum.playlist_maker.databinding.FragmentPlaylistBinding
import com.practicum.playlist_maker.library.domain.model.Playlist
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class PlaylistFragment: Fragment(){
    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PlaylistViewModel by viewModel()

    private val playlistAdapter = PlaylistAdapter {
        onPlaylistClickDebounce(it)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        placeholderDisable()
        //region Observer
        viewModel.observeState().observe(requireActivity()) {
            render(it)
        }
        //endregion
        viewModel.showPlaylists()

        binding.createTrackListButton.setOnClickListener {
            showPlaylistCreator()
        }


        //region Creating a list of tracks by using RecyclerView
        binding.playlistRecyclerView.layoutManager =
            GridLayoutManager(requireActivity(),2)
        binding.playlistRecyclerView.adapter = playlistAdapter

        //endregion
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun showPlaylistCreator(){
        findNavController().navigate(
            R.id.action_mediaLibraryFragment_to_createNewPlaylistFragment)
    }
    fun render(state: PlaylistState) {
        when (state) {
            is PlaylistState.Content -> showContent(state.list)
            is PlaylistState.Empty -> showEmpty(state.message)
        }
    }

    private fun placeholderDisable(){
        binding.apply{
            placeholderIconNotCreated.isVisible = false
            playlistsPlaceholderMessage.isVisible = false
        }
    }
    private fun showContent(playlists: List<Playlist>){
        placeholderDisable()
        binding.playlistRecyclerView.isVisible=true
        binding.createTrackListButton.isVisible=true

        playlistAdapter.playlists.clear()
        playlistAdapter.playlists.addAll(playlists)
        playlistAdapter.notifyDataSetChanged()
    }

    private fun showEmpty(emptyMessage: String){
        playlistAdapter.playlists.clear()
        playlistAdapter.notifyDataSetChanged()
        binding.apply{
            placeholderIconNotCreated.isVisible=true
            playlistsPlaceholderMessage.isVisible =true
            createTrackListButton.isVisible=true
            playlistRecyclerView.isVisible=false
            playlistsPlaceholderMessage.text = emptyMessage
        }
    }

    companion object{
        fun newInstance(): PlaylistFragment{
            return PlaylistFragment()
        }
    }
    fun onPlaylistClickDebounce(playlist: Playlist){}
}