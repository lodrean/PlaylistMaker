package com.practicum.playlistmaker.new_playlist.ui

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
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.BindingFragment
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.practicum.playlistmaker.player.ui.AudioPlayerActivity
import com.practicum.playlistmaker.player.ui.AudioPlayerViewModel
import com.practicum.playlistmaker.search.ui.dpToPx
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

        confirmDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.complete_playlist_creation))
            .setNeutralButton(getString(R.string.cancel)) { dialog, which ->
                // ничего не делаем
            }.setPositiveButton(getString(R.string.complete)) { dialog, which ->
                if (requireActivity() is AudioPlayerActivity) {
                    requireActivity().supportFragmentManager.beginTransaction().remove(this)
                        .commit()
                } else {
                    parentFragmentManager.popBackStack()
                }
            }
        binding.createButton.isEnabled = false
        binding.imageView.isVisible = false

        binding.backButton.setOnClickListener {
            if (checkCreationFlag()) {
                confirmDialog.show()
            } else {
                if (requireActivity() is AudioPlayerActivity) {
                    requireActivity().supportFragmentManager.beginTransaction().remove(this)
                        .commit()
                } else {
                    parentFragmentManager.popBackStack()
                }
            }
        }


        // добавление слушателя для обработки нажатия на кнопку Back
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (checkCreationFlag()) {
                        confirmDialog.show()
                    } else {
                        if (requireActivity() is AudioPlayerActivity) {
                            requireActivity().supportFragmentManager.beginTransaction()
                                .remove(this@NewPlaylistFragment)
                                .commit()
                        }
                        parentFragmentManager.popBackStack()
                    }
                }
            }
        )


        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                //обрабатываем событие выбора пользователем фотографии
                if (uri != null) {
                    viewModel.setImage(uri)
                } else {
                    Log.d("PhotoPicker", "No media selected")
                }
            }
        //по нажатию на кнопку pickImage запускаем photo picker
        binding.buttonView.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                binding.textInputName.requestFocus()
                if (!checkCreationFlag()) viewModel.disableCreation()
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                binding.textInputName.requestFocus()

                if (!checkCreationFlag()) viewModel.disableCreation()
                if (s?.isNotEmpty() == true) {
                    viewModel.enableCreation()
                    binding.createButton.isEnabled = true
                } else binding.createButton.isEnabled = false
            }

            override fun afterTextChanged(s: Editable?) {

            }
        }

        binding.textInputName.addTextChangedListener(simpleTextWatcher)
        binding.createButton.setOnClickListener {
            viewModel.createPlaylist(
                binding.textInputName.text.toString(),
                binding.textInputDescription.text.toString()
            )
            viewModel.deleteImage()
            if (requireActivity() is AudioPlayerActivity) {
                requireActivity().supportFragmentManager.beginTransaction().remove(this)
                    .commit()

            } else {
                parentFragmentManager.popBackStack()
            }

        }

        viewModel.observeState().observe(viewLifecycleOwner)
        {
            render(it)
        }
        viewModel.observeShowToast().observe(viewLifecycleOwner)
        { toast ->
            showToast(toast)
        }
    }

    private fun render(state: NewPlaylistState) {
        when (state) {
            is NewPlaylistState.Creation -> {
                if (binding.textInputName.text?.isNotEmpty() == true) {
                    binding.createButton.isEnabled
                }

                val cornerRadius = 8F
                binding.imageView.let {
                    Glide.with(this).load(state.uri)
                        .fitCenter()
                        .dontAnimate()
                        .placeholder(R.drawable.placeholder)
                        .transform(
                            CenterCrop(),
                            RoundedCorners(dpToPx(cornerRadius, requireContext()))
                        ).into(it)
                }
                if (state.uri.toString() != "") {
                    binding.imageView.isVisible = true
                    binding.placeHolder.isVisible = false
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

    private fun showToast(additionalMessage: String) {
        Toast.makeText(requireContext(), additionalMessage, Toast.LENGTH_LONG).show()
    }


    companion object {
        @JvmStatic
        fun newInstance() =
            NewPlaylistFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }


    fun checkCreationFlag(): Boolean {
        return (binding.textInputName.text?.isNotEmpty() == true) or (binding.textInputDescription.text?.isNotEmpty() == true) or binding.imageView.isVisible
    }
}



