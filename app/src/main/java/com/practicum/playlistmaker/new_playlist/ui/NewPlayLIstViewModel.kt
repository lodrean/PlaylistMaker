package com.practicum.playlistmaker.new_playlist.ui

import android.app.Application
import android.net.Uri
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.new_playlist.domain.NewPlaylistInteractor
import com.practicum.playlistmaker.util.SingleLiveEvent
import kotlinx.coroutines.launch

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
        stateLiveData.postValue(NewPlaylistState.Creation(creationIsAllowed,  newPlaylistInteractor.getImage()))
    }

    fun banCreation() {
        val creationIsAllowed = false
        stateLiveData.postValue(NewPlaylistState.Creation(creationIsAllowed,  newPlaylistInteractor.getImage()))
    }

    fun setImage(uri: Uri) {
        imageUri = uri.toString()
        newPlaylistInteractor.saveImage(uri.toString())
        stateLiveData.postValue(NewPlaylistState.Creation(true, newPlaylistInteractor.getImage()))
    }
    fun createPlaylist( playlistName: String, description: String){
        newPlaylistInteractor.createPlaylist(imageUri.toString(), playlistName, description )
    }

}