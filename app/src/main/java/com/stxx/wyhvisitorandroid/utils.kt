package com.stxx.wyhvisitorandroid

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Outline
import android.os.Build
import android.text.InputFilter
import android.text.Spanned
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.EditText
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.gavindon.mvvm_lib.base.MVVMBaseApplication
import com.gavindon.mvvm_lib.utils.showSoftInputWord
import com.gavindon.mvvm_lib.widgets.showToast
import com.stxx.wyhvisitorandroid.location.GeoBroadCast
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
 * @param origin_lon 原点经度
 * @param origin_lat 原点纬度
 * @param azimuth 偏移角度
 * @param distance 延伸距离
 */
fun azimuth_offset(
    origin_lon: Double,
    origin_lat: Double,
    azimuth: Int?,
    distance: Double
): Array<Double> {
    val lonlat = arrayOf<Double>()
    if (azimuth != null && azimuth == 0 && distance > 0) {
        lonlat[0] =
            origin_lon + distance * Math.sin(azimuth * Math.PI / 180) * 180 / (Math.PI * 6371229 * Math.cos(
                origin_lat * Math.PI / 180
            ))
        lonlat[1] =
            origin_lat + distance * Math.cos(azimuth * Math.PI / 180) / (Math.PI * 6371229 / 180)
    } else {
        lonlat[0] = origin_lon
        lonlat[1] = origin_lat
    }
    return lonlat
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
        if (isEmoji2(source.toString())) {
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

/**
 * 判断是否是表情
 */
fun isEmoji(string: String): Boolean {
    val p =
        Pattern.compile(
            "[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",
            Pattern.UNICODE_CASE or Pattern.CASE_INSENSITIVE
        )

    val matcher = p.matcher(string)
    return matcher.matches()
}

fun isEmoji2(str: String): Boolean {
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






