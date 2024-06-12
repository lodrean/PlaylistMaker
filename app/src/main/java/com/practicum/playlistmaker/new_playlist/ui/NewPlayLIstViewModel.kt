package com.practicum.playlistmaker.new_playlist.ui

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.new_playlist.domain.NewPlaylistInteractor
import com.practicum.playlistmaker.util.SingleLiveEvent

class NewPlayLIstViewModel(
    private val newPlaylistInteractor: NewPlaylistInteractor
) : ViewModel() {
    private lateinit var imageUri : Uri
    private var creationIsAllowed: Boolean = false
    private val showToast = SingleLiveEvent<String>()
    private val stateLiveData = MutableLiveData<NewPlaylistState>()
    fun observeState(): LiveData<NewPlaylistState> = stateLiveData
    fun observeShowToast(): LiveData<String> = showToast


    fun allowCreation() {
        creationIsAllowed = true
        stateLiveData.postValue(NewPlaylistState.Creation(creationIsAllowed))
    }

    fun banCreation() {
        creationIsAllowed = false
        stateLiveData.postValue(NewPlaylistState.Creation(creationIsAllowed))
    }

}