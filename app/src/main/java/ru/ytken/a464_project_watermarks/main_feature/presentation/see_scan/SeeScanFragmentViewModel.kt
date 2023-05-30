package ru.ytken.a464_project_watermarks.main_feature.presentation.see_scan

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class SeeScanFragmentViewModel: ViewModel() {
    private val liveScanLettersText = MutableLiveData<String>()

    var lineBounds: ArrayList<Int> = ArrayList()

    fun setLetterText(text: String) {
        liveScanLettersText.value = text
        Log.d("100000000000", text)
    }

    fun compareStrings(str1: String): Double {
        var count = 0.0
        for (i in str1.indices) {
            if (str1[i] == str2[i]) {
                count++
            }
        }
        return (count / str1.length) * 100
    }

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
    companion object{
        val str2: String = "010010000101001101000101"
    }
}
