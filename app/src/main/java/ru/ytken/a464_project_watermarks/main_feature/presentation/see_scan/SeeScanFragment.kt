package ru.ytken.a464_project_watermarks.main_feature.presentation.see_scan

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import kotlinx.android.synthetic.main.fragment_scan_result.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.ytken.a464_project_watermarks.R
import ru.ytken.a464_project_watermarks.main_feature.domain.use_cases.SavedImageFactoryUseCase
import ru.ytken.a464_project_watermarks.main_feature.presentation.see_scan.utils.Watermarks
import ru.ytken.a464_project_watermarks.main_feature.utils.BitmapExtensions.toGrayscale
import java.io.File

class SeeScanFragment: Fragment(R.layout.fragment_scan_result) {
    private val vm: SeeScanFragmentViewModel by viewModels {
        SavedImageFactoryUseCase()
    }
    private var fileWithImage: Bitmap? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFragmentResultListener("fromImageToSeeScan") {
                _, bun ->
            val str = bun.getString("uri")
            val uri = Uri.parse(
                str
            )
            fileWithImage = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, uri)
            imageViewSkanned.setImageBitmap(fileWithImage)
            if (fileWithImage != null) {
                processImage(fileWithImage!!.toGrayscale()!!)
            }
            val fdelete = vm.getFilePath(uri, requireContext())?.let { File(it) }
            if (fdelete!!.exists()) {
                if (fdelete.delete()) {
                } else {
                }
            }
        }
        setFragmentResultListener("fromImageToSeeScan2") {
                _, bun ->
            val str = bun.getString("uri")
            val uri = Uri.parse(
                str
            )
            fileWithImage = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, uri)
            imageViewSkanned.setImageBitmap(fileWithImage)
            if (fileWithImage != null) {
                processImage2(fileWithImage!!.toGrayscale()!!)
            }
            val fdelete = vm.getFilePath(uri, requireContext())?.let { File(it) }
            if (fdelete!!.exists()) {
                if (fdelete.delete()) {
                } else {
                }
            }
        }

        setFragmentResultListener("arrayList") {
                _, bun ->
                vm.lineBounds = bun.getSerializable("array") as ArrayList<Int>
        }

        for (i in vm.lineBounds) {
            Log.d("lineline","{$i}")
        }

        imageButtonNoSkan.setOnClickListener {
            findNavController().popBackStack(R.id.buttonFragment, true)
            findNavController().navigate(R.id.buttonFragment)
        }

        imageViewCopyToBuffer.setOnClickListener {
            val clipboard: ClipboardManager =
                context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("", textViewRecognizedText.text)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(activity, getString(R.string.copyToBuffer), Toast.LENGTH_SHORT).show()
        }
    }

    private fun processImage(fileWithImage: Bitmap) = lifecycleScope.launch(Dispatchers.Main) {
        progressBarWaitForScan.visibility = View.VISIBLE
        textViewProgress.visibility = View.VISIBLE
        textViewProgress.text = getString(R.string.ScanningImage)

        fileWithImage.let {
            imageButtonNoSkan.visibility = View.VISIBLE
            val watermarkSize = 24
            val resMatrix = ""
            val lineBounds = vm.lineBounds
            for (i in lineBounds) {
                Log.d("$i","okioki")
            }
            try{
                val lineIntervals = ArrayList<Int>()
                for (i in 1 until lineBounds.size)
                    lineIntervals.add(lineBounds[i]-lineBounds[i-1])
                for (i in lineIntervals) {
                    Log.d("$i","iiiii")
                }
//                launch(Dispatchers.IO) {
                    val intArr = lineIntervals.toIntArray()
                    if (!Python.isStarted()) {
                        Python.start(AndroidPlatform(context!!.applicationContext));
                    }
                    val pyObject = Python.getInstance().getModule("script")
//                val watermark = pyObject.callAttr("main").toString()
                    val watermark = pyObject.callAttr("gmm_alg", intArr).toString()
//                val watermark = Watermarks.getWatermark(lineIntervals)
                    setTextButton(watermark?.subSequence(0,watermarkSize).toString())
                    vm.setLetterText(resMatrix)
                    val res = vm.compareStrings(watermark?.subSequence(0,watermarkSize).toString())
                    Toast.makeText(context, "$res %", Toast.LENGTH_SHORT).show()
//                }

            } catch (e: java.lang.IndexOutOfBoundsException) {
                Toast.makeText(context, "К сожалению изображение не содержит водяной знак!", Toast.LENGTH_SHORT).show()
            }
            progressBarWaitForScan.visibility = View.INVISIBLE
            textViewProgress.visibility = View.INVISIBLE
        }
    }

    private fun processImage2(fileWithImage: Bitmap) = lifecycleScope.launch(Dispatchers.Main) {
        progressBarWaitForScan.visibility = View.VISIBLE
        textViewProgress.visibility = View.VISIBLE
        textViewProgress.text = getString(R.string.ScanningImage)

        fileWithImage.let {
            imageButtonNoSkan.visibility = View.VISIBLE
            val watermarkSize = 24
            val resMatrix = ""
            val lineBounds = vm.lineBounds
            for (i in lineBounds) {
                Log.d("$i","okioki")
            }
            try{
                val lineIntervals = ArrayList<Int>()
                for (i in 1 until lineBounds.size)
                    lineIntervals.add(lineBounds[i]-lineBounds[i-1])
                for (i in lineIntervals) {
                    Log.d("$i","iiiii")
                }
                val watermark = Watermarks.getWatermark(lineIntervals)
                setTextButton(watermark?.subSequence(0,watermarkSize).toString())
                vm.setLetterText(resMatrix)
                val res = vm.compareStrings(watermark?.subSequence(0,watermarkSize).toString())
                Toast.makeText(context, "$res %", Toast.LENGTH_SHORT).show()
//                Toast.makeText(context, "${resMatrix}", Toast.LENGTH_SHORT).show()
//                var res : Double?=0.0
            } catch (e: java.lang.IndexOutOfBoundsException) {
                Toast.makeText(context, "К сожалению изображение не содержит водяной знак!", Toast.LENGTH_SHORT).show()
            }
            progressBarWaitForScan.visibility = View.INVISIBLE
            textViewProgress.visibility = View.INVISIBLE

        }
    }

    private fun setTextButton(text: String) {
        textViewRecognizedText.visibility = View.VISIBLE
        imageViewCopyToBuffer.visibility = View.VISIBLE
        textViewRecognizedText.text = text
    }
    override fun onDestroyView() {
        super.onDestroyView()
        imageViewSkanned.setImageBitmap(null)
    }
}