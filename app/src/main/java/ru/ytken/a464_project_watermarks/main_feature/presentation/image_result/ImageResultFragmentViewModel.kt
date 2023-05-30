package ru.ytken.a464_project_watermarks.main_feature.presentation.image_result

import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import ru.ytken.a464_project_watermarks.main_feature.utils.BitmapExtensions.rotateBitmap

class ImageResultFragmentViewModel: ViewModel() {

    private val liveInitImage = MutableLiveData<Bitmap>()

    private val liveHighlightedImage = MutableLiveData<Bitmap>()
    val highlightedImage: LiveData<Bitmap> = liveHighlightedImage

    private val liveScanImage = MutableLiveData<Bitmap>()
    val scanImage: LiveData<Bitmap> = liveScanImage

    private val liveHasText = MutableLiveData<Boolean>()
    val hasText: LiveData<Boolean> = liveHasText

    var lineBounds: ArrayList<Int> = ArrayList()

    fun getFilePath(uri: Uri, context: Context): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor? = context.contentResolver.query(uri, projection, null, null, null)
        if (cursor != null) {
            cursor.moveToFirst()
            val columnIndex: Int = cursor.getColumnIndex(projection[0])
            val picturePath: String = cursor.getString(columnIndex) // returns null
            cursor.close()
            return picturePath
        }
        return null
    }

    fun findTextInBitmap(imageBitmap: Bitmap) {
        liveInitImage.value = imageBitmap
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        var maxBitmap: Bitmap = imageBitmap
        var maxText = 0
        var maxBlocks: List<Text.TextBlock>? = null
        lineBounds.clear()
        val copyBitmap = imageBitmap.rotateBitmap(0).copy(Bitmap.Config.ARGB_8888,false)
        val image = InputImage.fromBitmap(copyBitmap, 0)
        viewModelScope.async(Dispatchers.Default) {
            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    if (visionText.text.length > maxText) {
                        maxText = visionText.text.length
                        maxBitmap = copyBitmap
                        maxBlocks = visionText.textBlocks
                    }
                }
                .addOnCompleteListener {
                    val mutableImageBitmap = maxBitmap.copy(Bitmap.Config.ARGB_8888,true)
                    val canvas = Canvas(mutableImageBitmap)
                    val shapeDrawable = ShapeDrawable(RectShape())
                    shapeDrawable.paint.style = Paint.Style.STROKE
                    shapeDrawable.paint.strokeWidth = 10F

                    if (maxBlocks != null) {
                        for (block in maxBlocks!!) {
                            for (line in block.lines) {
                                line.boundingBox?.let {
                                    shapeDrawable.bounds = it
                                    lineBounds.add(it.centerY())
                                }
                                shapeDrawable.draw(canvas)
                            }
                        }
                        liveHasText.value = true
                    } else {
                        liveHasText.value = false
                    }

                    liveInitImage.value = maxBitmap
                    liveHighlightedImage.value = mutableImageBitmap
                }
        }
    }

    fun setScanImageToInit() {
        liveScanImage.value = highlightedImage.value
    }
}