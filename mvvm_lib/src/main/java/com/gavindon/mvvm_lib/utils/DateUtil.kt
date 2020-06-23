package com.gavindon.mvvm_lib.utils

import com.gavindon.mvvm_lib.utils.DatePattern._ymd
import com.gavindon.mvvm_lib.utils.DatePattern._ymd_hms
import java.text.SimpleDateFormat
import java.util.*

/**
 * 获取当前时间 包含时分秒
 */
fun getCurrentFullDate(): String {
    val date = Date()
    val simpleDateFormat = SimpleDateFormat(_ymd_hms, Locale.CHINA)
    return simpleDateFormat.format(date)
}

/**
 * 获取当前时间 不包含时分秒
 */
fun getCurrentDate(): String {
    val date = Date()
    val simpleDateFormat = SimpleDateFormat(_ymd, Locale.CHINA)
    return simpleDateFormat.format(date)
}

fun getCurrentDateMillSeconds(): Long {
    return System.currentTimeMillis()
}

/**
 * 格式化日期为年月日
 */
fun formatDate2YMD(date: String?): String {
    val sdf = SimpleDateFormat(_ymd_hms, Locale.getDefault())
    return if (date != null) {
        val strDate = sdf.parse(date)
        SimpleDateFormat(_ymd, Locale.getDefault()).format(strDate)
    } else {
        ""
    }
}

/**
 * 时间格式
 */
object DatePattern {
    //年月日 之间使用-
    const val _ymd_hms = "yyyy-MM-dd HH:mm:ss"
    const val _ymd = "yyyy-MM-dd"
}

