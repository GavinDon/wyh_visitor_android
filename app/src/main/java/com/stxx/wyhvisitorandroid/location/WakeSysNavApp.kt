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
import com.stxx.wyhvisitorandroid.location.GpsUtil.bd09_To_Gcj02
import org.jetbrains.anko.layoutInflater
import org.jetbrains.anko.runOnUiThread
import java.lang.StringBuilder
import java.net.URISyntaxException
import kotlin.concurrent.thread


/**
 * description: 唤醒百度/高德 app
 * Created by liNan on  2020/5/12 13:58
 */
//http://lbsyun.baidu.com/index.php?title=uri/api/android
fun wakeBaiduApp(
    context: Context,
    startPoint: LatLng,
    endLatLng: LatLng,
    endName: String?
) {
//    val endPoint = LatLng(40.08817693078905, 116.46738443454991)
    val paraOption = RouteParaOption()
        .startPoint(startPoint)
        .cityName("北京")
        .endPoint(endLatLng)
        .endName("温榆河-${endName}")

    try {
        BaiduMapRoutePlan.openBaiduMapDrivingRoute(paraOption, context)

    } catch (e: Exception) {
        e.printStackTrace()
    }
}

/**
 * 高德app不需要传入起点 使用高德默认的定位位置
 *  https://lbs.amap.com/api/amap-mobile/guide/android/route
 */
fun wakeGaodeApp(
    context: Context,
    endLatLng: LatLng,
    endName: String?
) {
    try {
        val bdConvertGd = bd09_To_Gcj02(endLatLng.latitude, endLatLng.longitude)
        val uri = StringBuilder()
        uri.append("androidamap://route?sourceApplication=maxuslife")
            .append("&dlat=").append(bdConvertGd[0])
            .append("&dlon=").append(bdConvertGd[1])
            .append("&dname=").append("温榆河-$endName")
            .append("&dev=0")
            .append("&t=0")
        val intent = Intent.parseUri(uri.toString(), Intent.URI_INTENT_SCHEME)
        startActivity(context, intent, null)
    } catch (e: URISyntaxException) {
        e.printStackTrace()
    }
}

/**
 * @param startLatLng 导航起点 坐标系为百度
 * @param endLatLng 导航终点 坐标系为百度
 * @param name 导航终点名
 */
fun showWakeApp(
    context: Context,
    startLatLng: LatLng,
    endLatLng: LatLng,
    endName: String?
) {
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
        wakeBaiduApp(context, startLatLng, endLatLng, endName)
    }
    //打开高德,使用高德默认的起点
    tvGaode?.setOnClickListener {
        wakeGaodeApp(context, endLatLng, endName)
    }
    bottomDialog.show()
}


fun tencentAPP() {
//    Uri.parse("qqmap://map/routeplan?type=drive&from=天坛南门&fromcoord=39.873145,116.413306&to=国家大剧院&tocoord=39.907380,116.388501"));
}

fun release() {

}