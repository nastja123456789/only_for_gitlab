package ru.ytken.a464_project_watermarks.main_feature.presentation.photo_crop
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_photo_crop.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import ru.ytken.a464_project_watermarks.R
import ru.ytken.a464_project_watermarks.main_feature.utils.BitmapExtensions.rotate
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.coroutines.coroutineContext


internal class PhotoCropFragment : Fragment(R.layout.fragment_photo_crop) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFragmentResultListener("fromButtonToCrop") {
                _ , bun ->
            val str = bun.getString("uri")
            val bitmap = assetToBitmap(str!!)
            document_scanner.setOnLoadListener { loading ->
                progressBar.isVisible = loading
            }
            document_scanner.post {
                document_scanner.setImage(bitmap)
            }

//            var angle1 = 0
            btnImageLeft.setOnClickListener {
                lifecycleScope.launch(Dispatchers.IO) {
                    angle1 -= 90
                    document_scanner.setImage(bitmap.rotate(angle1.toFloat()))
                    if (angle1 == -360) angle1 = 0
                }
            }

            btnImageRight.setOnClickListener {
                lifecycleScope.launch(Dispatchers.IO) {
                    angle1 += 90
                    document_scanner.setImage(bitmap.rotate(angle1.toFloat()))
                    if (angle1 == 360) angle1 = 0
                }
            }

            btnImageClarity.setOnClickListener {
                lifecycleScope.launch {
                    progressBar.isVisible = true
                    val image = document_scanner.getCroppedImage()
                    progressBar.isVisible = false
                    result_image.isVisible = false
                    result_image.setImageBitmap(image)
                    val bytes = ByteArrayOutputStream()
                    image.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
                    val path: String = MediaStore.Images.Media.insertImage(
                        requireActivity().contentResolver,
                        image,
                        "IMG_" + Calendar.getInstance().time,
                        null
                    )
                    val uri = Uri.parse(path)
                    setFragmentResult("fromCropToClarify", bundleOf("uri" to uri.toString()))
                    findNavController().navigate(R.id.action_photoCropFragment_to_improveClarifyFragment)
                }
            }
            btnImageLight.setOnClickListener {
                lifecycleScope.launch {
                    progressBar.isVisible = true
                    val image = document_scanner.getCroppedImage()
                    progressBar.isVisible = false
                    result_image.isVisible = false
                    result_image.setImageBitmap(image)
                    val bytes = ByteArrayOutputStream()
                    image.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
                    val path: String = MediaStore.Images.Media.insertImage(
                        requireActivity().contentResolver,
                        image,
                        "IMG_" + Calendar.getInstance().time,
                        null
                    )
                    val uri = Uri.parse(path)
                    setFragmentResult("fromCropToLight", bundleOf("uri" to uri.toString()))
                    findNavController().navigate(R.id.action_photoCropFragment_to_improveLightFragment)
                }
            }
            btnImageNewAlg.setOnClickListener {
                lifecycleScope.launch {
                    progressBar.isVisible = true
                    val image = document_scanner.getCroppedImage()
                    progressBar.isVisible = false
                    result_image.isVisible = true
                    result_image.setImageBitmap(image)
                    val bytes = ByteArrayOutputStream()
                    image.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
                    val path: String = MediaStore.Images.Media.insertImage(
                        requireActivity().contentResolver,
                        image,
                        "IMG_" + Calendar.getInstance().time,
                        null
                    )
                    val uri = Uri.parse(path)
                    setFragmentResult("fromCropToNewAlg", bundleOf("uri" to uri.toString()))
                    findNavController().navigate(R.id.action_photoCropFragment_to_newAlgFragment)
                }
            }
            btnImageCrop.setOnClickListener {
                lifecycleScope.launch {
                    progressBar.isVisible = true
                    val image = document_scanner.getCroppedImage()
                    progressBar.isVisible = false
                    result_image.isVisible = true
                    result_image.setImageBitmap(image)
                    val bytes = ByteArrayOutputStream()
                    image.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
                    val path: String = MediaStore.Images.Media.insertImage(
                        requireActivity().contentResolver,
                        image,
                    "IMG_" + Calendar.getInstance().time,
                    null
                    )
                    val uri = Uri.parse(path)
                    setFragmentResult("fromCropToImage", bundleOf("uri" to uri.toString()))
                    findNavController().navigate(R.id.action_photoCropFragment_to_imageResultFragment)
                }
            }
        }
    }

    private fun assetToBitmap(file: String): Bitmap =
        context!!.contentResolver.openInputStream(Uri.parse(file)).run {
            BitmapFactory.decodeStream(this)
        }

    companion object {
        private var angle1 = 0
    }

    override fun onDestroy() {
        angle1 = 0
        super.onDestroy()
    }
}