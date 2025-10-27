package com.practicum.playlist_maker.search.ui

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlist_maker.R
import com.practicum.playlist_maker.databinding.FragmentSearchBinding
import com.practicum.playlist_maker.player.ui.AudioPlayerFragment
import com.practicum.playlist_maker.search.domain.model.Track
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment: Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private var isClickOnTrackAllowed = true
    private val mainThreadHandler = Handler(Looper.getMainLooper())
    private val viewModel: SearchViewModel by viewModel()
    private var textWatcher: TextWatcher? = null

    //region Adapters initialization
    private val searchAdapter = TrackSearchAdapter {
        if (clickOnTrackDebounce()) {
            viewModel.addNewTrackToHistoryList(it)
            showTrackAudioPlayer(it)
        }
    }
    private val historyAdapter = TrackSearchAdapter {
        if (clickOnTrackDebounce()) {
            showTrackAudioPlayer(it)
        }
    }
    //endregion

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //region Observer
        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }
        //endregion

        //region Starting settings for displaying screen elements
        showClearTextButtonOrNot()
        placeholderDisable()
        binding.inputEditText.requestFocus()//focus on EditText
        viewModel.getHistoryList()
        //endregion

        //region Listeners

        binding.clearIcon.setOnClickListener{
            binding.apply {
                inputEditText.setText("")
                trackSearchRecyclerView.isVisible =false
            }
            val inputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(binding.inputEditText.windowToken, 0) //Hide keyboard
            searchAdapter.trackList.clear()
            searchAdapter.notifyDataSetChanged()
            viewModel.getHistoryList()
        }
        binding.updateTrackListButton.setOnClickListener {
            viewModel.searchRequest(
                binding.inputEditText.text.toString() ?: ""
            )
            placeholderDisable()
        }
        binding.clearHistoryButton.setOnClickListener {
            viewModel.clearHistory()
            historyAdapter.notifyDataSetChanged()
            historyElementsDisable()
        }
        //endregion

        //region Everything about TextWatcher
        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.searchDebounce(
                    changedText = s?.toString() ?: ""
                )
                showClearTextButtonOrNot()
            }
            override fun afterTextChanged(s: Editable?) {}
        }
        binding.inputEditText.addTextChangedListener(textWatcher)
        //endregion

        //region Creating a list of tracks by using RecyclerView
        binding.trackSearchRecyclerView.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        binding.trackSearchRecyclerView.adapter = searchAdapter
        binding.historyRecyclerView.layoutManager=
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        binding.historyRecyclerView.adapter = historyAdapter
        //endregion

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun showTrackAudioPlayer(track: Track){
        val bundle = Bundle().apply { putParcelable(AudioPlayerFragment.Companion.TRACK_INFORMATION_KEY,track) }
        findNavController().navigate(
            R.id.action_searchFragment_to_audioPlayerFragment,
            bundle)

    }
    private fun clickOnTrackDebounce() : Boolean {
        val current = isClickOnTrackAllowed
        if (isClickOnTrackAllowed) {
            isClickOnTrackAllowed = false
            mainThreadHandler.postDelayed({ isClickOnTrackAllowed = true },
                CLICK_ON_TRACK_DEBOUNCE_DELAY
            )
        }
        return current
    }

    //region Elements activation methods
    private fun placeholderDisable(){
        binding.apply {
            placeholderMessage.isVisible = false
            placeholderIconSmthWrong.isVisible = false
            placeholderIconNothing.isVisible = false
            updateTrackListButton.isVisible = false
        }
    }
    private fun historyElementsDisable(){
        binding.apply {
            historyLayout.isVisible = false
            historyTitleTextView.isVisible = false
            historyRecyclerView.isVisible = false
            clearHistoryButton.isVisible = false
        }
    }
    private fun historyElementsVisible(){
        binding.apply {
            historyLayout.isVisible = true
            historyTitleTextView.isVisible = true
            historyRecyclerView.isVisible = true
            clearHistoryButton.isVisible = true
        }
    }
    //endregion
    fun render(state: SearchState) {
        when (state) {
            is SearchState.Loading -> showLoading()
            is SearchState.Content -> showContent(state.tracks)
            is SearchState.Error -> showError(state.errorMessage)
            is SearchState.Empty -> showEmpty(state.message)
            is SearchState.History -> showHistory(state.tracks)
        }
    }
    fun showLoading() {
        binding.progressBar.isVisible = true
        binding.trackSearchRecyclerView.isVisible = false
        placeholderDisable()
        historyElementsDisable()
    }

    fun showContent(trackList: List<Track>) {
        binding.trackSearchRecyclerView.isVisible = true
        placeholderDisable()
        historyElementsDisable()
        binding.progressBar.isVisible = false

        searchAdapter.trackList.clear()
        searchAdapter.trackList.addAll(trackList)
        searchAdapter.notifyDataSetChanged()
    }

    fun showError(errorMessage: String) {
        searchAdapter.trackList.clear()
        searchAdapter.notifyDataSetChanged()
        historyElementsDisable()
        binding.apply {
            trackSearchRecyclerView.isVisible = false
            progressBar.isVisible = false
            placeholderMessage.isVisible = true
            placeholderMessage.text = errorMessage
            placeholderIconSmthWrong.isVisible = true
            updateTrackListButton.isVisible = true
        }
    }

    fun showEmpty(emptyMessage: String) {
        searchAdapter.trackList.clear()
        searchAdapter.notifyDataSetChanged()
        historyElementsDisable()
        binding.apply {
            trackSearchRecyclerView.isVisible = false
            progressBar.isVisible = false
            placeholderMessage.isVisible = true
            placeholderMessage.text =  emptyMessage
            placeholderIconNothing.isVisible= true
        }
    }

    fun showHistory(historyTrackList: List<Track>){
        if(binding.inputEditText.hasFocus()
            && historyTrackList.isNotEmpty()&& binding.inputEditText.text.isEmpty()
        ){
            placeholderDisable()
            historyElementsVisible()
            binding.apply {
                trackSearchRecyclerView.isVisible = false
                progressBar.isVisible = false
            }
            historyAdapter.trackList.clear()
            historyAdapter.trackList.addAll(historyTrackList)
            historyAdapter.notifyDataSetChanged()

        }
        else{
            historyElementsDisable()
        }
    }
    private fun showClearTextButtonOrNot(){
        binding.clearIcon.isVisible = !binding.inputEditText.text.isNullOrEmpty()
    }

    companion object {
        private const val CLICK_ON_TRACK_DEBOUNCE_DELAY = 1000L
    }
}