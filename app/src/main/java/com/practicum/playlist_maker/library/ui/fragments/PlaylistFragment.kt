package com.practicum.playlist_maker.library.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.practicum.playlist_maker.databinding.FragmentPlaylistBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class PlaylistFragment: Fragment(){
    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PlaylistViewModel by viewModel()

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
    }
    fun render(state: FragmentState) {
        when (state) {
            is FragmentState.Content -> showContent(state.list)
            is FragmentState.Error -> showError(state.errorMessage)
            is FragmentState.Empty -> showEmpty(state.message)
        }
    }

    private fun placeholderDisable(){
        binding.apply{
            placeholderIconNotCreated.isVisible = false
            playlistsPlaceholderMessage.isVisible = false
            updateTrackListButton.isVisible=false
        }
    }
    private fun showContent(tracks: List<Any>){
        placeholderDisable()
    }
    private fun showError(errorMessage: String){
        binding.apply{
            placeholderIconNotCreated.isVisible=true
            playlistsPlaceholderMessage.isVisible =true
            updateTrackListButton.isVisible=true
            playlistsPlaceholderMessage.text = errorMessage
        }

    }
    private fun showEmpty(emptyMessage: String){
        binding.apply{
            placeholderIconNotCreated.isVisible=true
            playlistsPlaceholderMessage.isVisible =true
            updateTrackListButton.isVisible=true
            playlistsPlaceholderMessage.text = emptyMessage
        }
    }
    companion object{
        fun newInstance(): PlaylistFragment{
            return PlaylistFragment()
        }
    }
}