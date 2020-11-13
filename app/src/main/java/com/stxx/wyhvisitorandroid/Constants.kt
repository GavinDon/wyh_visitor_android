package com.stxx.wyhvisitorandroid

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import com.baidu.mapapi.model.LatLng
import com.gavindon.mvvm_lib.base.MVVMBaseApplication
import com.gavindon.mvvm_lib.base.ViewModelProviders
import com.gavindon.mvvm_lib.net.SuccessSource
import com.gavindon.mvvm_lib.utils.SpUtils
import com.gavindon.mvvm_lib.utils.getCurrentDateMillSeconds
import com.gavindon.mvvm_lib.widgets.showToast
import com.stxx.wyhvisitorandroid.mplusvm.MineVm
import com.stxx.wyhvisitorandroid.view.splash.MultiFragments
import com.stxx.wyhvisitorandroid.view.splash.WxLoginBindPhoneActivity
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.toast

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

//扫码request_code
const val SCAN_CODE = 0X01
const val BIND_PHONE_RESULT = 119


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

// 人脸识别跳转到我的界面
const val FACE_IDENTIFY = "face_identify"

//景点中心点
//val SCENIC_CENTER_LATLNG = LatLng(40.082681, 116.474134)
val SCENIC_CENTER_LATLNG = LatLng(40.082681, 116.477431)
const val wxappid = "wx697de48974c13c39"
const val wxSecret = "e8cdc89c4adfdf89bc1ddd814bd64515"

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


//跳转到ai步道页面
fun goAiBudaoPage(view: View) {
    val token = judgeLogin()
    val phone = SpUtils.get(LOGIN_NAME_SP, "")
    /**
     *   1.判断是否登陆
     *   2.判断是否绑定了手机号
     *   3.判断是否已经人脸认证
     */
    if (token.isNotEmpty()) {
        if (phone.isNotEmpty()) {
            val ctx = view.context
            if (ctx is MultiFragments) {
                val mineVm = ViewModelProviders.of(ctx).get(MineVm::class.java)
                mineVm.fetchUserInfo().observe(ctx, Observer {
                    if (it is SuccessSource) {
                        if (it.body.data.education == "1") {
                            view.findNavController().navigate(
                                R.id.fragment_webview_notitle,
                                bundleOf(
                                    "url" to "${WebViewUrl.AI_BUDAO}$phone",
                                    "title" to R.string.visitor_ai_budao
                                )
                                , navOption
                            )
                        } else {
                            ctx.toast("请先进行人脸认证")
                            view.findNavController().navigate(R.id.fragment_mine, null, navOption)
                        }
                    } else {
                        ctx.toast("数据出错.请稍候再试")
                    }
                })
            }
        } else {
            view.context.startActivity(
                Intent(
                    view.context,
                    WxLoginBindPhoneActivity::class.java
                )
            )
        }
    } else {
        MVVMBaseApplication.appContext.showToast("请先登陆")
        view.findNavController().navigate(R.id.login_activity, null, navOption)
    }
}


