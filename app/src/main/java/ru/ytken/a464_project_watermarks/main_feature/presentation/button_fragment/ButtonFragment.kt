package ru.ytken.a464_project_watermarks.main_feature.presentation.button_fragment

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_button.*
import ru.ytken.a464_project_watermarks.R
import ru.ytken.a464_project_watermarks.main_feature.presentation.button_fragment.utils.PickPhoto
import java.io.File

class ButtonFragment : Fragment(
    R.layout.fragment_button,
) {
    private var photoPath: String?=null
    private var fileUri: Uri ?= null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttonChoosePhotoFromStorage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            galleryImageLauncher.launch(intent)
        }

        buttonTakePhoto.setOnClickListener {
            val fileName = System.currentTimeMillis().toString() + ".jpg"
            val values = ContentValues()
            values.put(MediaStore.Images.Media.TITLE, fileName)
            fileUri =
                requireActivity().contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri)
            pickPhotoLauncher.launch(intent)
        }
        galleryImageLauncher = registerForActivityResult(StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                setFragmentResult(
                    "fromButtonToCrop",
                    bundleOf("uri" to data!!.data.toString())
                )
                findNavController().navigate(R.id.action_buttonFragment_to_photoCropFragment)
            } else {
                reload()
            }
        }
        pickPhotoLauncher = registerForActivityResult(StartActivityForResult()) { result->
            Log.d("${result.resultCode}","resultCode")
            if (result.resultCode == Activity.RESULT_OK) {
                photoPath = fileUri?.let { PickPhoto.getPath(requireActivity(),it) }
                photoPath = Uri.fromFile(photoPath?.let { File(it) }).toString()
                setFragmentResult(
                    "fromButtonToCrop",
                    bundleOf("uri" to photoPath)
                )
                findNavController().navigate(R.id.action_buttonFragment_to_photoCropFragment)
            } else {
                reload()
            }
        }
    }

    private fun reload() {
        val id = findNavController().currentDestination?.id
        findNavController().popBackStack(id!!,true)
        findNavController().navigate(id)
    }

    companion object {
        lateinit var galleryImageLauncher: ActivityResultLauncher<Intent>
        lateinit var pickPhotoLauncher: ActivityResultLauncher<Intent>
    }
}