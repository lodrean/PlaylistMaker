package com.practicum.playlistmaker.player.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.player.domain.AudioPlayerInteractor
import com.practicum.playlistmaker.player.domain.AudioPlayerState

class AudioPlayerViewModel(
    private val trackId: String,
    private val mediaPlayer: AudioPlayerInteractor
) : ViewModel() {

    private val playStatusLiveData = MutableLiveData<AudioPlayerState>()

}