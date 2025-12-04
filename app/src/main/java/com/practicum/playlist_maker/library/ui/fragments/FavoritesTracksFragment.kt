package com.practicum.playlist_maker.library.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlist_maker.R
import com.practicum.playlist_maker.databinding.FragmentFavoritesTracksBinding
import com.practicum.playlist_maker.player.ui.AudioPlayerFragment
import com.practicum.playlist_maker.search.domain.model.Track
import com.practicum.playlist_maker.search.ui.TrackSearchAdapter
import com.practicum.playlist_maker.utils.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class FavoritesTracksFragment: Fragment() {
    private var _binding: FragmentFavoritesTracksBinding?=null
    private val binding get()=_binding!!
    private val viewModel: FavoritesTracksViewModel by viewModel()

    //region Adapters initialization
    private val favoritesAdapter = TrackSearchAdapter {
        onFavoriteTrackClickDebounce(it)
    }
    //endregion

    //region Debounce
    private lateinit var onFavoriteTrackClickDebounce: (Track) -> Unit
    //endregion


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
        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }
        //endregion

        //region Creating a list of tracks by using RecyclerView
        binding.favoritesRecyclerView.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        binding.favoritesRecyclerView.adapter = favoritesAdapter
        //endregion

        //region Debouncer
        onFavoriteTrackClickDebounce = debounce<Track>(CLICK_ON_FAVORITE_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope, false) { track ->
            showTrackAudioPlayer(track)
        }
        //endregion
    }
    override fun onResume() {
        super.onResume()
        viewModel.showFavoritesTracks()
    }
    private fun showTrackAudioPlayer(track: Track){
        val bundle = Bundle().apply { putParcelable(AudioPlayerFragment.Companion.TRACK_INFORMATION_KEY,track) }
        findNavController().navigate(
            R.id.action_mediaLibraryFragment_to_audioPlayerFragment,
            bundle)

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun placeholderDisable(){
        binding.apply{
            placeholderIconNotFoundFavorites.isVisible = false
            favoritesTracksPlaceholderMessage.isVisible = false
        }
    }
    fun render(state: FragmentState) {
        when (state) {
            is FragmentState.Content -> showContent(state.list)
            is FragmentState.Empty -> showEmpty(state.message)
        }
    }

    private fun showContent(tracks: List<Track>){
        placeholderDisable()
        binding.favoritesRecyclerView.isVisible=true

        favoritesAdapter.trackList.clear()
        favoritesAdapter.trackList.addAll(tracks)
        favoritesAdapter.notifyDataSetChanged()
    }
    private fun showError(errorMessage: String){
        favoritesAdapter.trackList.clear()
        favoritesAdapter.notifyDataSetChanged()

        binding.apply{
            placeholderIconNotFoundFavorites.isVisible=true
            favoritesTracksPlaceholderMessage.isVisible =true
            favoritesRecyclerView.isVisible=false
            favoritesTracksPlaceholderMessage.text = errorMessage
        }

    }
    private fun showEmpty(emptyMessage: String){
        favoritesAdapter.trackList.clear()
        favoritesAdapter.notifyDataSetChanged()

        binding.apply{
            placeholderIconNotFoundFavorites.isVisible = true
            favoritesTracksPlaceholderMessage.isVisible = true
            favoritesRecyclerView.isVisible=false
            favoritesTracksPlaceholderMessage.text = emptyMessage
        }
    }
    companion object{
        fun newInstance(): FavoritesTracksFragment{
            return FavoritesTracksFragment()
        }
        private const val CLICK_ON_FAVORITE_DEBOUNCE_DELAY = 1000L
    }
}