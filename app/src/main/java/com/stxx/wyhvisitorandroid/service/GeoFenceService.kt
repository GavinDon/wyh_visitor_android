package com.stxx.wyhvisitorandroid.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.baidu.geofence.GeoFenceClient
import com.orhanobut.logger.Logger
import com.stxx.wyhvisitorandroid.location.GeoBroadCast

/**
 * description:
 * Created by liNan on  2022-9-27 14:39
 */
class GeoFenceService : Service() {

    private val mGeoFenceClient by lazy { GeoFenceClient(this) }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Logger.i("onStartCommand")
        mGeoFenceClient.clear()
        mGeoFenceClient.addGeoFence("北京温榆河公园朝阳一期", "公园", "北京", 5, " 0001")
        //初始化围栏(在位置回调中先进行移除再添加达到每隔6s回调一次)
        mGeoFenceClient.createPendingIntent(GeoBroadCast.fenceaction)
        mGeoFenceClient.setTriggerCount(Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE)
        mGeoFenceClient.setActivateAction(GeoFenceClient.GEOFENCE_IN_OUT)

        return START_STICKY
    }

    override fun onDestroy() {
        stopSelf()
    }
}