package com.stxx.wyhvisitorandroid.location

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.baidu.geofence.GeoFence
import com.stxx.wyhvisitorandroid.NOTIFY_ID_FENCE
import com.stxx.wyhvisitorandroid.R

/**
 * description:监听是否进入园区或者离开园区
 * Created by liNan on  2020/5/1 11:20
 */
object GeoBroadCast : BroadcastReceiver() {

    const val fenceaction = "com.stxx.geofence"
    const val channelId = "1"

    //进入或者未进入园区
    var status: Int? = null

    //是否允许提示已经进入园区
    private var isInitEntry = true

    //是否允许提示没进入园区
    private var isInitOut = true

    //是进入园区之后再离开/还是未进入园区之前打开
    private var lastStatus: Int? = null

    //提示离开园区还是未进入园区
    private var outText = "未进入园区"

    var iInvokeLocation: ((Int) -> Unit)? = null
    fun setOnReceiveLocation(status: (Int) -> Unit) {
        iInvokeLocation = status
    }

    override fun onReceive(context: Context, intent: Intent) {

        if (intent.action == fenceaction) {
            val bundle = intent.extras
            status = bundle?.getInt(GeoFence.BUNDLE_KEY_FENCESTATUS)
            iInvokeLocation?.invoke(status ?: 0)
            when (status) {
                GeoFence.INIT_STATUS_IN, GeoFence.STATUS_IN -> {
                    if (isInitEntry) {
                        status = GeoFence.INIT_STATUS_IN
                        showNotify(context, "您已进入园区")
                        isInitEntry = !isInitEntry
                        //重置out
                        isInitOut = true
                        lastStatus = status
                    }

                }
                GeoFence.INIT_STATUS_OUT, GeoFence.STATUS_OUT -> {
                    if (isInitOut) {
                        outText = if (lastStatus == GeoFence.INIT_STATUS_IN) "您已离开园区" else "您未进入园区"
                        status = GeoFence.INIT_STATUS_OUT
                        showNotify(context, outText)
                        isInitOut = !isInitOut
                        //重置int
                        isInitEntry = true

                        lastStatus = status
                    }

                }
            }
        }
    }


    private fun showNotify(context: Context, content: String) {
        val notification = NotificationCompat.Builder(context, channelId).setContentTitle(content)
            .setShowWhen(true)
            .setVibrate(longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400))
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setSmallIcon(R.mipmap.ic_icon, 1).build()
        val notifyManager = NotificationManagerCompat.from(context)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(channelId, "围栏", NotificationManager.IMPORTANCE_DEFAULT)
            notifyManager.createNotificationChannel(notificationChannel)
        }
        notifyManager.notify(NOTIFY_ID_FENCE, notification)
    }

}