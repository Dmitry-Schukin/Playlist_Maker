package com.practicum.playlist_maker.search.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlist_maker.databinding.ActivitySearchBinding
import com.practicum.playlist_maker.search.domain.model.Track
import com.practicum.playlist_maker.player.ui.AudioPlayerActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchActivity : AppCompatActivity() {
    companion object {
        private const val CLICK_ON_TRACK_DEBOUNCE_DELAY = 1000L
    }

    private var isClickOnTrackAllowed = true
    private val mainThreadHandler = Handler(Looper.getMainLooper())
    private lateinit var bindingSearchActivity: ActivitySearchBinding
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

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingSearchActivity = ActivitySearchBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(bindingSearchActivity.root)
        ViewCompat.setOnApplyWindowInsetsListener(bindingSearchActivity.root) { view, insets ->
            val statusBar = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            view.updatePadding(top = statusBar.top)
            insets
        }

        //region Observer
        viewModel.observeState().observe(this) {
            render(it)
        }
        //endregion

        //region Starting settings for displaying screen elements
        showClearTextButtonOrNot()
        placeholderDisable()
        bindingSearchActivity.inputEditText.requestFocus()//focus on EditText
        viewModel.getHistoryList()
        //endregion

        //region Listeners
        bindingSearchActivity.searchMaterialToolbar.setNavigationOnClickListener {
            finish()
        }
        bindingSearchActivity.clearIcon.setOnClickListener{
            bindingSearchActivity.apply {
                inputEditText.setText("")
                trackSearchRecyclerView.isVisible =false
            }
            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(bindingSearchActivity.inputEditText.windowToken, 0) //Hide keyboard
            searchAdapter.trackList.clear()
            searchAdapter.notifyDataSetChanged()
            viewModel.getHistoryList()
        }
        bindingSearchActivity.updateTrackListButton.setOnClickListener {
            viewModel.searchRequest(
                bindingSearchActivity.inputEditText.text.toString() ?: ""
            )
            placeholderDisable()
        }
        bindingSearchActivity.clearHistoryButton.setOnClickListener {
            viewModel.clearHistory()
            historyAdapter.notifyDataSetChanged()
            historyElementsDisable()
        }
        //endregion

        //region Everything about TextWatcher
        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel?.searchDebounce(
                    changedText = s?.toString() ?: ""
                )
                showClearTextButtonOrNot()
            }
            override fun afterTextChanged(s: Editable?) {}
        }
        bindingSearchActivity.inputEditText.addTextChangedListener(textWatcher)
        //endregion


        //region Creating a list of tracks by using RecyclerView
        bindingSearchActivity.trackSearchRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        bindingSearchActivity.trackSearchRecyclerView.adapter = searchAdapter
        bindingSearchActivity.historyRecyclerView.layoutManager=
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        bindingSearchActivity.historyRecyclerView.adapter = historyAdapter
        //endregion
    }


    private fun showTrackAudioPlayer(track: Track){
        val displayIntent = Intent(this, AudioPlayerActivity::class.java)
        displayIntent.putExtra(AudioPlayerActivity.Companion.TRACK_INFORMATION_KEY, track)
        startActivity(displayIntent)
    }
    private fun clickOnTrackDebounce() : Boolean {
        val current = isClickOnTrackAllowed
        if (isClickOnTrackAllowed) {
            isClickOnTrackAllowed = false
            mainThreadHandler.postDelayed({ isClickOnTrackAllowed = true }, CLICK_ON_TRACK_DEBOUNCE_DELAY)
        }
        return current
    }

    //region Elements activation methods
    private fun placeholderDisable(){
        bindingSearchActivity.apply {
            placeholderMessage.isVisible = false
            placeholderIconSmthWrong.isVisible = false
            placeholderIconNothing.isVisible = false
            updateTrackListButton.isVisible = false
        }
    }
    private fun historyElementsDisable(){
        bindingSearchActivity.apply {
            historyLayout.isVisible = false
            historyTitleTextView.isVisible = false
            historyRecyclerView.isVisible = false
            clearHistoryButton.isVisible = false
        }
    }
    private fun historyElementsVisible(){
        bindingSearchActivity.apply {
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
        bindingSearchActivity.progressBar.isVisible = true
        bindingSearchActivity.trackSearchRecyclerView.isVisible = false
        placeholderDisable()
        historyElementsDisable()
    }

    fun showContent(trackList: List<Track>) {
        bindingSearchActivity.trackSearchRecyclerView.isVisible = true
        placeholderDisable()
        historyElementsDisable()
        bindingSearchActivity.progressBar.isVisible = false

        searchAdapter.trackList.clear()
        searchAdapter.trackList.addAll(trackList)
        searchAdapter.notifyDataSetChanged()
    }

    fun showError(errorMessage: String) {
        searchAdapter.trackList.clear()
        searchAdapter.notifyDataSetChanged()
        historyElementsDisable()
        bindingSearchActivity.apply {
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
        bindingSearchActivity.apply {
            trackSearchRecyclerView.isVisible = false
            progressBar.isVisible = false
            placeholderMessage.isVisible = true
            placeholderMessage.text =  emptyMessage
            placeholderIconNothing.isVisible= true
        }
    }

    fun showHistory(historyTrackList: List<Track>){
        if(bindingSearchActivity.inputEditText.hasFocus()
                    && historyTrackList.isNotEmpty()&& bindingSearchActivity.inputEditText.text.isEmpty()
        ){
            placeholderDisable()
            historyElementsVisible()
            bindingSearchActivity.apply {
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
        bindingSearchActivity.clearIcon.isVisible = !bindingSearchActivity.inputEditText.text.isNullOrEmpty()
    }
    fun getStringFromXml(context: Context, resId: Int): String {
        return context.resources.getString(resId)
    }
}