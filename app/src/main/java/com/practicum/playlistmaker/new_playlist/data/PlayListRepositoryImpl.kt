package com.practicum.playlistmaker.new_playlist.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import androidx.core.net.toUri
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.new_playlist.data.db.PlaylistDbConvertor
import com.practicum.playlistmaker.new_playlist.data.db.PlaylistEntity
import com.practicum.playlistmaker.new_playlist.data.db.PlaylistTrackEntity
import com.practicum.playlistmaker.new_playlist.domain.PlayListRepository
import com.practicum.playlistmaker.new_playlist.domain.Playlist
import com.practicum.playlistmaker.search.domain.Constant.Companion.CHOSEN_PLAYLIST
import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.util.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class PlayListRepositoryImpl(
    private val arguments: Bundle,
    private val context: Context,
    private val appDatabase: AppDatabase,
    private val playlistDbConvertor: PlaylistDbConvertor
) : PlayListRepository {

    private var tracks = mutableListOf<Track>()
    private var filename = ""

    override suspend fun createPlaylist(
        playlistName: String,
        description: String,
        imageUri: String
    ) {

        withContext(Dispatchers.IO) {
            if (imageUri != "") {
                val newImageUri = saveImageToPrivateStorage(imageUri.toUri())
                appDatabase.playlistDao()
                    .insert(
                        PlaylistEntity(
                            0,
                            playlistName,
                            description,
                            newImageUri.toString(),
                            ""
                        )
                    )
            } else {
                appDatabase.playlistDao()
                    .insert(PlaylistEntity(0, playlistName, description, "", ""))
            }
        }
    }


    override fun getPlaylists(): Flow<List<Playlist>> = flow {

        val playlists = withContext(Dispatchers.IO) {
            appDatabase.playlistDao().getPlaylists()
        }
        emit(convertFromPlaylistsEntity(playlists))

    }

    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist): Flow<String> = flow {
        withContext(Dispatchers.IO) {
            val playlistDto = PlaylistDto().map(playlist)
            playlistDto.idList += track.trackId
            playlistDto.tracksCount += 1
            appDatabase.playlistDao().updatePlaylist(playlistDbConvertor.map(playlistDto))
            appDatabase.playlistTrackDao().insert(
                PlaylistTrackEntity(
                    track.trackId,
                    track.trackName,
                    track.artistName,
                    track.url,
                    track.trackTime,
                    track.artworkUrl100,
                    track.collectionName,
                    track.releaseDate,
                    track.genre,
                    track.country
                )
            )
        }
        emit(context.getString(R.string.adding_to_playlist, playlist.playlistName))
    }


    private fun saveImageToPrivateStorage(uri: Uri): Uri {
        //создаём экземпляр класса File, который указывает на нужный каталог
        val filePath =
            File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "myalbum")
        //создаем каталог, если он не создан
        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        //создаём экземпляр класса File, который указывает на файл внутри каталога
        filename = "cover(%d).jpg".format((System.currentTimeMillis()) / 1000)
        val file = File(filePath, filename)
        // создаём входящий поток байтов из выбранной картинки
        val inputStream = context.contentResolver.openInputStream(uri)
        // создаём исходящий поток байтов в созданный выше файл
        val outputStream = FileOutputStream(file)
        // записываем картинку с помощью BitmapFactory
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
        return getImageFromPrivateStorage()
    }

    private fun getImageFromPrivateStorage(): Uri {
        val filePath = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "myalbum")
        val file = File(filePath, filename)
        return file.toUri()
    }

    private fun convertFromPlaylistsEntity(playlists: List<PlaylistEntity>): List<Playlist> {
        return playlists.map { playlist -> playlistDbConvertor.map(playlist) }
    }

    override fun getPlaylist(): Playlist {
        val playlistId = arguments.getString(CHOSEN_PLAYLIST)?.toInt()
        return playlistDbConvertor.map(appDatabase.playlistDao().getPlaylistById(playlistId!!))
    }


    override suspend fun getTracksByIds(trackIds: List<String>): Flow<List<Track>> = flow {

        withContext(Dispatchers.IO) {
            tracks.addAll(appDatabase.playlistTrackDao().getTracks().map {
                Track(
                    it.trackId,
                    it.trackName,
                    it.artistName,
                    it.url,
                    it.trackTime,
                    it.artworkUrl100,
                    it.collectionName,
                    it.releaseDate,
                    it.genre,
                    it.country
                )
            }
                .filter { it.trackId in trackIds })
        }
        emit(tracks)
    }


}