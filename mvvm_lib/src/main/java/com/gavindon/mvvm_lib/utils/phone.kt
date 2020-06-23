package com.gavindon.mvvm_lib.utils

import android.content.Context
import android.content.res.Resources
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.gavindon.mvvm_lib.base.MVVMBaseApplication
import com.orhanobut.logger.Logger


/**
 * description:
 * Created by liNan on 2019/12/27 10:00

 */
fun getDenstity() {
    val xDpi = Resources.getSystem().displayMetrics.xdpi
    val yDpi = Resources.getSystem().displayMetrics.ydpi
    val dpi = Resources.getSystem().displayMetrics.densityDpi

}

/**
 * 获取状态栏高度
 *
 * @param context context
 * @return 状态栏高度
 */
fun getStatusBarHeight(context: Context?): Int { // 获得状态栏高度
    return if (context == null) {
        25
    } else {
        val resourceId =
            context.resources.getIdentifier("status_bar_height", "dimen", "android")
        context.resources.getDimensionPixelSize(resourceId)
    }

}

val phoneHeight: Int
    get() = Resources.getSystem().displayMetrics.heightPixels

val phoneWidth: Int
    get() = Resources.getSystem().displayMetrics.widthPixels

fun getAppList(context: Context) {
    val pm = context.packageManager
    val lstPackage = pm.getInstalledPackages(0)
    for (i in lstPackage) {
        Logger.i(i.packageName)
    }
}

fun getVersionName(): String {
    val pm = MVVMBaseApplication.appContext.packageManager
    val applicationInfo = MVVMBaseApplication.appContext.applicationInfo
    val info = pm.getPackageInfo(applicationInfo.packageName, 0)
    return info.versionName
}

fun EditText.showSoftInputWord() {
    val imm: InputMethodManager =
        MVVMBaseApplication.appContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)

}

fun EditText.hideSoftInputWord() {
    val imm: InputMethodManager =
        MVVMBaseApplication.appContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(this.windowToken, 0)

}

