package com.example.android.databinding.twowaysample.util

import androidx.databinding.InverseMethod
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by dumingwei on 2020-03-09.
 * Desc: 双向绑定转换器
 */
object MyConverter {

    private val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    @InverseMethod("stringToDate")
    @JvmStatic
    fun dateToString(value: Long): String {
        // Converts long to String.
        return format.format(value)
    }

    @JvmStatic
    fun stringToDate(
            value: String
    ): Long {
        // Converts String to long.
        return format.parse(value).time
    }


}