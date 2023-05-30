package ru.ytken.a464_project_watermarks.main_feature.presentation.button_fragment.utils

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore

object PickPhoto {
    fun getPath(context: Context,selectedImaeUri: Uri): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor? = context.contentResolver.query(selectedImaeUri, projection, null, null,
            null)
        if (cursor != null) {
            cursor.moveToFirst()
            val columnIndex: Int = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            return cursor.getString(columnIndex)
        }
        return selectedImaeUri.path
    }
}