package com.practicum.playlist_maker.library.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.practicum.playlist_maker.databinding.FragmentFavoritesTracksBinding
import com.practicum.playlist_maker.search.domain.model.Track
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class FavoritesTracksFragment: Fragment() {
    private var _binding: FragmentFavoritesTracksBinding?=null
    private val binding get()=_binding!!
    private val viewModel: FavoritesTracksViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentFavoritesTracksBinding.inflate(inflater, container, false)
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
        viewModel.showFavoritesTracks()
    }
    private fun placeholderDisable(){
        binding.apply{
            placeholderIconNotFoundFavorites.isVisible = false
            favoritesTracksPlaceholderMessage.isVisible = false
        }
    }
    fun render(state: FragmentState) {
        when (state) {
            is FragmentState.Content -> showContent(state.list as List<Track>)
            is FragmentState.Error -> showError(state.errorMessage)
            is FragmentState.Empty -> showEmpty(state.message)
        }
    }
    private fun showContent(tracks: List<Track>){
        placeholderDisable()
    }
    private fun showError(errorMessage: String){
        binding.apply{
            placeholderIconNotFoundFavorites.isVisible=true
            favoritesTracksPlaceholderMessage.isVisible =true
            favoritesTracksPlaceholderMessage.text = errorMessage
        }

    }
    private fun showEmpty(emptyMessage: String){
        binding.apply{
            placeholderIconNotFoundFavorites.isVisible = true
            favoritesTracksPlaceholderMessage.isVisible = true
            favoritesTracksPlaceholderMessage.text = emptyMessage
        }
    }
    companion object{
        fun newInstance(): FavoritesTracksFragment{
            return FavoritesTracksFragment()
        }
    }
}