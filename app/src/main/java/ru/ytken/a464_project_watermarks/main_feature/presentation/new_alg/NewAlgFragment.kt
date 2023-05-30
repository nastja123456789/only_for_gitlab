package ru.ytken.a464_project_watermarks.main_feature.presentation.new_alg
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_image_result.*
import kotlinx.android.synthetic.main.fragment_image_result.imageButtonClose
import kotlinx.android.synthetic.main.fragment_image_result.progressBarWaitForImage
import kotlinx.android.synthetic.main.fragment_new_alg.*
import ru.ytken.a464_project_watermarks.R
import ru.ytken.a464_project_watermarks.main_feature.domain.use_cases.SavedImageFactoryUseCase
import ru.ytken.a464_project_watermarks.main_feature.presentation.image_result.ImageResultFragmentViewModel
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*

class NewAlgFragment : Fragment(R.layout.fragment_new_alg) {
    private val vm: NewAlgFragmentViewModel by viewModels {
        SavedImageFactoryUseCase()
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFragmentResultListener("fromCropToNewAlg") {
                _, bun ->
            val str = bun.getString("uri")
            val uri = Uri.parse(
                str
            )
            bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, uri)
            vm.findTextInBitmap(bitmap)
            val fdelete = vm.getFilePath(uri, requireContext())?.let { File(it) }
            if (fdelete!!.exists()) {
                if (fdelete.delete()) {
                } else {
                }
            }
        }
        buttonRes.setOnClickListener {
            val bytes = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
            val path: String = MediaStore.Images.Media.insertImage(
                requireActivity().contentResolver,
                bitmap,
                "IMG_" + Calendar.getInstance().time,
                null
            )
            val uri = Uri.parse(path)
            setFragmentResult("fromNewAlgToResultNewAlg", bundleOf("uri" to uri.toString()))
//            val arr = vm.lineBounds
//            for (i in vm.lineBounds) {
//                Log.d("$i","lollol")
//            }
            val arr = vm.lost
            for (i in vm.lost) {
                Log.d("$i","lollol")
            }

            setFragmentResult(
                "arrayListArrayList",
                bundleOf( "arrayList" to arr)
            )
            findNavController().navigate(R.id.action_newAlgFragment_to_resultNewAlgFragment)
        }
        progressBarWaitForImage.visibility = View.VISIBLE
        imageViewResultAlg.visibility = View.INVISIBLE
        vm.highlightedImage.observe(viewLifecycleOwner) {
            imageViewResultAlg.setImageBitmap(it)
            imageViewResultAlg.visibility = View.VISIBLE
            progressBarWaitForImage.visibility = View.INVISIBLE
            if (vm.hasText.value == false) {
                imageButtonClose.visibility = View.VISIBLE
                Toast.makeText(activity, getString(R.string.text_not_found), Toast.LENGTH_SHORT).show()
            } else {
//                buttonSeeSkan.visibility = View.VISIBLE
            }
        }
        imageButtonClose.setOnClickListener {
            findNavController().popBackStack(R.id.buttonFragment, true)
            findNavController().navigate(R.id.buttonFragment)
        }
        for (e in vm.lineBounds) {
            for (l in e) {
                Log.d("eeeeeeeeeee","$l")
            }

        }
    }
    companion object {
        var bitmap: Bitmap?=null
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}