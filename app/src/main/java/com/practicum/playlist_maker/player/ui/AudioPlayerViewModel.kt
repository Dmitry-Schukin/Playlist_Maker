package com.practicum.playlist_maker.player.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlist_maker.library.domain.api.FavoritesDBInteractor
import com.practicum.playlist_maker.library.domain.api.PlaylistInteractor
import com.practicum.playlist_maker.library.domain.model.Playlist
import com.practicum.playlist_maker.player.domain.api.PlayerInteractor
import com.practicum.playlist_maker.playlist_tracks.domain.PlaylistTracksInteractor
import com.practicum.playlist_maker.search.domain.model.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale


class AudioPlayerViewModel (track: Track,
                            private val playerInteractor: PlayerInteractor,
                            private val favoritesDbInteractor: FavoritesDBInteractor,
                            private val playlistDbInteractor: PlaylistInteractor,
                            private val playlistTracksDbInteractor: PlaylistTracksInteractor
): ViewModel() {
    companion object {
        private const val UPDATE_DELAY = 300L
    }
    private var url = track.previewUrl
    private var currentTrack:Track = track
    private var currentFavoriteStatus: Boolean = track.isFavorite
    private var currentPlaylistStatus: AddingToPlaylistState = AddingToPlaylistState.Default
    private val currentPlaylists= mutableListOf<Playlist>()
    private var currentPlaylistId= -1L
    private var timerJob: Job? = null


    //region LiveData and Observers
    private val stateLiveData=
        MutableLiveData<StateController>(StateController.Default(
            currentFavoriteStatus,
            currentPlaylistStatus,
            currentPlaylists))
    fun observeState(): LiveData<StateController> = stateLiveData
    //endregion

    init {
        preparePlayer()
        checkTrackInFavoritesList()
        checkTrackInSomePlaylist()
    }

    override fun onCleared() {
        super.onCleared()
        playerInteractor.releasePlayer()
    }
    //region Player State Functions
    fun onPlayButtonClicked() {
        when(stateLiveData.value) {
            is StateController.Playing -> {
                pausePlayer()
            }
            is StateController.Prepared, is StateController.Paused -> {
                startPlayer()
            }
            else -> {}
        }
    }
    fun preparePlayer(){
        playerInteractor.preparePlayer(url,
            {
                stateLiveData.postValue(StateController.Prepared(
                    currentFavoriteStatus,
                    currentPlaylistStatus,
                    currentPlaylists))
            },
            {
                timerJob?.cancel()
                stateLiveData.postValue(StateController.Prepared(
                    currentFavoriteStatus,
                    currentPlaylistStatus,
                    currentPlaylists))
            })
    }

    fun startPlayer() {
        playerInteractor.startPlayer()
        stateLiveData.postValue(StateController.Playing(getCurrentPlayerPosition(),
            currentFavoriteStatus,
            currentPlaylistStatus,
            currentPlaylists))
        startTimerUpdate()
    }

    private fun startTimerUpdate() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (playerInteractor.isPlaying()) {
                delay(UPDATE_DELAY)
                stateLiveData.postValue(StateController.Playing(getCurrentPlayerPosition(),
                    currentFavoriteStatus,
                    currentPlaylistStatus,
                    currentPlaylists))
            }
        }
    }
    fun pausePlayer() {
        playerInteractor.pausePlayer()
        timerJob?.cancel()
        stateLiveData.postValue(
            StateController.Paused(
                getCurrentPlayerPosition(),
                currentFavoriteStatus,
                currentPlaylistStatus,
                currentPlaylists
            )
        )
    }
    private fun getCurrentPlayerPosition(): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(playerInteractor.getCurrentAudioTime())
    }

    fun release(){
        playerInteractor.stop()
        playerInteractor.releasePlayer()
        stateLiveData.value = StateController.Default(
            currentFavoriteStatus,
            currentPlaylistStatus,
            currentPlaylists)
    }
    fun onPause(){
        pausePlayer()
    }

    //endregion

    //region Favorite Button Functions
    fun onFavoriteButtonClicked() {
        when(currentFavoriteStatus) {
            true -> {
                deleteTrackFromFavorites()
                currentFavoriteStatus = false
                val currentState = stateLiveData.value
                currentState?.isFavorite = false
                stateLiveData.postValue(currentState!!)
            }
            else -> {
                makeTrackFavorite()
                currentFavoriteStatus = true
                val currentState = stateLiveData.value
                currentState?.isFavorite = true
                stateLiveData.postValue(currentState!!)

            }
        }
    }
    fun makeTrackFavorite(){
        viewModelScope.launch {
            favoritesDbInteractor.makeTrackAsFavorite(currentTrack)
        }
    }
    fun deleteTrackFromFavorites(){
        viewModelScope.launch {
            favoritesDbInteractor.deleteTrackFromFavorites(currentTrack)
        }
    }
    fun checkTrackInFavoritesList(){
        viewModelScope.launch {
            favoritesDbInteractor.getAllFavoriteTracks().collect {
                tracks ->
                val currentState = stateLiveData.value
                if(!tracks.first.isNullOrEmpty()) {
                    val isFavorite = tracks.first?.any { it.trackId == currentTrack.trackId }

                    currentTrack.isFavorite = isFavorite!!
                    currentFavoriteStatus = isFavorite

                    currentState?.isFavorite = isFavorite
                    stateLiveData.postValue(currentState!!)
                }else{
                    currentFavoriteStatus = false
                    currentState?.isFavorite = false
                    stateLiveData.postValue(currentState!!)
                }
            }
        }
    }
    //endregion

    //region Functions for working with playlists

    fun requestPlaylists() {
        viewModelScope.launch {
            playlistDbInteractor
                .getPlaylists()
                .catch { exception -> Log.d("FlowGettingPlaylistsError", "$exception") }
                .collect { pair ->
                    val playlists = mutableListOf<Playlist>()
                    if (pair.first != null) {
                        playlists.addAll(pair.first!!)
                    }
                    when {
                        playlists.isEmpty() -> {
                            currentPlaylists.clear()
                            val currentState = stateLiveData.value
                            currentState?.playlists = currentPlaylists
                            stateLiveData.postValue(currentState!!)
                            Log.d("PlaylistMassage","${pair.second}")
                        }
                        else -> {
                            currentPlaylists.clear()
                            currentPlaylists.addAll(playlists)
                            val currentState = stateLiveData.value
                            currentState?.playlists = currentPlaylists
                            stateLiveData.postValue(currentState!!)
                        }
                    }
                }
        }
    }

    fun checkAndAddTrackInChosenPlaylist(playlist:Playlist){
        if(currentPlaylistId==-1L){
            addTrackInPlaylist(playlist)
            currentPlaylistId = playlist.playlistId
            currentPlaylistStatus = AddingToPlaylistState.JustAddedInPlaylist(currentPlaylistId)
            val currentLiveDataState = stateLiveData.value
            currentLiveDataState?.inPlaylist = currentPlaylistStatus
            stateLiveData.postValue(currentLiveDataState!!)
        }else{
            currentPlaylistStatus = AddingToPlaylistState.AlreadyInPlaylist(currentPlaylistId)
            val currentLiveDataState = stateLiveData.value
            currentLiveDataState?.inPlaylist = currentPlaylistStatus
            stateLiveData.postValue(currentLiveDataState!!)
        }

    }

    fun checkTrackInSomePlaylist(){
        viewModelScope.launch {
            playlistTracksDbInteractor
                .isThereTrackInSomePlaylist(currentTrack.trackId)
                .catch { exception -> Log.d("FlowGettingPlaylistIdError", "$exception") }
                .collect { pair ->
                    val playlistIds = mutableListOf<Long>()
                    if (pair.first != null) {
                        playlistIds.addAll(pair.first!!)
                    }
                    when{
                        playlistIds.isEmpty()->{
                            currentPlaylistId = -1L

                        }else -> {
                            val playlistId = playlistIds.first()
                            currentPlaylistId = playlistId
                        }
                    }
                }
        }
    }

    fun addTrackInPlaylist(playlist: Playlist){
        viewModelScope.launch {
            val newPlaylist = Playlist.addTrackToPlaylist(playlist,currentTrack.trackId)
            playlistDbInteractor.updateTrackListAndCount(newPlaylist)
            playlistTracksDbInteractor.insertNewTrackIntoPlaylist(currentTrack,playlist.playlistId)
        }
    }

    fun getPlaylistNameWhereTrackWasAdded(): String{
        val playlistTitle = mutableListOf<Playlist>()
        currentPlaylists.forEach {
            item -> if(item.playlistId == currentPlaylistId) playlistTitle.add(item)
        }
        if(playlistTitle.size==0){
            return ""
        }else{
            return playlistTitle.first().playlistName
        }


    }

    fun returnDefaultState(){
        currentPlaylistStatus = AddingToPlaylistState.Default
        val currentState = stateLiveData.value
        currentState?.inPlaylist = AddingToPlaylistState.Default
        stateLiveData.postValue(currentState!!)
    }
    //endregion

}