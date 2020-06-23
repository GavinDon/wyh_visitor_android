package com.gavindon.mvvm_lib.utils

import android.content.Context
import android.content.res.Resources
import android.util.TypedValue
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

/**
 * description:
 * Created by liNan on 2019/12/24 15:52

 */
inline fun <reified T> genericType(): Type = object : TypeToken<T>() {}.type

//匹配所有手机卡正则
const val phoneRegex =
    "^(?:\\+?86)?1(?:3\\d{3}|5[^4\\D]\\d{2}|8\\d{3}|7(?:[35678]\\d{2}|4(?:0\\d|1[0-2]|9\\d))|9[01356789]\\d{2}|66\\d{2})\\d{6}\$"
//邮箱
const val emailRegex = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+\$"
//昵称
const val nickNameRegex = "^[a-zA-Z0-9_\\u4e00-\\u9fa5]+\$"


fun Int.dp2px(context: Context): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        context.resources.displayMetrics
    )
}


val Int.px2dp
    get() = run {
        val dm = Resources.getSystem().displayMetrics
        val scale = dm.density
        (this / scale + 0.5f).toInt()
    }

val Int.dp2px
    get() = run {
        val dm = Resources.getSystem().displayMetrics
        val scale = dm.density
        (this * scale + 0.5f).toInt()
    }