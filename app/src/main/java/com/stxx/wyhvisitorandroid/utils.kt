package com.stxx.wyhvisitorandroid

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Outline
import android.os.Build
import android.text.InputFilter
import android.text.Spanned
import android.text.TextUtils
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.EditText
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.pm.PackageInfoCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.baidu.mapapi.model.LatLng
import com.baidu.mapapi.utils.CoordinateConverter
import com.gavindon.mvvm_lib.base.MVVMBaseApplication
import com.gavindon.mvvm_lib.utils.GsonUtil
import com.gavindon.mvvm_lib.utils.showSoftInputWord
import com.gavindon.mvvm_lib.widgets.showToast
import com.google.gson.reflect.TypeToken
import com.stxx.wyhvisitorandroid.bean.VisitGridData
import com.stxx.wyhvisitorandroid.location.GeoBroadCast
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.util.regex.Pattern

/**
 * description:
 * Created by liNan on  2020/5/13 10:44
 */

fun showNotify(context: Context, content: String) {
    val channelId = "2"
    val notification =
        NotificationCompat.Builder(context, channelId).setContentTitle(content)
            .setShowWhen(true)
            .setVibrate(longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400))
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setSmallIcon(R.mipmap.ic_icon, 1).build()
    val notifyManager = NotificationManagerCompat.from(context)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val notificationChannel =
            NotificationChannel(
                GeoBroadCast.channelId,
                "围栏",
                NotificationManager.IMPORTANCE_DEFAULT
            )
        notifyManager.createNotificationChannel(notificationChannel)
    }
    notifyManager.notify(1, notification)
}

val viewOutlineProvider = object : ViewOutlineProvider() {
    override fun getOutline(view: View?, outline: Outline?) {
    }
}

fun convertBaidu(lat: Double, lng: Double): LatLng {
    return try {
        CoordinateConverter()
            .from(CoordinateConverter.CoordType.GPS)
            .coord(LatLng(lat, lng))
            .convert()
    } catch (ex: java.lang.Exception) {
        LatLng(0.0, 0.0)
    }
}

fun readAssets(context: Context, dir: String): String {

    var inputStream: InputStream? = null
    var inputStreamReader: InputStreamReader? = null
    var bufferedReader: BufferedReader? = null

    try {
        inputStream = context.resources.assets.open(dir)
        inputStreamReader = InputStreamReader(inputStream)
        bufferedReader = BufferedReader(inputStreamReader)
        var strJson = bufferedReader.readLine()
        val stringBuilder = StringBuilder()
        while (strJson != null) {
            stringBuilder.append(strJson)
            strJson = bufferedReader.readLine()
        }
        val type = object : TypeToken<List<VisitGridData>>() {}.type
        val obj = GsonUtil.str2Obj<List<VisitGridData>>(stringBuilder.toString(), type)
        return stringBuilder.toString()
    } catch (ex: Exception) {
    } finally {
        inputStream?.close()
        inputStreamReader?.close()
        bufferedReader?.close()
    }
    return ""

}

/**
 * 让显示自动刷新 不会请求数据
 */
fun SwipeRefreshLayout.showRefreshIcon() {
    this.measure(0, 0)
    this.post {
        this.isRefreshing = true
    }
    this.setColorSchemeResources(R.color.colorTabSelect)
}

val Context.fileProviderAuth: String
    get() {
        val metadata =
            packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
        var fileProvider = metadata.metaData.getString("file_provider")
        if (fileProvider.isNullOrEmpty()) {
            fileProvider = "com.stxx.wyhvisitorandroid.FileProvider"
        }
        return fileProvider
    }

/**
 * 可加载多少页数据
 */
const val pageSize = 10
val Int.pageTotalNum: Int
    get() {
        return if (this > 0) {
            val pageNum = this / pageSize
            if (this % pageSize == 0) {
                pageNum
            } else {
                pageNum + 1
            }
        } else {
            0
        }
    }

/**
 * 提示输入并弹出软键盘
 */
fun EditText.getFocus(str: String) {
    this.apply {
        requestFocus()
        showSoftInputWord()
    }
    this.context?.showToast(str)
}


/**
 * 检查是否有安装AR科普的包
 */
val checkInstallAr: PackageInfo?
    get() {
        val packageManager = MVVMBaseApplication.appContext.packageManager
        var arPackageInfo: PackageInfo? = null
        try {
            arPackageInfo = packageManager.getPackageInfo("com.stxx.wyh_unity_ar", 0)

        } catch (ex: PackageManager.NameNotFoundException) {
        }
        return arPackageInfo
    }


/**
 * 过滤表情，空格
 */
fun EditText.filterSpaceEmoji(lengthMax: Int = 10): EditText {
    this.filters = arrayOf(SpaceEmjFilter(), InputFilter.LengthFilter(lengthMax))
    return this
}

/**
 * 过滤表情，空格，特殊字符
 */
fun EditText.filterSpaceEmojiSpecial(lengthMax: Int = 10): EditText {
    this.filters =
        arrayOf(SpaceEmjFilter(), SpecialCharFilter(), InputFilter.LengthFilter(lengthMax))
    return this
}


/**
 * 过滤表情和空格
 */
class SpaceEmjFilter : InputFilter {
    override fun filter(
        source: CharSequence,
        start: Int,
        end: Int,
        dest: Spanned,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        if (source == " ") {
            return ""
        }
        if (isEmoji(source.toString())) {
            MVVMBaseApplication.appContext.showToast("不可以输入表情")
            return ""
        }
        return null
    }

}

class SpecialCharFilter : InputFilter {
    override fun filter(
        source: CharSequence,
        start: Int,
        end: Int,
        dest: Spanned?,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        val regexStr =
            "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]"

        return if (Pattern.matches(regexStr, source)) {
            ""
        } else {
            null
        }
    }

}

fun isEmoji(str: String): Boolean {
    str.forEachIndexed { index, _ ->
        val codePoint = Character.codePointAt(str, index)
        return (codePoint in 0x0080..0x02AF) ||
                (codePoint in 0x0300..0x03FF) ||
                (codePoint in 0x0600..0x06FF) ||
                (codePoint in 0x0C00..0x0C7F) ||
                (codePoint in 0x1DC0..0x1DFF) ||
                (codePoint in 0x1E00..0x1EFF) ||
                (codePoint in 0x2000..0x209F) ||
                (codePoint in 0x20D0..0x214F) ||
                (codePoint in 0x2190..0x23FF) ||
                (codePoint in 0x2460..0x25FF) ||
                (codePoint in 0x2600..0x27EF) ||
                (codePoint in 0x2900..0x29FF) ||
                (codePoint in 0x2B00..0x2BFF) ||
                (codePoint in 0x2C60..0x2C7F) ||
                (codePoint in 0x2E00..0x2E7F) ||
                (codePoint in 0xA490..0xA4CF) ||
                (codePoint in 0xE000..0xF8FF) ||
                (codePoint in 0xFE00..0xFE0F) ||
                (codePoint in 0xFE30..0xFE4F) ||
                (codePoint in 0x1F000..0x1F02F) ||
                (codePoint in 0x1F0A0..0x1F0FF) ||
                (codePoint in 0x1F100..0x1F64F) ||
                (codePoint in 0x1F680..0x1F6FF) ||
                (codePoint in 0x1F910..0x1F96B) ||
                (codePoint in 0x1F980..0x1F9E0)
    }
    return false
}








