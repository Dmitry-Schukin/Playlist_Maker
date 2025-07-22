package com.practicum.playlist_maker

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
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
import com.practicum.playlist_maker.model.Track
import com.practicum.playlist_maker.network.track_list.TrackApi
import com.practicum.playlist_maker.network.track_list.TrackResponse
import com.practicum.playlist_maker.presentation.search.SearchHistory
import com.practicum.playlist_maker.presentation.search.TrackSearchAdapter
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.Callback
import retrofit2.Response
import retrofit2.converter.gson.GsonConverterFactory



class SearchActivity : AppCompatActivity() {
    companion object {
        const val EDIT_TEXT = "EDIT_TEXT"
        const val TEXT_DEF = ""
    }

    private var savedValue: String = TEXT_DEF

    private val trackBaseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(trackBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val trackService = retrofit.create(TrackApi::class.java)
    private val tracks = ArrayList<Track>()
    private var historyTrackList =ArrayList<Track>()

    private val searchAdapter = TrackSearchAdapter {
        val historySharedPref = SearchHistory(getSharedPreferences(SearchHistory.TRACK_HISTORY, MODE_PRIVATE))
        historySharedPref.addTrackToHistoryList(it)
    }
    private val historyAdapter = TrackSearchAdapter{
        testTrackMethod(it)
    }

    private lateinit var backClickEvent:MaterialToolbar
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
    private lateinit var historySharedPreferences: SharedPreferences

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
        historySharedPreferences = getSharedPreferences(SearchHistory.TRACK_HISTORY, MODE_PRIVATE)

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
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
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
            val clear = SearchHistory(historySharedPreferences)
            clear.clearHistory()
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
        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE ) {
                search()
                historyElementsDisable()
                true
            }
            false
        } //Handling keyboard button presses
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
        recyclerView.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false)

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
        trackService.getTrack(inputEditText.text.toString())
            .enqueue(object : Callback<TrackResponse> {
                override fun onResponse(call: Call<TrackResponse>,
                                        response: Response<TrackResponse>) {
                    if(response.isSuccessful) {
                        val songs = response.body()?.songs
                        if (songs?.isNotEmpty() == true) {
                            tracks.clear()
                            tracks.addAll(songs)
                            searchAdapter.notifyDataSetChanged()
                            showMessage("")
                            } else {
                                showMessage(getString(R.string.nothing_was_found))
                            }
                    } else {
                        showMessage(getString(R.string.problem_with_network))
                    }

                }

                override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                    showMessage(getString(R.string.problem_with_network))
                }
            })
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
        val history = SearchHistory(historySharedPreferences)
        val trackListFromSharedPrefs = history.createTracksListFromJson()
        if (trackListFromSharedPrefs.isNotEmpty()) {
            historyElementsVisible()
            historyTrackList = trackListFromSharedPrefs

            historyRecyclerView = findViewById<RecyclerView>(R.id.history_recycler_view)
            historyRecyclerView.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false)
            historyAdapter.trackList = historyTrackList
            historyRecyclerView.adapter = historyAdapter
        } else {
            historyElementsDisable()
        }
    }
    //region Elements activity methods
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

    //region Test methods
    private fun trackListCreatorMockObject(): MutableList<Track> {
        val first = Track("1",getStringFromXml(applicationContext,R.string.first_group),getStringFromXml(applicationContext,R.string.first_track),
            301000, getStringFromXml(applicationContext,R.string.first_album_url))

        val second = Track("2",getStringFromXml(applicationContext,R.string.second_group),getStringFromXml(applicationContext,R.string.second_track),
            275000, getStringFromXml(applicationContext,R.string.second_album_url))

        val third = Track("3",getStringFromXml(applicationContext,R.string.third_group),getStringFromXml(applicationContext,R.string.third_track),
            250000, getStringFromXml(applicationContext,R.string.third_album_url))

        val fourth= Track("4",getStringFromXml(applicationContext,R.string.fourth_group), getStringFromXml(applicationContext,R.string.fourth_track),
            333000, getStringFromXml(applicationContext,R.string.fourth_album_url))

        val fifth= Track("5",getStringFromXml(applicationContext,R.string.fifth_group), getStringFromXml(applicationContext,R.string.fifth_track),
            303000, getStringFromXml(applicationContext,R.string.fifth_album_url))

        val trackList: MutableList<Track> = mutableListOf(first,second,third,fourth,fifth)

        return trackList
    }
    private fun testTrackMethod(track: Track){}
    //endregion
}