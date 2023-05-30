package ru.ytken.a464_project_watermarks.main_feature.presentation.improve_light

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_image_result.*
import kotlinx.android.synthetic.main.fragment_image_result.buttonSeeSkan
import kotlinx.android.synthetic.main.fragment_image_result.imageButtonClose
import kotlinx.android.synthetic.main.fragment_image_result.imageViewResultImage
import kotlinx.android.synthetic.main.fragment_improve_light.*
import kotlinx.android.synthetic.main.fragment_improve_light.view.*
import kotlinx.android.synthetic.main.fragment_photo_crop.*
import kotlinx.android.synthetic.main.fragment_photo_crop.view.*
import ru.ytken.a464_project_watermarks.R
import ru.ytken.a464_project_watermarks.main_feature.utils.BitmapExtensions.makeImageSharpGaussian
import ru.ytken.a464_project_watermarks.main_feature.utils.BitmapExtensions.setBrightnessContrast
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*

class ImproveLightFragment : Fragment(R.layout.fragment_improve_light) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFragmentResultListener("fromCropToLight") {
                _, bun ->
            val str = bun.getString("uri")
            val uri = Uri.parse(
                str
            )
            val bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, uri)
//            val bitmapp = bitmap.setBrightnessContrast()

//            val bitmapp =
                bitmap.apply {
                imageViewResultImage.setImageBitmap(this)
                seekBarBrightness.setOnSeekBarChangeListener(object:
                    SeekBar.OnSeekBarChangeListener{
                    override fun onProgressChanged(
                        seekBar: SeekBar?,
                        progress: Int,
                        fromUser: Boolean
                    ) {
                        val brightness = progress.toFloat()-200
                        val contrast = seekBarContrast.progress.toFloat()/10F
                        bit = bitmap.setBrightnessContrast(brightness, contrast)
                        imageViewResultImage.setImageBitmap(
                            setBrightnessContrast(
                                brightness=brightness,
                                contrast = contrast
                            )
                        )
                        tvBrightness.text = "Brightness $brightness"+"F"
                    }
                    override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    }
                })

                seekBarContrast.setOnSeekBarChangeListener(object:
                    SeekBar.OnSeekBarChangeListener{
                    override fun onProgressChanged(
                        seekBar: SeekBar?,
                        progress: Int,
                        fromUser: Boolean
                    ) {
                        val brightness = seekBarBrightness.progress.toFloat()-200
                        val contrast = progress.toFloat()/10F
                        bit = bitmap.setBrightnessContrast(brightness, contrast)
                        imageViewResultImage.setImageBitmap(
                            setBrightnessContrast(
                                brightness=brightness,
                                contrast = contrast
                            )
                        )
                        tvContrast.text = "Contrast $contrast"+"F"
                    }
                    override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    }
                })
            }
//                .setBrightnessContrast()
            val bitmapp = bitmap.setBrightnessContrast()
            imageViewResultImage.setImageBitmap(bitmapp)
            buttonSeeSkan.setOnClickListener {
                val bytes = ByteArrayOutputStream()
                if (bit != null) {
                    bit!!.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
                }
                val path: String = MediaStore.Images.Media.insertImage(
                    requireActivity().contentResolver,
                    bit,
                    "IMG_" + Calendar.getInstance().time,
                    null
                )
                val uri = Uri.parse(path)
                setFragmentResult(
                    "fromLightToImage",
                    bundleOf("uri" to uri.toString())
                )
                findNavController().navigate(R.id.action_improveLightFragment_to_imageResultFragment)
            }
        }
        imageButtonClose.setOnClickListener {
            findNavController().popBackStack()
        }
    }
    companion object{
        var bit:Bitmap ?= null
    }
}