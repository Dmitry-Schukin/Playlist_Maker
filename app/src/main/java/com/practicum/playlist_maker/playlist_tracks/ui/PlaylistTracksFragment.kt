package com.practicum.playlist_maker.playlist_tracks.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlist_maker.R
import com.practicum.playlist_maker.databinding.FragmentPlaylistTracksBinding
import com.practicum.playlist_maker.library.ui.fragments.playlists.PlaylistFragment.Companion.PLAYLIST_INFORMATION_KEY
import com.practicum.playlist_maker.player.ui.AudioPlayerFragment
import com.practicum.playlist_maker.search.domain.model.Track
import com.practicum.playlist_maker.utils.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.io.File


class PlaylistTracksFragment: Fragment() {

    private val viewModel: PlaylistTracksViewModel by viewModel {
        parametersOf(playlistId)
    }
    private var playlistId: Long = -1L
    private var _binding: FragmentPlaylistTracksBinding? = null
    private val binding get() = _binding!!
    private lateinit var trackBottomSheetContainer: LinearLayout
    private lateinit var trackBottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var menuBottomSheetContainer: LinearLayout
    private lateinit var menuBottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    //region Adapters initialization
    private val tracksAdapter = PlaylistTracksAdapter (
        {onTrackClickDebounce(it)},
        {deleteTrack(it)}
    )
    //endregion

    //region Debounce
    private lateinit var onTrackClickDebounce: (Track) -> Unit
    //endregion

    private lateinit var confirmDialog: MaterialAlertDialogBuilder

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentPlaylistTracksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //region Getting playlist using Parcelable
        playlistId = requireArguments().getLong(PLAYLIST_INFORMATION_KEY)

        //endregion

        //region Observer
        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }
        //endregion

        //region Starting settings for displaying screen elements
        viewModel.showTracksFromPlaylists()
        //endregion

        //region Debounce Initialization
        onTrackClickDebounce = debounce<Track>(CLICK_ON_TRACK_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope, false) { track ->
            showTrackAudioPlayer(track)
        }
        //endregion

        //region Creating a list of tracks by using RecyclerView
        binding.playlistTracksRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.playlistTracksRecyclerView.adapter = tracksAdapter
        //endregion
        //region BottomSheet
        trackBottomSheetContainer = binding.tracksBottomSheetContainer
        trackBottomSheetBehavior = BottomSheetBehavior.from(trackBottomSheetContainer).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
            peekHeight = getPeekHeightForThisScreen()
        }
        menuBottomSheetContainer = binding.playlistActionsBottomSheetContainer
        hideMenu()

        trackBottomSheetBehavior.addBottomSheetCallback(object: BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
            }
            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }
        })
        menuBottomSheetBehavior.addBottomSheetCallback(object: BottomSheetBehavior.BottomSheetCallback() {
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
        //region Listeners
        binding.playlistAdditionIcon.setOnClickListener {
            menuBottomSheetBehavior = BottomSheetBehavior.from(menuBottomSheetContainer).apply {
                state = BottomSheetBehavior.STATE_HALF_EXPANDED
            }
        }
        binding.materialToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        binding.deletePlaylistMaterialTextView.setOnClickListener {
            deletePlaylist()
        }
        binding.editInfoMaterialTextView.setOnClickListener {
            val playlist = viewModel.getCurrentPlaylist()
            val bundle = Bundle().apply { putParcelable(PLAYLIST_INFORMATION_KEY_FOR_UPDATE,playlist) }
            findNavController().navigate(
                R.id.action_playlistTracksFragment_to_createNewPlaylistFragment,
                bundle)
        }
        binding.shareMaterialTextView.setOnClickListener {
            sharePlaylist()
        }
        binding.playlistShareIcon.setOnClickListener {
            sharePlaylist()
        }
        //endregion

    }

    fun sharePlaylist(){
        hideMenu()

        if(viewModel.trackListIsNotEmpty()){
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.setType("text/plain")
            shareIntent.putExtra(Intent.EXTRA_TEXT, viewModel.sharePlaylist())
            startActivity(shareIntent)
        }else{
            val message = requireContext().getString(R.string.playlist_is_not_available_for_sharing)
            Toast.makeText(requireContext(),message, Toast.LENGTH_SHORT).show()
        }
    }
    private fun showTrackAudioPlayer(track: Track){
        val bundle = Bundle().apply { putParcelable(AudioPlayerFragment.Companion.TRACK_INFORMATION_KEY,track) }
       findNavController().navigate(
            R.id.action_playlistTracksFragment_to_audioPlayerFragment,
            bundle)

    }
    private fun deletePlaylist(){
        //region Callback creating
        val playlistTitle = viewModel.getCurrentPlaylistTitle()
        val message = requireContext().getString(R.string.do_you_want_to_delete_playlist)+" \"$playlistTitle\"?"
        confirmDialog = MaterialAlertDialogBuilder(requireContext())
            .setMessage(message)
            .setNeutralButton(requireContext().getString(R.string.cansel)) { dialog, which ->
                // nothing
                hideMenu()
            }.setPositiveButton(requireContext().getString(R.string.delete)) { dialog, which ->
                // delete playlist
                deleteImageFromInternalStorage(viewModel.getCurrentPlaylist().imagePath)
                viewModel.deletePlaylist()
                findNavController().navigateUp()
            }
        //endregion
        confirmDialog.show()
    }

    fun hideMenu(){
        menuBottomSheetBehavior = BottomSheetBehavior.from(menuBottomSheetContainer).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }
    }
    private fun deleteTrack(track: Track){
        //region Callback creating
        confirmDialog = MaterialAlertDialogBuilder(requireContext())
            .setMessage(requireContext().getString(R.string.do_you_want_to_delete_track))
            .setNegativeButton(requireContext().getString(R.string.no))  { dialog, which ->
                // nothing
            }.setPositiveButton(requireContext().getString(R.string.yes)) { dialog, which ->
                // delete track
                viewModel.deleteTrackFromPlaylist(track)
            }
        //endregion
        confirmDialog.show()
    }
    //region Showing data methods
    fun render(state: PlaylistTrackState) {
        when (state) {
            is PlaylistTrackState.Content -> showContent(
                state.title,
                state.description,
                state.imagePath,
                state.list,
                state.duration,
                state.trackCount
                )
            is PlaylistTrackState.Empty -> showEmpty(
                state.title,
                state.description,
                state.imagePath,
                state.message,
                state.duration,
                state.trackCount)
        }
    }
    fun showContent(title:String, description:String, imagePath:String, trackList: List<Track>, playlistDuration:Int, trackCount:Int){

        binding.placeholderIcon.isVisible = false
        binding.placeholderMessage.isVisible = false
        binding.playlistTracksRecyclerView.isVisible = true

        binding.playlistTitle.text = title
        binding.playlistName.text = title
        binding.playlistDescription.text = description
        Glide.with(binding.playlistImage)
            .load(imagePath)
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .into(binding.playlistImage)
        Glide.with(binding.playlistIcon)
            .load(imagePath)
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .into(binding.playlistIcon)


        val trackCount = trackCount.toString() + " " + getTrackForms(trackCount)
        val minutesCount = playlistDuration.toString() + " " + getMinutesForms(playlistDuration)
        binding.playlistMinutes.text = minutesCount
        binding.trackCount.text = trackCount
        binding.count.text= trackCount
        tracksAdapter.trackList.clear()
        tracksAdapter.trackList.addAll(trackList)
        tracksAdapter.notifyDataSetChanged()
    }
    fun showEmpty(title:String, description:String, imagePath:String,emptyMessage: String, playlistDuration:Int, trackCount:Int) {
        val message = requireContext().getString(emptyMessage.toInt())
        val trackCount = trackCount.toString() + " " + getTrackForms(trackCount)
        val minutesCount = playlistDuration.toString() + " " + getMinutesForms(playlistDuration)

        binding.placeholderIcon.isVisible = true
        binding.placeholderMessage.isVisible = true
        binding.placeholderMessage.text = message
        binding.playlistTracksRecyclerView.isVisible = false

        binding.playlistTitle.text = title
        binding.playlistName.text = title
        binding.playlistDescription.text = description
        Glide.with(binding.playlistImage)
            .load(imagePath)
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .into(binding.playlistImage)
        Glide.with(binding.playlistIcon)
            .load(imagePath)
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .into(binding.playlistIcon)

        binding.playlistMinutes.text = minutesCount
        binding.trackCount.text = trackCount
        binding.count.text= trackCount
    }
    //endregion
    fun getTrackForms(number:Int):String{
        val forms = mutableListOf<String>(
            requireContext().getString(R.string.track),
            requireContext().getString(R.string.track_a),
            requireContext().getString(R.string.track_ov))
        val lastDigit = number % 10
        val lastTwoDigits = number % 100
        return when {
            // 1, 21, 31, 41... (кроме 11)
            lastDigit == 1 && lastTwoDigits != 11 -> forms[0] // 1 трек
            // 2, 3, 4, 22, 23, 24... (кроме 12, 13, 14)
            lastDigit in 2..4 && lastTwoDigits !in 12..14 -> forms[1] // 2 трека, 3 трека, 4 трека
            // Все остальные случаи: 0, 5-20, 25-30...
            else -> forms[2] // 0 треков, 5 треков, 10 треков, 12 треков
        }
    }
    fun getMinutesForms(number:Int):String{
        val forms = mutableListOf<String>(
            requireContext().getString(R.string.minute),
            requireContext().getString(R.string.minutes2),
            requireContext().getString(R.string.minutes))
        val lastDigit = number % 10
        val lastTwoDigits = number % 100
        return when {
            // 1, 21, 31, 41... (кроме 11)
            lastDigit == 1 && lastTwoDigits != 11 -> forms[0] // 1 трек
            // 2, 3, 4, 22, 23, 24... (кроме 12, 13, 14)
            lastDigit in 2..4 && lastTwoDigits !in 12..14 -> forms[1] // 2 трека, 3 трека, 4 трека
            // Все остальные случаи: 0, 5-20, 25-30...
            else -> forms[2] // 0 треков, 5 треков, 10 треков, 12 треков
        }
    }

    fun deleteImageFromInternalStorage(uri: String){
        if(uri.isNotEmpty()) {
            val filePath = File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), requireActivity().getString(R.string.photo_storage))
            val imageName = uri.split("/").last()
            val imageInPrivateDirectory = File(filePath, imageName)
            if (imageInPrivateDirectory.exists()) {
                val deleted = imageInPrivateDirectory.delete()
                if (deleted) {
                    Log.d("ImageDirectory", "Файл $uri успешно удален из внутренней памяти")
                } else {
                    Log.e("ImageDirectory", "Не удалось удалить файл $uri")
                }
            } else {
                Log.e("ImageDirectory", "Файл $uri не найден")
            }
        }else{
            Log.e("ImageDirectory", "Ссылка на файл отсутствует")
        }
    }

   //region BottomSheet Drawing functions
    fun getScreenHeight(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = requireActivity().windowManager.currentWindowMetrics
            windowMetrics.bounds.height()
        } else {
            val displayMetrics = DisplayMetrics()
            @Suppress("DEPRECATION")
            requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
            displayMetrics.heightPixels
        }
    }
    fun getPeekHeightForThisScreen(): Int {
        val rootHeightInPx = getScreenHeight()
        val bottomSheetHeight = rootHeightInPx*0.3
        return bottomSheetHeight.toInt()
    }
    //endregion
    companion object {
        const val PLAYLIST_INFORMATION_KEY_FOR_UPDATE = "PLAYLIST_INFORMATION_KEY_FOR_UPDATE"
        private const val CLICK_ON_TRACK_DEBOUNCE_DELAY = 1000L

    }

}