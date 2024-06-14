package com.practicum.playlistmaker.new_playlist.ui

import android.content.Intent
import android.os.Bundle
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
        val creationFlag =
            (binding.textInputName.text?.isNotEmpty() == true) or (binding.textInputDescription.text?.isNotEmpty() == true) or binding.imageView.isVisible

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
                if (creationFlag) {
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
                    viewModel.setImage(uri)
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
                if (!creationFlag) viewModel.banCreation()
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                binding.textInputName.requestFocus()
                /*viewModel.searchDebounce(changedText = s.toString())
                searchView.isVisible = s?.isEmpty() != true
                recyclerView.isVisible = s?.isNotEmpty() == true
                if (inputEditText.hasFocus() && s?.isEmpty() == true && (trackHistoryAdapter.itemCount > 0)) {
                    viewModel.showHistoryTrackList()
                }*/
                if (!creationFlag) viewModel.banCreation()
                if (s?.isNotEmpty() == true) {
                    viewModel.allowCreation()
                    binding.createButton.isEnabled = true
                } else binding.createButton.isEnabled = false
            }

            override fun afterTextChanged(s: Editable?) {
                /*  inputText.plus(s)*/

            }
        }

        binding.textInputName.addTextChangedListener(simpleTextWatcher)
        binding.createButton.setOnClickListener {
            viewModel.createPlaylist(
                binding.textInputName.text.toString(),
                binding.textInputDescription.text.toString()
            )
            viewModel.deleteImage()
            parentFragmentManager.popBackStack()
        }

        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }
        viewModel.observeShowToast().observe(viewLifecycleOwner) { toast ->
            showToast(toast)
        }
    }

    private fun save() {

    }


    private fun render(state: NewPlaylistState) {
        when (state) {
            is NewPlaylistState.Creation -> {
                if (binding.textInputName.text?.isNotEmpty() == true) {
                    binding.createButton.isEnabled
                }
                binding.imageView.setImageURI(state.uri)
                if (state.uri.toString() != "") {
                    binding.imageView.isVisible = state.creation
                    binding.placeHolder.isVisible = !state.creation
                }
            }

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
    private fun showToast(additionalMessage: String) {
        Toast.makeText(requireContext(), additionalMessage, Toast.LENGTH_LONG).show()
    }
    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}


