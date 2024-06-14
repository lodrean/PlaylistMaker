package com.practicum.playlistmaker.new_playlist.ui

import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.new_playlist.domain.NewPlaylistInteractor
import com.practicum.playlistmaker.util.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NewPlayLIstViewModel(
    private val newPlaylistInteractor: NewPlaylistInteractor
) : ViewModel() {


    private var imageUri: String = ""

    private val showToast = SingleLiveEvent<String>()
    private val stateLiveData = MutableLiveData<NewPlaylistState>()
    fun observeState(): LiveData<NewPlaylistState> = stateLiveData
    fun observeShowToast(): LiveData<String> = showToast


    fun allowCreation() {
        val creationIsAllowed = true
        stateLiveData.postValue(
            NewPlaylistState.Creation(
                creationIsAllowed,
                imageUri.toUri()
            )
        )
    }

    fun banCreation() {
        val creationIsAllowed = false
        stateLiveData.postValue(
            NewPlaylistState.Creation(
                creationIsAllowed,
                imageUri.toUri()
            )
        )
    }

    fun setImage(uri: Uri) {
        imageUri = uri.toString()
        newPlaylistInteractor.saveImage(uri.toString())
        stateLiveData.postValue(NewPlaylistState.Creation(true, uri))
    }

    fun deleteImage() {
        stateLiveData.postValue(NewPlaylistState.Creation(false, "".toUri()))
    }

    fun createPlaylist(playlistName: String, description: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                newPlaylistInteractor.createPlaylist(
                    newPlaylistInteractor.getImage().toString(),
                    playlistName,
                    description
                )
            }
        }
        showToast("«Плейлист $playlistName создан")
    }

    private fun showToast(message: String) {
        showToast.postValue(message)
    }
}