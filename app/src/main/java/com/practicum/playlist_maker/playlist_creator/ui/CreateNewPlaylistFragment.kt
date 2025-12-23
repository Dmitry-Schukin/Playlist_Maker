package com.practicum.playlist_maker.playlist_creator.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlist_maker.databinding.FragmentCreateNewPlaylistBinding
import com.practicum.playlist_maker.utils.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.practicum.playlist_maker.R
import java.io.File
import java.io.FileOutputStream
import kotlin.getValue

class CreateNewPlaylistFragment: Fragment() {

    private var _binding: FragmentCreateNewPlaylistBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CreateNewPlaylistViewModel by viewModel()
    private var textWatcherForTitle: TextWatcher? = null
    private var textWatcherForDescription: TextWatcher? = null
    private lateinit var confirmDialog: MaterialAlertDialogBuilder


    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentCreateNewPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //region Observer
        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }
        //endregion

        disableCreateButton()

        //region Everything about TextWatcher
        textWatcherForTitle = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                /*typedTitleDebounce(
                    changedText = s.toString().trim()
                )*/
                viewModel.updateTitle(s.toString().trim())
            }
            override fun afterTextChanged(s: Editable?) {
            }
        }
        binding.playlistName.addTextChangedListener(textWatcherForTitle)

        textWatcherForDescription = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.updateDescription(s.toString().trim())
            }
            override fun afterTextChanged(s: Editable?) {}
        }
        binding.playlistDescription.addTextChangedListener(textWatcherForDescription)
        //endregion

        //region Pick and show media
        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                fun dpToPx (dp: Float, context: Context): Int{
                    return TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        dp,
                        context.resources.displayMetrics).toInt()
                }
                val cornerRadius = dpToPx(8f, requireContext())
                //обрабатываем событие выбора пользователем фотографии
                if (uri != null) {
                    Glide.with(binding.playlistImage)
                        .load(uri)
                        .override(312, 312)
                        .centerCrop()
                        .transform(RoundedCorners(cornerRadius))
                        .into(binding.playlistImage)
                    viewModel.updateImagePath(uri.toString())
                } else {
                    Log.d("PhotoPicker", "No media selected")
                }
            }
        //endregion

        //region Back click
            // Callback creating
        confirmDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(requireContext().getString(R.string.finish_creating))
            .setMessage(requireContext().getString(R.string.all_data_will_be_lost))
            .setNeutralButton(requireContext().getString(R.string.cansel)) { dialog, which ->
            // ничего не делаем
            }.setPositiveButton(requireContext().getString(R.string.finish)) { dialog, which ->
                // выходим
                findNavController().navigateUp()
            }
            // Callback registration in dispatcher
        requireActivity().onBackPressedDispatcher.addCallback(
            object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if(viewModel.showExitDialog()){
                    confirmDialog.show()
                }else{
                    findNavController().navigateUp()
                }
            }
        })
        //endregion

        //region Listeners
        binding.newPlaylistMaterialToolbar.setNavigationOnClickListener {
            if(viewModel.showExitDialog()){
                confirmDialog.show()
            }else{
                findNavController().navigateUp()
            }
        }
        binding.playlistImage.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        binding.createPlaylistButton.setOnClickListener {
            val uri = viewModel.getLatestImageUri()
            var newImageNameInDirectory = ""
            if(uri.isNotEmpty()){
                newImageNameInDirectory = saveImageToPrivateStorage(uri)
            }
            viewModel.updateImagePath(newImageNameInDirectory)
            viewModel.createNewPlaylist()
            showToast(R.string.playlist,R.string.created, viewModel.getLatestTitle())
        }
        //endregion
    }
    private fun saveImageToPrivateStorage(uri: String): String {
        val filePath = File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), requireActivity().getString(R.string.photo_storage))
        if (!filePath.exists()){
            filePath.mkdirs()
        }
        val imageName = System.currentTimeMillis().toString() + requireContext().getString(R.string.extension_jpg)
        val file = File(filePath, imageName)
        val inputStream = requireActivity().contentResolver.openInputStream(uri.toUri())
        val outputStream = FileOutputStream(file)
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
        val imageInPrivateDirectory = File(filePath, imageName).toUri().toString()
        return imageInPrivateDirectory
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun render(state: CreatorPlaylistState) {
        when (state) {
            is CreatorPlaylistState.PlaylistCreated-> finishActions()
            is CreatorPlaylistState.EmptyInputField -> disableCreateButton()
            is CreatorPlaylistState.ReadyToCreate -> readyToCreateNewPlaylist()
        }
    }
    fun finishActions(){
        findNavController().navigateUp()
    }
    fun readyToCreateNewPlaylist(){
        binding.createPlaylistButton.isEnabled = true
        val enableButtonColor = ContextCompat.getColor(requireActivity(), R.color.bright_blue)
        binding.createPlaylistButton.setBackgroundColor(enableButtonColor)
    }
    fun disableCreateButton(){
        binding.createPlaylistButton.isEnabled = false
        val disableButtonColor = ContextCompat.getColor(requireActivity(), R.color.gray_color)
        binding.createPlaylistButton.setBackgroundColor(disableButtonColor)
    }
    private fun showToast(textFromDirectory1:Int,textFromDirectory2:Int,playlistTitle: String){
        val message = requireContext()
            .getString(textFromDirectory1)+" "+playlistTitle+" "+requireContext().getString(textFromDirectory2)
        Toast.makeText(requireActivity(),message, Toast.LENGTH_SHORT).show()
    }
    companion object {
        private const val EDIT_TEXT_DEBOUNCE_DELAY = 1000L
    }
}