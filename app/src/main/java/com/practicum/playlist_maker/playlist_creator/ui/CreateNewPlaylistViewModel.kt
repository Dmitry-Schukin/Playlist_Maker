package com.practicum.playlist_maker.playlist_creator.ui

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlist_maker.library.domain.model.Playlist
import com.practicum.playlist_maker.playlist_creator.domain.CreatePlaylistInteractor
import kotlinx.coroutines.launch

class CreateNewPlaylistViewModel (private val interactor: CreatePlaylistInteractor): ViewModel(){

    private var playlistImagePath: String=""
    private var playlistTitle:String = ""
    private var playlistDescription:String = ""
    private val stateLiveData = MutableLiveData<CreatorPlaylistState>(CreatorPlaylistState.EmptyInputField)
    fun observeState(): LiveData<CreatorPlaylistState> = stateLiveData

    fun createNewPlaylist(){
        viewModelScope.launch {
            val newPlaylist = Playlist(0,playlistTitle, playlistDescription,playlistImagePath,emptyList(),0)
            interactor.createNewPlaylist(newPlaylist)
            renderState(CreatorPlaylistState.PlaylistCreated)
        }
    }


    fun updateImagePath(path: String){
        if(playlistTitle.isEmpty()){
            playlistImagePath = path
            renderState(CreatorPlaylistState.EmptyInputField)
        }else{
            playlistImagePath = path
            renderState(CreatorPlaylistState.ReadyToCreate(playlistImagePath,playlistTitle,playlistDescription))
        }
    }
    fun updateDescription(description: String){
        if(playlistTitle.isEmpty()){
            playlistDescription = description
            renderState(CreatorPlaylistState.EmptyInputField)
        }else{
            playlistDescription = description
            renderState(CreatorPlaylistState.ReadyToCreate(playlistImagePath,playlistTitle,playlistDescription))
        }
    }
    fun updateTitle(newTitle:String){
        if(playlistTitle.isEmpty()){
            playlistTitle = newTitle
            renderState(
                CreatorPlaylistState.ReadyToCreate(
                    playlistImagePath,
                    playlistTitle,
                    playlistDescription
                )
            )
        }else {
            playlistTitle = newTitle
            renderState(CreatorPlaylistState.EmptyInputField)
        }
    }

    fun getLatestTitle():String{
        return playlistTitle
    }
    fun getLatestDescription(): String{
        return  playlistDescription
    }
    fun getLatestImageUri(): String{
        return  playlistImagePath
    }
    private fun renderState(state: CreatorPlaylistState) {
        stateLiveData.postValue(state)
    }
    fun showExitDialog(): Boolean{
        return !playlistTitle.isEmpty()||!playlistImagePath.isEmpty()||!playlistDescription.isEmpty()
    }
}