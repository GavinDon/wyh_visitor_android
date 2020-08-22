package com.stxx.wyhvisitorandroid.location

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.baidu.geofence.GeoFence
import com.baidu.location.*
import com.baidu.mapapi.map.BaiduMapOptions
import com.baidu.mapapi.model.LatLng
import com.baidu.mapapi.utils.CoordinateConverter
import com.baidu.mapapi.utils.DistanceUtil
import com.gavindon.mvvm_lib.base.MVVMBaseApplication
import com.orhanobut.logger.Logger
import com.stxx.wyhvisitorandroid.SCENIC_CENTER_LATLNG

/**
 * description:百度定位
 * Created by liNan on  2020/5/1 10:20
 */
object BdLocation2 : LifecycleObserver {

    private lateinit var mLocationClient: LocationClient
    private lateinit var mLocationOption: LocationClientOption


    private fun initOptions() {
        mLocationOption = LocationClientOption()
        mLocationOption.apply {
            isOpenGps = true
            enableSimulateGps = false
            setOpenAutoNotifyMode()
            scanSpan = 2000
            isLocationNotify = true
            setCoorType(CoordinateConverter.CoordType.BD09LL.name)
            locationMode = LocationClientOption.LocationMode.Hight_Accuracy
        }
        mLocationClient = LocationClient(MVVMBaseApplication.appContext, mLocationOption)
        //监听位置变化
        mLocationClient.registerLocationListener(listener)

        //设置位置提醒，参数:纬度、精度、半径、坐标类型LatLng(40.082681, 116.474134)
        bdNotifyLister.SetNotifyLocation(
            40.085884758824925,
            116.473419535023,
            1000f,
            mLocationClient.locOption.coorType
        )
//        bdNotifyLister.SetNotifyLocation(
//            40.076847185822174,
//            116.46784880133484,
//            1000f,
//            mLocationClient.locOption.coorType
//        )
        mLocationClient.registerNotify(bdNotifyLister)

    }

    /**
     * 处理位置变化提示
     */
    private val bdNotifyLister = object : BDNotifyListener() {
        override fun onNotify(location: BDLocation?, distance: Float) {
            super.onNotify(location, distance)
            Logger.i("bdlocation${location?.country}$distance")
        }
    }

    val startLocation: BdLocation2
        get() {
            if (!mLocationClient.isStarted) {
                mLocationClient.start()
            }
            return this
        }

    fun bdLocationListener(listener: ((location: BDLocation) -> Unit)? = null) {
        this.locationListener = listener
    }

    private var locationListener: ((location: BDLocation) -> Unit)? = null

    /**
     * 定位结果回调
     */
    private val listener = object : BDAbstractLocationListener() {
        override fun onReceiveLocation(location: BDLocation) {
            if (location.locType != BDLocation.TypeServerError &&
                location.locType != BDLocation.TypeOffLineLocationFail &&
                location.locType != BDLocation.TypeCriteriaException
            ) {
                locationListener?.invoke(location)
                val startLatlng = LatLng(location.latitude, location.longitude)
            }
        }
    }

    /**
     * 计算最近的景点
     */
    private fun calculateNear(startLatlng: LatLng) {
        val distance = DistanceUtil.getDistance(startLatlng, SCENIC_CENTER_LATLNG)
        if (distance < 10000) {
            //计算提示哪个景点
        }

    }

    init {
        initOptions()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun stopLocation() {
        mLocationClient.stop()
        mLocationClient.removeNotifyEvent(bdNotifyLister)
    }

}