package com.stxx.wyhvisitorandroid.location

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.baidu.location.*
import com.baidu.mapapi.utils.CoordinateConverter
import com.gavindon.mvvm_lib.base.MVVMBaseApplication

/**
 * description:
 * Created by liNan on  2020/5/1 10:20
 */
class BdLocation(private val locationListener: BDAbstractLocationListener) : LifecycleObserver {

    private lateinit var mLocationClient: LocationClient
    private lateinit var mLocationOption: LocationClientOption
    private val notifyListener = MyNotifyLister()

    private fun initOptions() {
        mLocationOption = LocationClientOption()
        mLocationOption.apply {
            isOpenGps = true
            enableSimulateGps = false
            setOpenAutoNotifyMode()
            scanSpan = 2000
            setCoorType(CoordinateConverter.CoordType.BD09LL.name)
            locationMode = LocationClientOption.LocationMode.Hight_Accuracy
        }

        mLocationClient = LocationClient(MVVMBaseApplication.appContext, mLocationOption)
        mLocationClient.registerLocationListener(locationListener)

        notifyListener.apply {
            //设置位置提醒，参数:纬度、精度、半径、坐标类型LatLng(40.082681, 116.474134)
            SetNotifyLocation(40.082681, 116.474134, 1000f, mLocationClient.locOption.coorType)
        }
        mLocationClient.registerNotify(notifyListener)

    }

    fun startLocation() {
        mLocationClient.start()
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun stopLocation() {
        mLocationClient.stop()
        mLocationClient.removeNotifyEvent(notifyListener)
    }

    init {
        initOptions()
    }

    class MyNotifyLister : BDNotifyListener() {
        override fun onNotify(location: BDLocation?, distance: Float) {
            super.onNotify(location, distance)
        }
    }

}