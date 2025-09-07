package com.practicum.playlist_maker.presentation.ui.search

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.gson.Gson
import com.practicum.playlist_maker.R
import com.practicum.playlist_maker.creator.Creator
import com.practicum.playlist_maker.data.HistorySharedPrefsManager
import com.practicum.playlist_maker.domain.RequestResult
import com.practicum.playlist_maker.domain.Track
import com.practicum.playlist_maker.domain.api.TrackInteractor
import com.practicum.playlist_maker.domain.use_case.HistorySharedPrefsUseCase
import com.practicum.playlist_maker.presentation.ui.audio_player.AudioPlayerActivity

class SearchActivity : AppCompatActivity() {
    companion object {
        const val EDIT_TEXT = "EDIT_TEXT"
        const val TEXT_DEF = ""
        private const val CLICK_ON_TRACK_DEBOUNCE_DELAY = 1000L
        private const val SEARCH_TRACK_DEBOUNCE_DELAY = 2000L
    }


    private var savedValue: String = TEXT_DEF
    private val tracks = ArrayList<Track>()
    private var historyTrackList = ArrayList<Track>()


    private var isClickOnTrackAllowed = true
    private val mainThreadHandler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { search() }
    private val getTrackListInteractor = Creator.provideTracksInteractor()


    private lateinit var backClickEvent: MaterialToolbar
    private lateinit var inputEditText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var placeholderMessage: TextView
    private lateinit var placeholderIconSearchNothing: ImageView
    private lateinit var placeholderIconNoConnection: ImageView
    private lateinit var updateButton: Button
    private lateinit var historyLayout: LinearLayout
    private lateinit var historyTitle: TextView
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var clearHistoryButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var historyUseCase: HistorySharedPrefsUseCase

    //region Adapters initialization
    private val searchAdapter = TrackSearchAdapter {
        if (clickOnTrackDebounce()) {
            historyUseCase.executeAddingNewTrack(it)
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
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.search_layout)) { view, insets ->
            val statusBar = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            view.updatePadding(top = statusBar.top)
            insets
        }

        backClickEvent = findViewById<MaterialToolbar>(R.id.search_material_toolbar)
        inputEditText = findViewById<EditText>(R.id.input_edit_text)
        clearButton = findViewById<ImageView>(R.id.clearIcon)
        placeholderMessage = findViewById<TextView>(R.id.placeholder_message)
        placeholderIconSearchNothing = findViewById<ImageView>(R.id.placeholder_icon_nothing)
        placeholderIconNoConnection= findViewById<ImageView>(R.id.placeholder_icon_smth_wrong)
        updateButton = findViewById<Button>(R.id.update_track_list_button)
        historyLayout = findViewById<LinearLayout>(R.id.history_layout)
        historyTitle = findViewById<TextView>(R.id.history_title_text_view)
        historyRecyclerView = findViewById<RecyclerView>(R.id.history_recycler_view)
        clearHistoryButton = findViewById<Button>(R.id.clear_history_button)
        progressBar = findViewById<ProgressBar>(R.id.progressBar)
        historyUseCase = HistorySharedPrefsUseCase(HistorySharedPrefsManager(this))

        //region Starting settings for displaying screen elements
        clearButton.visibility = if(savedValue.isEmpty()) View.GONE else View.VISIBLE
        placeholderDisable()
        inputEditText.requestFocus()//focus on EditText
        showTrackHistory()
        //endregion

        //region Listeners
        backClickEvent.setNavigationOnClickListener {
            finish()
        }
        clearButton.setOnClickListener{
            inputEditText.setText("")
            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(inputEditText.windowToken, 0) //Hide keyboard
            tracks.clear()
            searchAdapter.notifyDataSetChanged()
            placeholderDisable()
            showTrackHistory()
        }
        updateButton.setOnClickListener {
            search()
            placeholderDisable()
        }
        clearHistoryButton.setOnClickListener {
            historyUseCase.executeClear()
            historyTrackList.clear()
            historyAdapter.notifyDataSetChanged()
            historyElementsDisable()
        }
        inputEditText.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus && inputEditText.text.isEmpty()) {
                placeholderDisable()
                showTrackHistory()
            } else {
                historyElementsDisable()
            }
        }
        //endregion


        //region Everything about TextWatcher
        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // empty
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                savedValue = inputEditText.text.toString()
                clearButton.isVisible = !s.isNullOrEmpty()

                if (inputEditText.hasFocus() && s?.isEmpty() == true) {
                    tracks.clear()
                    searchAdapter.notifyDataSetChanged()
                    placeholderDisable()
                    showTrackHistory()
                } else {
                    historyElementsDisable()
                    searchTrackDebounce()
                }
            }
            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        }
        inputEditText.addTextChangedListener(simpleTextWatcher)
        //endregion
        inputEditText.setText(savedValue)//Storing a value for reuse

        //region Creating a list of tracks by using RecyclerView
        recyclerView = findViewById<RecyclerView>(R.id.track_search_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        searchAdapter.trackList = tracks
        recyclerView.adapter = searchAdapter
        //endregion


    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(EDIT_TEXT, savedValue)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        savedValue = savedInstanceState.getString(EDIT_TEXT, TEXT_DEF)
    }
    fun getStringFromXml(context: Context, resId: Int): String {
        return context.resources.getString(resId)
    }
    private fun search() {
        if(inputEditText.text.isNotEmpty()) {

            progressBar.isVisible = true
            recyclerView.isVisible = false
            placeholderDisable()

            getTrackListInteractor.searchTrack(
                inputEditText.text.toString(),
                object : TrackInteractor.TracksConsumer {

                    override fun consume(foundTracks: RequestResult) {
                        mainThreadHandler.post {
                            progressBar.isVisible=false
                            if (foundTracks.success == true) {
                                val songs = foundTracks.trackList

                                if (songs.isNotEmpty() == true) {
                                    tracks.clear()
                                    tracks.addAll(songs)

                                    recyclerView.isVisible = true
                                    searchAdapter.notifyDataSetChanged()
                                    showMessage("")
                                } else {
                                    showMessage(getString(R.string.nothing_was_found))
                                }
                            } else {
                                showMessage(getString(R.string.problem_with_network))
                            }
                        }
                    }
                })
        }
    }

    private fun showMessage(text: String) {
        if (text.isNotEmpty()) {
            placeholderMessage.visibility = View.VISIBLE

            tracks.clear()
            searchAdapter.notifyDataSetChanged()

            placeholderMessage.text = text

            if(text.equals(getStringFromXml(applicationContext, R.string.problem_with_network))) {
                placeholderIconNoConnection.visibility= View.VISIBLE
                updateButton.visibility = View.VISIBLE
            }else if (text.equals(getStringFromXml(applicationContext, R.string.nothing_was_found))) {
                placeholderIconSearchNothing.visibility = View.VISIBLE
            }
        } else {
            placeholderDisable()
        }
    }
    private fun showTrackHistory() {
        val trackListFromSharedPrefs = historyUseCase.executeGettingHistoryList()
        if (trackListFromSharedPrefs.isNotEmpty()) {
            historyElementsVisible()
            historyTrackList = trackListFromSharedPrefs as ArrayList<Track>

            historyRecyclerView = findViewById<RecyclerView>(R.id.history_recycler_view)
            historyRecyclerView.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            historyAdapter.trackList = historyTrackList
            historyRecyclerView.adapter = historyAdapter
        } else {
            historyElementsDisable()
        }
    }
    private fun showTrackAudioPlayer(track: Track){
        val displayIntent = Intent(this, AudioPlayerActivity::class.java)
        val jsonTrack = Gson().toJson(track)
        displayIntent.putExtra(AudioPlayerActivity.Companion.TRACK_INFORMATION_KEY, jsonTrack)
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
    private fun searchTrackDebounce() {
        mainThreadHandler.removeCallbacks(searchRunnable)
        mainThreadHandler.postDelayed(searchRunnable, SEARCH_TRACK_DEBOUNCE_DELAY)
    }
    //region Elements activation methods
    private fun placeholderDisable(){
        placeholderMessage.isVisible=false
        placeholderIconNoConnection.isVisible=false
        placeholderIconSearchNothing.isVisible=false
        updateButton.isVisible=false
    }
    private fun historyElementsDisable(){
        historyLayout.isVisible=false
        historyTitle.isVisible=false
        historyRecyclerView.isVisible=false
        clearHistoryButton.isVisible=false
    }
    private fun historyElementsVisible(){
        historyLayout.isVisible=true
        historyTitle.isVisible=true
        historyRecyclerView.isVisible=true
        clearHistoryButton.isVisible=true
    }
    //endregion
}