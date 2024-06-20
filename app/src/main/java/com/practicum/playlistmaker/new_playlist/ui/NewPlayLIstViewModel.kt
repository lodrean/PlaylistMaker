package com.practicum.playlistmaker.new_playlist.ui

import android.app.Application
import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.new_playlist.domain.PlaylistInteractor
import com.practicum.playlistmaker.util.App
import com.practicum.playlistmaker.util.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NewPlayLIstViewModel(
    application: Application,
    private val playlistInteractor: PlaylistInteractor
) : AndroidViewModel(application) {


    private var imageUri: Uri = "".toUri()

    private val showToast = SingleLiveEvent<String>()
    private val stateLiveData = MutableLiveData<NewPlaylistState>()
    fun observeState(): LiveData<NewPlaylistState> = stateLiveData
    fun observeShowToast(): LiveData<String> = showToast


    fun enableCreation() {
        stateLiveData.postValue(
            NewPlaylistState.Creation(
                true,
                imageUri
            )
        )
    }

    fun disableCreation() {
        val creationIsAllowed = false
        stateLiveData.postValue(
            NewPlaylistState.Creation(
                creationIsAllowed,
                imageUri
            )
        )
    }

    fun setImage(uri: Uri) {
        imageUri = uri
        stateLiveData.postValue(NewPlaylistState.Creation(true, uri))
    }

    fun deleteImage() {
        stateLiveData.postValue(NewPlaylistState.Creation(false, "".toUri()))
    }

    fun createPlaylist(playlistName: String, description: String) {
        viewModelScope.launch {
            playlistInteractor
            withContext(Dispatchers.IO) {
                playlistInteractor.createPlaylist(
                    playlistName,
                    description,
                    imageUri
                )
            }
        }
        showToast(getApplication<App>().getString(R.string.playlist_is_creates, playlistName))
    }

    private fun showToast(message: String) {
        showToast.postValue(message)
    }
}