package ru.ytken.a464_project_watermarks.main_feature.presentation.new_alg

import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.net.Uri
import android.provider.MediaStore
import android.util.ArrayMap
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
import ru.ytken.a464_project_watermarks.main_feature.presentation.see_scan.SeeScanFragmentViewModel
import ru.ytken.a464_project_watermarks.main_feature.utils.BitmapExtensions.rotateBitmap

class NewAlgFragmentViewModel: ViewModel() {
    private val liveScanLettersText = MutableLiveData<String>()
    private val liveInitImage = MutableLiveData<Bitmap>()

    private val liveHighlightedImage = MutableLiveData<Bitmap>()
    val highlightedImage: LiveData<Bitmap> = liveHighlightedImage

    private val liveScanImage = MutableLiveData<Bitmap>()
    val scanImage: LiveData<Bitmap> = liveScanImage

    private val liveHasText = MutableLiveData<Boolean>()
    val hasText: LiveData<Boolean> = liveHasText
    val l: ArrayList<ArrayList<ArrayList<Int>>> = ArrayList()
    var lineBounds: ArrayList<ArrayList<Int>> = ArrayList()
    var resting: ArrayList<ArrayList<ArrayList<Int>>> = ArrayList()
    var remov: ArrayList<ArrayList<Int>> = ArrayList()
    var lost: ArrayList<ArrayList<Int>> = ArrayList()
    var removing: ArrayList<Int> = ArrayList()
//    private var symBounds: MutableList<Int> = ArrayList()
//    var symBounds: ArrayList<Int> = ArrayList()

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

    fun setLetterText(text: String) {
        liveScanLettersText.value = text
        Log.d("100000000000", text)
    }

    fun findTextInBitmap(imageBitmap: Bitmap?) {
        liveInitImage.value = imageBitmap
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        var maxBitmap: Bitmap? = imageBitmap
        var maxText = 0
        var maxBlocks: List<Text.TextBlock>? = null
        var lensym: Int = 0
//        var maxTexting: List<String>
        lineBounds.clear()
//        symBounds.clear()
        val copyBitmap = imageBitmap?.rotateBitmap(0)?.copy(Bitmap.Config.ARGB_8888,false)
        val image = copyBitmap?.let { InputImage.fromBitmap(it, 0) }
        viewModelScope.async(Dispatchers.Default) {
            if (image != null) {
                recognizer.process(image)
                    .addOnSuccessListener { visionText ->
                        if (visionText.text.length > maxText) {
                            maxText = visionText.text.length
                            maxBitmap = copyBitmap
                            maxBlocks = visionText.textBlocks
                        }
                    }
                    .addOnCompleteListener {
                        val mutableImageBitmap = maxBitmap?.copy(Bitmap.Config.ARGB_8888,true)

                        val canvas = mutableImageBitmap?.let { it1 -> Canvas(it1) }
                        val shapeDrawable = ShapeDrawable(RectShape())
                        shapeDrawable.paint.style = Paint.Style.STROKE
                        shapeDrawable.paint.strokeWidth = 10F

                        if (maxBlocks != null) {
                            for (block in maxBlocks!!) {
                                for (line in block.lines) {
                                    val symBounds: ArrayList<Int> = ArrayList()
                                    val map: ArrayList<ArrayList<Int>> = ArrayList()
                                    for (element in line.elements) {
//                                        symBounds.clear()
                                        for (char in element.symbols) {
//                                            Log.d(char.text,"yyyyyyy")
                                            char.boundingBox?.let {
                                                shapeDrawable.bounds = it
                                                symBounds.add(it.width())
                                                val ar: ArrayList<Int> = ArrayList()
                                                ar.add(it.left)
                                                ar.add(it.right)
                                                map.add(ar)
                                            }
                                            if (canvas != null) {
                                                shapeDrawable.draw(canvas)
                                            }
                                        }
                                    }
                                    l.add(map)
                                    lineBounds.add(symBounds)
                                }
                            }
                            for (line in l) {
                                        if (line.size%8!=0) {
                                            val length = line.size-line.size%8
                                            val mut: java.util.ArrayList<ArrayList<Int>> = line
                                            var mutty: MutableList<ArrayList<Int>> = mut.toMutableList()
                                            mutty = mut.subList(0, length)
                                            val arr: ArrayList<ArrayList<Int>> = ArrayList(mutty)
                                            val num = arr.chunked(8)
                                            val siz = num.size
                                            val res: ArrayList<ArrayList<Int>> = ArrayList()
                                            for (i in num) {
                                                val a:ArrayList<Int> = ArrayList()
                                                for (j in 1..i.size-1) {
                                                    if (j!=i.size-1) {
                                                        val adding = i[j][0]-i[j-1][0]
                                                        a.add(adding)
                                                    } else {
                                                        a.add(i[j][0]-i[j-1][0])
                                                        a.add(i[j][1]-i[j][0])
                                                    }
                                                }
                                                res.add(a)
                                            }
                                            resting.add(res)
                                        }
                                        else {
                                            val num = line.chunked(8)
                                            var res: ArrayList<ArrayList<Int>> = ArrayList()
                                            for (i in num) {
//                                                Log.d("$i","numnumnum")
                                                var a:ArrayList<Int> = ArrayList()
                                                for (j in 1..i.size-1) {
                                                    if (j!=i.size-1) {
                                                        val adding = i[j][0]-i[j-1][0]
                                                        a.add(adding)
                                                    } else {
                                                        a.add(i[j][0]-i[j-1][0])
                                                        a.add(i[j][1]-i[j][0])
                                                    }
                                                }
                                                res.add(a)
                                            }
                                            resting.add(res)
                                        }
                                    }
                            for (i in resting){
                                val r: ArrayList<Int> = ArrayList()
                                for (k in i) {
                                    for (m in k) {
                                        r.add(m)
                                    }
                                }
                                lost.add(r)
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
    }
    fun compareStrings(str1: String): Double {
        var count = 0.0
        for (i in str1.indices) {
            if (str1[i] == SeeScanFragmentViewModel.str2[i]) {
                count++
            }
        }
        return (count / str1.length) * 100
    }
}