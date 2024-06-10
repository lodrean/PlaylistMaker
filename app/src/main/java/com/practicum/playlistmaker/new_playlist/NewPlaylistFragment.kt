package com.practicum.playlistmaker.new_playlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.BindingFragment
import com.practicum.playlistmaker.databinding.FragmentNewPlaylistBinding

class NewPlaylistFragment : BindingFragment<FragmentNewPlaylistBinding>() {

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


        // обработка кнопки сохранения ОК
        binding.backButton.setOnClickListener {
            confirmDialog.show()
        }

        // добавление слушателя для обработки нажатия на кнопку Back
        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                confirmDialog.show()
            }
        })


    }

    private fun save() {

    }
}