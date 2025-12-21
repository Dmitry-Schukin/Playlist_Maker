package com.practicum.playlist_maker.player.ui

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.practicum.playlist_maker.R
import com.practicum.playlist_maker.databinding.FragmentAudioPlayerBinding
import com.practicum.playlist_maker.library.domain.model.Playlist
import com.practicum.playlist_maker.player.domain.model.MediaPlayerState
import com.practicum.playlist_maker.search.domain.model.Track
import com.practicum.playlist_maker.utils.debounce
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class AudioPlayerFragment: Fragment() {
    private val viewModel:AudioPlayerViewModel by viewModel {
        parametersOf(track)
    }
    private var _binding: FragmentAudioPlayerBinding? = null
    private val binding get() = _binding!!
    private lateinit var track: Track
    private lateinit var url: String
    private lateinit var mainThreadHandler: Handler
    private val playlistAdapter = PlaylistInPlayerAdapter{
        onPlaylistClickDebounce(it)
    }
    private lateinit var onPlaylistClickDebounce: (Playlist) -> Unit

    private var chosenPlaylistTitle: String = ""
    private lateinit var bottomSheetContainer: LinearLayout
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>


    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentAudioPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //region Start State
        binding.playButton.isVisible = true
        binding.pauseButton.isVisible=false
        //endregion

        //region Getting track using Parcelable

        track = if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.TIRAMISU) {
            requireArguments().getParcelable(TRACK_INFORMATION_KEY, Track::class.java)
        }else{
            @Suppress("DEPRECATION")
            requireArguments().getParcelable<Track>(TRACK_INFORMATION_KEY)
        }?: return
        url = track.previewUrl

        viewModel.observeState().observe(viewLifecycleOwner){ t->
            changeButtonState(t.state== MediaPlayerState.MEDIA_PLAYER_STATE_PLAYING)
            changeFavoriteStateButton(t.isFavorite)
            binding.audioTime.text= t.timer
            updatePlaylists(t)
            addTrackToPlaylist(t.inPlaylist)
        }
        //endregion

        //region BottomSheet
        bottomSheetContainer = binding.bottomSheetContainer
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }
        //endregion

        mainThreadHandler = Handler(Looper.getMainLooper())

        //region Fields filling
        if(track!=null) {
            val artworkUrl512 = track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
            Glide.with(binding.bigAlbumIcon)
                .load(artworkUrl512)
                .placeholder(R.drawable.placeholder)
                .centerCrop()
                .into(binding.bigAlbumIcon)

            binding.songNameAudioPlayer.text = track.trackName
            binding.vocalistOrGroupName.text = track.artistName

            binding.durationValue.text = track.trackTimeMillis

            if(track.releaseDate.isNotEmpty()){
                binding.yearValue.text = track.releaseDate
            }else{
                binding.year.isVisible =false
                binding.yearValue.isVisible=false
            }
            if(track.collectionName.isNotEmpty()) {
                binding.albumValue.text = track.collectionName
            } else{
                binding.album.isVisible = false
                binding.albumValue.isVisible = false
            }
            binding.genreValue.text = track.primaryGenreName
            binding.countryValue.text = track.country
        }
        //endregion
        //region Listeners
        binding.audioPlayerMaterialToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        binding.playButton.setOnClickListener {
            viewModel.onPlayButtonClicked()
        }
        binding.pauseButton.setOnClickListener {
            viewModel.onPlayButtonClicked()
        }
        binding.addToFavorites.setOnClickListener {
            viewModel.onFavoriteButtonClicked()
            buttonEnabledDelay(binding.addToFavoritesActive)
        }
        binding.addToFavoritesActive.setOnClickListener {
            viewModel.onFavoriteButtonClicked()
            buttonEnabledDelay(binding.addToFavorites)
        }
        binding.addToPlaylist.setOnClickListener {
            viewModel.requestPlaylists()
            bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer).apply {
                state = BottomSheetBehavior.STATE_HALF_EXPANDED
            }
        }
        binding.createPlaylistButton.setOnClickListener {
            showPlaylistCreator()
        }
        bottomSheetBehavior.addBottomSheetCallback(object: BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when(newState){
                    BottomSheetBehavior.STATE_HIDDEN ->{
                        binding.overlay.isVisible= false
                    }
                    else -> {
                        binding.overlay.isVisible= true
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.overlay.alpha=slideOffset
            }
        })

        //endregion
        onPlaylistClickDebounce = debounce<Playlist>(CLICK_ON_PLAYLIST_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope, false) { playlist ->
            chosenPlaylistTitle = playlist.playlistName
            viewModel.checkAndAddTrackInChosenPlaylist(playlist)
            bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer).apply {
                state = BottomSheetBehavior.STATE_HIDDEN
            }
        }

        //region Creating a list of tracks by using RecyclerView
        binding.playlistsRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.playlistsRecyclerView.adapter = playlistAdapter
        //endregion

        requireActivity().onBackPressedDispatcher.addCallback(
            object: OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    viewModel.release()
                }
            })
    }

    override fun onStop() {
        super.onStop()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        //viewModel.release()
        _binding = null
    }
    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    override fun onResume() {
        super.onResume()

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }
    }


    private fun changeButtonState(isPlaying: Boolean) {
        if (isPlaying){
            binding.playButton.isVisible = false
            binding.pauseButton.isVisible=true
        }else {
            binding.playButton.isVisible = true
            binding.pauseButton.isVisible=false
        }
    }

    private fun changeFavoriteStateButton(isFavorites: Boolean){
        if(isFavorites){
            binding.addToFavorites.isVisible = false
            binding.addToFavoritesActive.isVisible = true
        }else{
            binding.addToFavorites.isVisible = true
            binding.addToFavoritesActive.isVisible = false
        }
    }
    private fun buttonEnabledDelay(button: ImageButton) {
        lifecycleScope.launch {
            button.isEnabled = false
            delay(CLICK_ON_FAVORITE_DELAY)
            button.isEnabled = true
        }
    }
    private fun updatePlaylists(state:StateController){
        playlistAdapter.playlists.clear()
        playlistAdapter.playlists.addAll(state.playlists)
        playlistAdapter.notifyDataSetChanged()
    }
    private fun addTrackToPlaylist(state: AddingToPlaylistState){
        when(state){
            AddingToPlaylistState.AlreadyInPlaylist -> {
                toastConstructor(R.string.track_is_already_in_playlist,chosenPlaylistTitle)
                viewModel.returnDefaultState()
            }
            AddingToPlaylistState.ThereIsNotInPlaylist -> {viewModel.returnDefaultState()}
            AddingToPlaylistState.JustAddedInPlaylist -> {
                toastConstructor(R.string.track_was_added_in_playlist,chosenPlaylistTitle)
                viewModel.returnDefaultState()
            }
            AddingToPlaylistState.Default -> {}
        }
    }
    private fun toastConstructor(textFromDirectory:Int,playlistTitle: String ){
        val message = requireContext().getString(textFromDirectory) + " " + playlistTitle
        Toast.makeText(requireContext(),message, Toast.LENGTH_SHORT).show()
    }
    private fun showPlaylistCreator(){
        findNavController().navigate(R.id.action_audioPlayerFragment_to_createNewPlaylistFragment)
    }

    companion object {
        const val TRACK_INFORMATION_KEY = "TRACK_INFORMATION_KEY"
        private const val CLICK_ON_FAVORITE_DELAY = 1000L
        private const val CLICK_ON_PLAYLIST_DEBOUNCE_DELAY = 1000L
    }
}