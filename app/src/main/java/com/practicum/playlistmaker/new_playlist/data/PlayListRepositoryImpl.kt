package com.practicum.playlistmaker.new_playlist.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.net.toUri
import com.practicum.playlistmaker.new_playlist.data.db.PlaylistDbConvertor
import com.practicum.playlistmaker.new_playlist.data.db.PlaylistEntity
import com.practicum.playlistmaker.new_playlist.data.db.PlaylistTrackEntity
import com.practicum.playlistmaker.new_playlist.domain.PlayListRepository
import com.practicum.playlistmaker.new_playlist.domain.Playlist
import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.util.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class PlayListRepositoryImpl(
    private val context: Context,
    private val appDatabase: AppDatabase,
    private val playlistDbConvertor: PlaylistDbConvertor
) : PlayListRepository {



    private var filename = ""

    override fun createPlaylist(playlistName: String, description: String, imageUri: Uri) {
        if (imageUri.toString() != "") {
            saveImage(imageUri)
            appDatabase.playlistDao()
                .insert(PlaylistEntity(0, playlistName, description, getImage().toString(), ""))
        } else {
            appDatabase.playlistDao().insert(PlaylistEntity(0, playlistName, description, "", ""))
        }
    }

    override fun getImage(): Uri {
        return getImageFromPrivateStorage()
    }

    override fun saveImage(imageUri: Uri) {
        saveImageToPrivateStorage(imageUri)
    }

    override fun getPlaylists(): Flow<List<Playlist>> = flow {

        val playlists = withContext(Dispatchers.IO) {
            appDatabase.playlistDao().getPlaylists()
        }
        emit(convertFromPlaylistsEntity(playlists))

    }

    override fun addTrackToPlaylist(track: Track, playlist: Playlist): Flow<String> = flow {
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
        emit("Добавлено в плейлист ${playlist.playlistName}")
    }

private fun getDbId(): Int{
   val size = appDatabase.playlistDao().getPlaylists().lastIndex
    Log.d("size", "$size")
    return size

}
    private fun saveImageToPrivateStorage(uri: Uri) {
        //создаём экземпляр класса File, который указывает на нужный каталог
        val filePath =
            File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "myalbum")
        //создаем каталог, если он не создан
        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        //создаём экземпляр класса File, который указывает на файл внутри каталога
        filename = "cover(%d).jpg".format(getDbId()+1)
        val file = File(filePath, filename)
        // создаём входящий поток байтов из выбранной картинки
        val inputStream = context.contentResolver.openInputStream(uri)
        // создаём исходящий поток байтов в созданный выше файл
        val outputStream = FileOutputStream(file)
        // записываем картинку с помощью BitmapFactory
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
    }

    private fun getImageFromPrivateStorage(): Uri {
        val filePath = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "myalbum")
        val file = File(filePath, filename)
        return file.toUri()
    }

    private fun convertFromPlaylistsEntity(playlists: List<PlaylistEntity>): List<Playlist> {
        return playlists.map { playlist -> playlistDbConvertor.map(playlist) }
    }

}