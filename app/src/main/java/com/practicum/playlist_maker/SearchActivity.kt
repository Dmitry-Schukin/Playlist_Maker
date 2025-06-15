package com.practicum.playlist_maker

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
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
import com.practicum.playlist_maker.presentation.search.TrackSearchAdapter


class SearchActivity : AppCompatActivity() {
    private var savedValue: String = TEXT_DEF

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

        val backClickEvent = findViewById<MaterialToolbar>(R.id.search_material_toolbar)
        val inputEditText = findViewById<EditText>(R.id.input_edit_text)
        val clearButton = findViewById<ImageView>(R.id.clearIcon)

        clearButton.visibility = if(savedValue.isEmpty()) View.GONE else View.VISIBLE

        backClickEvent.setNavigationOnClickListener {
            finish()
        }

        clearButton.setOnClickListener{
            inputEditText.setText("")
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(inputEditText.windowToken, 0)
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // empty
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                savedValue = inputEditText.text.toString()
                clearButton.isVisible = !s.isNullOrEmpty()
            }
            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        }

        inputEditText.addTextChangedListener(simpleTextWatcher)
        inputEditText.setText(savedValue)

        //region Creating a list of tracks by using RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.track_search_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false)

        val searchAdapter = TrackSearchAdapter(trackListCreatorMockObject())
        recyclerView.adapter = searchAdapter
        //endregion
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(EDIT_TEXT, savedValue)
    }
    companion object {
        const val EDIT_TEXT = "EDIT_TEXT"
        const val TEXT_DEF = ""
    }
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        savedValue = savedInstanceState.getString(EDIT_TEXT, TEXT_DEF)
    }

    private fun trackListCreatorMockObject(): MutableList<Track> {
        val first = Track(getStringFromXml(applicationContext,R.string.first_track), getStringFromXml(applicationContext,R.string.first_group),
            getStringFromXml(applicationContext,R.string.first_track_time), getStringFromXml(applicationContext,R.string.first_album_url))

        val second = Track(getStringFromXml(applicationContext,R.string.second_track), getStringFromXml(applicationContext,R.string.second_group),
            getStringFromXml(applicationContext,R.string.second_track_time), getStringFromXml(applicationContext,R.string.second_album_url))

        val third = Track(getStringFromXml(applicationContext,R.string.third_track), getStringFromXml(applicationContext,R.string.third_group),
            getStringFromXml(applicationContext,R.string.third_track_time), getStringFromXml(applicationContext,R.string.third_album_url))

        val fourth= Track(getStringFromXml(applicationContext,R.string.fourth_track), getStringFromXml(applicationContext,R.string.fourth_group),
            getStringFromXml(applicationContext,R.string.fourth_track_time), getStringFromXml(applicationContext,R.string.fourth_album_url))

        val fifth= Track(getStringFromXml(applicationContext,R.string.fifth_track), getStringFromXml(applicationContext,R.string.fifth_group),
            getStringFromXml(applicationContext,R.string.fifth_track_time), getStringFromXml(applicationContext,R.string.fifth_album_url))

        val trackList: MutableList<Track> = mutableListOf(first,second,third,fourth,fifth)

        return trackList
    }
    fun getStringFromXml(context: Context, resId: Int): String {
        return context.resources.getString(resId)
    }
}