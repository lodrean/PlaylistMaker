package com.practicum.playlistmaker.new_playlist.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import androidx.core.net.toUri
import com.practicum.playlistmaker.mediateka.data.db.TrackEntity
import com.practicum.playlistmaker.new_playlist.data.db.PlaylistEntity
import com.practicum.playlistmaker.new_playlist.domain.NewPlayListRepository
import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.util.AppDatabase
import java.io.File
import java.io.FileOutputStream

class NewPlayListRepositoryImpl(private val context: Context,
   private val appDatabase: AppDatabase) : NewPlayListRepository {
    override fun createPlaylist(imageUri: String, playlistName: String, description: String) {
        appDatabase.playlistDao().insert(PlaylistEntity( 0, playlistName, description, imageUri, ""))
    }

    override fun getImage(): Uri {
        return getImageFromPrivateStorage()
    }

    override fun saveImage(imageUri: String) {
        saveImageToPrivateStorage(imageUri.toUri())
    }

    fun saveImageToPrivateStorage(uri: Uri) {
        //создаём экземпляр класса File, который указывает на нужный каталог
        val filePath =
            File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "myalbum")
        //создаем каталог, если он не создан
        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        //создаём экземпляр класса File, который указывает на файл внутри каталога
        val file = File(filePath, "first_cover.jpg")
        // создаём входящий поток байтов из выбранной картинки
        val inputStream = context.contentResolver.openInputStream(uri)
        // создаём исходящий поток байтов в созданный выше файл
        val outputStream = FileOutputStream(file)
        // записываем картинку с помощью BitmapFactory
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
    }

    fun getImageFromPrivateStorage(): Uri{
        val filePath = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "myalbum")
        val file = File(filePath, "first_cover.jpg")
        return file.toUri()
    }


}