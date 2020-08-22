package com.stxx.wyhvisitorandroid.location

import android.content.Context
import android.content.Intent
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import com.baidu.mapapi.model.LatLng
import com.baidu.mapapi.utils.route.BaiduMapRoutePlan
import com.baidu.mapapi.utils.route.RouteParaOption
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.gavindon.mvvm_lib.utils.NavUtils
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.stxx.wyhvisitorandroid.R
import org.jetbrains.anko.layoutInflater
import org.jetbrains.anko.runOnUiThread
import java.net.URISyntaxException
import kotlin.concurrent.thread


/**
 * description: 唤醒百度/高德 app
 * Created by liNan on  2020/5/12 13:58
 */
fun wakeBaiduApp(context: Context, startPoint: LatLng) {
    val endPoint = LatLng(40.08817693078905, 116.46738443454991)
    val paraOption = RouteParaOption()
        .startPoint(startPoint)
        .endPoint(endPoint)
    try {
        BaiduMapRoutePlan.openBaiduMapTransitRoute(paraOption, context)
    } catch (e: Exception) {
        e.printStackTrace();
    }
}

fun wakeGaodeApp(context: Context) {
    try {
        val intent =
            Intent.parseUri(
                "androidamap://route?sourceApplication=maxuslife&dlat=40.08817693078905&dlon=116.46738443454991&dev=0&t=0",
                Intent.URI_INTENT_SCHEME
            )
        startActivity(context, intent, null)
    } catch (e: URISyntaxException) {
        e.printStackTrace()
    }
}

fun showWakeApp(context: Context, currentLatitude: Double, currentLongitude: Double) {
    val bottomDialog = BottomSheetDialog(context)
    val bottomView = context.layoutInflater.inflate(R.layout.bottom_wake_nav_app, null, false)
    bottomDialog.setContentView(bottomView)
    val tvBaidu = bottomDialog.findViewById<TextView>(R.id.tvBaiduNav)
    val tvGaode = bottomDialog.findViewById<TextView>(R.id.tvGaodeNav)
    val navAppList =
        NavUtils.checkInstallPackage(context)
    for (i in navAppList) {
        val packageName = i.first
        if (packageName == NavUtils.bd_package) {
            thread {
                val drawable =
                    Glide.with(context).load(i.second).apply(RequestOptions.circleCropTransform())
                        .submit()
                        .get()
                drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
                context.runOnUiThread {
                    tvBaidu?.setCompoundDrawables(null, drawable, null, null)
                }
            }
        } else if (packageName == NavUtils.gd_package) {
            thread {
                Glide.with(context).load(i.second).apply(RequestOptions.circleCropTransform())
                    .submit()
                    .get().also {
                        it.setBounds(
                            0, 0,
                            tvBaidu!!.compoundDrawables[1].minimumWidth,
                            tvBaidu.compoundDrawables[1].minimumHeight
                        )
                        context.runOnUiThread {
                            tvGaode?.text = "高德地图"
                            tvGaode?.setCompoundDrawables(null, it, null, null)
                        }
                    }
            }
        }
    }

    //打开百度
    tvBaidu?.setOnClickListener {
        wakeBaiduApp(context, LatLng(currentLatitude, currentLongitude))
    }
    //打开高德
    tvGaode?.setOnClickListener {
        wakeGaodeApp(context)
    }
    bottomDialog.show()
}

fun tencentAPP() {
//    Uri.parse("qqmap://map/routeplan?type=drive&from=天坛南门&fromcoord=39.873145,116.413306&to=国家大剧院&tocoord=39.907380,116.388501"));
}

fun release() {

}