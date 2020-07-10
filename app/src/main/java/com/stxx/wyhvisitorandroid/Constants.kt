package com.stxx.wyhvisitorandroid

import androidx.navigation.NavOptions
import com.gavindon.mvvm_lib.utils.SpUtils
import com.gavindon.mvvm_lib.utils.getCurrentDateMillSeconds

/**
 * description:
 * Created by liNan on  2020/2/26 09:47
 */

const val LOGIN_NAME_SP = "loginName"
const val PASSWORD_SP = "password"
const val TOKEN = "token"
const val FIRST_INSTALL = "first_install"
const val HISTORY_SEARCH_SP = "search_history"

//是否显示机器人按钮
const val OPEN_ROBOT_SP = "open_robot"

//围栏，语音讲解,下载AR科普apk
const val NOTIFY_ID_FENCE = 1
const val NOTIFY_ID_SOUND = 2
const val NOTIFY_ID_DOWNLOAD = 3


//跳转到热门推荐、新闻资讯等详情页面bundle key
const val BUNDLE_DETAIL = "detail"

//跳转到景区地图选中tab的索引
const val BUNDLE_SELECT_TAB = "select_tab"

//跳转到景点详情
const val BUNDLE_SCENIC_DETAIL = "scenic_detail"
const val BUNDLE_IS_ROBOT = "robot"

//机器人识别出是否是子景点名称
const val BUNDLE_IS_SUB_SCENIC = "scenicItem"

//需要跳转的webView url
const val WEB_VIEW_URL = "url"
const val WEB_VIEW_TITLE = "title"


/**
 * fragment转场设置
 */
val navOption: NavOptions
    get() = NavOptions.Builder()
        .setEnterAnim(R.anim.anim_right_in)
        .setExitAnim(R.anim.anim_left_out)
//        .setPopEnterAnim(androidx.fragment.R.anim.fragment_close_enter)
        .setPopExitAnim(R.anim.alpha_exit)
        .build()

val alphaNavOption: NavOptions
    get() = NavOptions.Builder()
        .setEnterAnim(R.anim.alpha_enter)
        .setExitAnim(R.anim.alpha_exit)
        .setPopExitAnim(R.anim.alpha_exit)
        .build()

fun judgeLogin(): String {
    val token = SpUtils.get(TOKEN, "")
    if (token.isNotEmpty()) {
        val preTimeSplit = token.split("-")
        if (preTimeSplit.size == 2) {
            val preTime = preTimeSplit[1]
            if (getTimeDiffMinute(preTime.toLong())) {
                return preTimeSplit[0]
            }
        }
    }
    return ""
}

/**
 *获取时间是否已经超过30分钟
 * @return 小于30分钟为true 已经登陆
 */
fun getTimeDiffMinute(preTime: Long): Boolean {
    val diffSeconds = getCurrentDateMillSeconds() - preTime

    val diff = diffSeconds / 1000 / 60
    //保存token是否超过了30分钟
    return diff <= 120
}


