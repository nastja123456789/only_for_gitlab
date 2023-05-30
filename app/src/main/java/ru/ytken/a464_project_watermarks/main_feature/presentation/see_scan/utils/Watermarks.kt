package ru.ytken.a464_project_watermarks.main_feature.presentation.see_scan.utils

import android.util.Log
import kotlin.collections.ArrayList

import kotlin.math.sqrt
import kotlin.math.max
import kotlin.math.min


object Watermarks {
    fun getWatermark(lineBounds: ArrayList<Int>): String? {
        val line = corr(lineBounds)
//        val meanInterval = lineBounds.mean()
//        Log.d("$meanInterval","33333")
//        val stdIntervals = lineBounds.std()
//        if (stdIntervals < 0.4) return null
        val meanInterval = line.mean()
        Log.d("$meanInterval","33333")
        val stdIntervals = line.std()
        if (stdIntervals < 3.0) return null
        var watermark = ""
        for (i in line)
            if (i > meanInterval + stdIntervals*0.35) {
                watermark += "1"
            }
            else {
                watermark += 0
            }
        return watermark
    }

    fun corr(arr: ArrayList<Int>): ArrayList<Int> {
        val modes = arr.groupingBy { it }.eachCount().filter { it.value > 1 }.keys.toList()
        val maxMode = modes.maxOrNull()!!
        val minMode = modes.minOrNull()!!
        var k = 0
        for (i in arr.indices) {
            if (i == arr.size - k) {
                break
            }
            if (arr[i - k] > 1.2 * maxMode) {
                arr[i - k] = maxMode
                arr.add(i, maxMode)
                k++
            }
            if (arr[i - k] < 0.8 * minMode) {
                arr.removeAt(i - k)
                k--
            }
        }
        return arr
    }

    private fun ArrayList<Int>.mean(): Float = this.sum().toFloat() / this.size

    private fun ArrayList<Int>.std(): Float {
        val mean = this.mean()
        var sqSum = 0f
        for (i in this) sqSum += (i - mean)*(i - mean)
        sqSum /= this.size
        return sqrt(sqSum)
    }
}
