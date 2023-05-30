package ru.ytken.a464_project_watermarks.main_feature.utils

import android.content.Context
import android.graphics.*
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicConvolve3x3
import org.opencv.android.Utils
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc.filter2D


object BitmapExtensions {
    internal fun Bitmap.rotateBitmap(angle: Int): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle.toFloat())
        return Bitmap.createBitmap(this, 0, 0, this.width, this.height, matrix, true)
    }

    fun Bitmap.rotate(degrees: Float): Bitmap {
        val matrix = Matrix().apply { postRotate(degrees) }
        return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
    }

    fun Bitmap.toGrayscale(): Bitmap? {
        val height: Int = this.height
        val width: Int = this.width
        val bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val c = Canvas(bmpGrayscale)
        val paint = Paint()
        val cm = ColorMatrix()
        cm.setSaturation(0f)
        val f = ColorMatrixColorFilter(cm)
        paint.colorFilter = f
        c.drawBitmap(this, 0f, 0f, paint)
        return bmpGrayscale
    }

    fun makeImageSharpGaussian(srcBitmap: Bitmap): Bitmap {
        val src = Mat()
        Utils.bitmapToMat(srcBitmap, src)
        val dest = Mat(src.rows(), src.cols(), src.type())
        val convMat = Mat(3, 3, CvType.CV_32F)
        convMat.put(0,0, -0.1)
        convMat.put(0,1, -0.1)
        convMat.put(0,2, -0.1)
        convMat.put(1,0, -0.1)
        convMat.put(1,1, 2.0)
        convMat.put(1,2, -0.1)
        convMat.put(2,0, -0.1)
        convMat.put(2,1, -0.1)
        convMat.put(2,2, -0.1)
        val point = org.opencv.core.Point(-1.0,-1.0)
        filter2D(src, dest, -1, convMat, point)
        val sharpBitmap = Bitmap.createBitmap(dest.cols(), dest.rows(), Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(dest, sharpBitmap)
        return sharpBitmap
    }

    fun Bitmap.applySharpen(
        radius: FloatArray, context: Context,
    ): Bitmap {
        val bitmap = copy(config,true)
        RenderScript.create(context).apply {
            val input = Allocation.createFromBitmap(this,this@applySharpen)
            val output = Allocation.createFromBitmap(this, this@applySharpen)

            ScriptIntrinsicConvolve3x3.create(
                this, Element.U8_4(this)).apply {
                setInput(input)
                setCoefficients(radius)
                forEach(output)
            }
            output.copyTo(bitmap)
            destroy()
        }
        return bitmap
    }
    fun Bitmap.setBrightnessContrast(
        brightness:Float = 70.0F,
        contrast:Float = 1.4F
    ):Bitmap? {
        val bitmap = copy(Bitmap.Config.ARGB_8888,true)
        val paint = Paint()
        val matrix = ColorMatrix(
            floatArrayOf(
                contrast, 0f, 0f, 0f, brightness,
                0f, contrast, 0f, 0f, brightness,
                0f, 0f, contrast, 0f, brightness,
                0f, 0f, 0f, 1f, 0f
            )
        )
        val filter = ColorMatrixColorFilter(matrix)
        paint.colorFilter = filter
        Canvas(bitmap).drawBitmap(this,0f,0f,paint)
        return bitmap
    }
}
