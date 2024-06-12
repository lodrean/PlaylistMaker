package com.practicum.playlistmaker.new_playlist.ui

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.BindingFragment
import com.practicum.playlistmaker.databinding.FragmentNewPlaylistBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream

class NewPlaylistFragment : BindingFragment<FragmentNewPlaylistBinding>() {
    private val viewModel by viewModel<NewPlayLIstViewModel>()

    lateinit var confirmDialog: MaterialAlertDialogBuilder
    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentNewPlaylistBinding {
        return FragmentNewPlaylistBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        confirmDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Завершить создание плейлиста?")
            .setNeutralButton("Отмена") { dialog, which ->
                // ничего не делаем
            }.setPositiveButton("Завершить") { dialog, which ->
                // сохраняем изменения и выходим
                save()
                parentFragmentManager.popBackStack()
            }
        binding.createButton.isEnabled = false


        binding.backButton.setOnClickListener {
            if ((binding.textInputName.text?.isNotEmpty() == true) or (binding.textInputDescription.text?.isNotEmpty() == true) or binding.imageView.isVisible) {
                confirmDialog.show()
            } else {
                parentFragmentManager.popBackStack()
            }
        }


        // добавление слушателя для обработки нажатия на кнопку Back
        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Log.d("PhotoPicker", "${binding.textInputName.text?.isNotEmpty()}")
                if ((binding.textInputName.text?.isNotEmpty() == true) or (binding.textInputDescription.text?.isNotEmpty() == true) or binding.imageView.isVisible) {

                    confirmDialog.show()
                } else {
                    parentFragmentManager.popBackStack()
                }
            }
        })

        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->

                //обрабатываем событие выбора пользователем фотографии
                if (uri != null) {


                    val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    context?.contentResolver?.takePersistableUriPermission(uri, flag)

                    binding.imageView.setImageURI(uri)
                    binding.imageView.isVisible = true
                    binding.placeHolder.isVisible = false
                    saveImageToPrivateStorage(uri)
                } else {
                    Log.d("PhotoPicker", "No media selected")
                }
            }
        //по нажатию на кнопку pickImage запускаем photo picker
        binding.imageView.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        //по нажатию на кнопку loadImageFromStorage пытаемся загрузить фотографию из нашего хранилища
        /*binding.loadImageFromStorage.setOnClickListener {
            val filePath = File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "myalbum")
            val file = File(filePath, "first_cover.jpg")
            binding.storageImage.setImageURI(file.toUri())
        }*/
        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                binding.textInputName.requestFocus()
                if (s?.isEmpty() == true) viewModel.banCreation()
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                binding.textInputName.requestFocus()
                /*viewModel.searchDebounce(changedText = s.toString())
                searchView.isVisible = s?.isEmpty() != true
                recyclerView.isVisible = s?.isNotEmpty() == true
                if (inputEditText.hasFocus() && s?.isEmpty() == true && (trackHistoryAdapter.itemCount > 0)) {
                    viewModel.showHistoryTrackList()
                }*/
                if (s?.isEmpty() == true) viewModel.banCreation() else viewModel.allowCreation()
            }

            override fun afterTextChanged(s: Editable?) {
                /*  inputText.plus(s)*/

            }
        }

        binding.textInputName.addTextChangedListener(simpleTextWatcher)

        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }
    }

    private fun save() {

    }

    private fun saveImageToPrivateStorage(uri: Uri) {
        //создаём экземпляр класса File, который указывает на нужный каталог
        val filePath =
            File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "myalbum")
        //создаем каталог, если он не создан
        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        //создаём экземпляр класса File, который указывает на файл внутри каталога
        val file = File(filePath, "first_cover.jpg")
        // создаём входящий поток байтов из выбранной картинки
        val inputStream = requireActivity().contentResolver.openInputStream(uri)
        // создаём исходящий поток байтов в созданный выше файл
        val outputStream = FileOutputStream(file)
        // записываем картинку с помощью BitmapFactory
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
    }

    private fun render(state: NewPlaylistState) {
        when (state) {
            is NewPlaylistState.Creation -> binding.createButton.isEnabled = state.creation
            is NewPlaylistState.IsCreate -> showIsCreateMessage(state.message)
        }
    }

    private fun showIsCreateMessage(message: String) {
        Toast.makeText(
            requireContext().applicationContext,
            message,
            Toast.LENGTH_LONG
        ).show()
    }

    override fun onPause() {

        super.onPause()

    }
}


