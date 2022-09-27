package com.stxx.wyhvisitorandroid.location

import android.location.Location
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.navigation.findNavController
import androidx.navigation.navOptions
import com.baidu.geofence.GeoFence
import com.baidu.location.*
import com.baidu.mapapi.map.BaiduMapOptions
import com.baidu.mapapi.model.LatLng
import com.baidu.mapapi.utils.CoordinateConverter
import com.baidu.mapapi.utils.DistanceUtil
import com.gavindon.mvvm_lib.base.MVVMBaseApplication
import com.gavindon.mvvm_lib.utils.GsonUtil
import com.gavindon.mvvm_lib.widgets.ToastUtil
import com.google.gson.reflect.TypeToken
import com.orhanobut.logger.Logger
import com.stxx.wyhvisitorandroid.R
import com.stxx.wyhvisitorandroid.SCENIC_CENTER_LATLNG
import com.stxx.wyhvisitorandroid.base.MyApplication
import com.stxx.wyhvisitorandroid.bean.LocationBean
import com.stxx.wyhvisitorandroid.bean.VisitGridData
import com.stxx.wyhvisitorandroid.convertBaidu
import com.stxx.wyhvisitorandroid.readAssets
import kotlin.concurrent.thread

/**
 * description:百度定位
 * Created by liNan on  2020/5/1 10:20
 */
object BdLocation2 : LifecycleObserver {

    private lateinit var mLocationClient: LocationClient
    private lateinit var mLocationOption: LocationClientOption
    private var locationObj: List<LocationBean>? = null
    private var lastShowDialogId = -1

    //已经提示过的景点不再提示
    private var hasShowNameLst = mutableSetOf<String>()

    private val mThread = Thread()

    private fun initOptions() {
        mLocationOption = LocationClientOption()
        mLocationOption.apply {
            isOpenGps = true
            enableSimulateGps = false
            setOpenAutoNotifyMode()
            scanSpan = 6000
            isLocationNotify = true
            setCoorType(CoordinateConverter.CoordType.BD09LL.name)
            locationMode = LocationClientOption.LocationMode.Hight_Accuracy
        }
        mLocationClient = LocationClient(MVVMBaseApplication.appContext, mLocationOption)
        //监听位置变化
        mLocationClient.registerLocationListener(listener)

        //设置位置提醒，参数:纬度、精度、半径、坐标类型LatLng(40.082681, 116.474134)
//        bdNotifyLister.SetNotifyLocation(
//            40.085884758824925,
//            116.473419535023,
//            1000f,
//            mLocationClient.locOption.coorType
//        )
//        bdNotifyLister.SetNotifyLocation(
//            40.076847185822174,
//            116.46784880133484,
//            1000f,
//            mLocationClient.locOption.coorType
//        )
//        mLocationClient.registerNotify(bdNotifyLister)

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

    fun bdLocationListener(listener: ((location: BDLocation) -> Unit)? = null): BdLocation2 {
        this.locationListener = listener
        return this
    }

    private var locationListener: ((location: BDLocation) -> Unit)? = null


    /*根据位置来弹出一些内容*/
    fun setDistanceListener(listener: ((bean: LocationBean) -> Unit)? = null): BdLocation2 {
        this.distanceListener = listener
        return this
    }

    private var distanceListener: ((bean: LocationBean) -> Unit)? = null

    /**
     * 定位结果回调
     */
    private val listener = object : BDAbstractLocationListener() {
        override fun onReceiveLocation(location: BDLocation) {
            Logger.d("chage")
            if (location.locType != BDLocation.TypeServerError &&
                location.locType != BDLocation.TypeOffLineLocationFail &&
                location.locType != BDLocation.TypeCriteriaException
            ) {
                locationListener?.invoke(location)
                val startLatlng = LatLng(location.latitude, location.longitude)
                calculateNear(startLatlng)
            }
        }
    }

    /**
     * 计算最近的景点
     */
    private fun calculateNear(startLatLng: LatLng) {
//        ToastUtil.instance?.show("cal", 0)
/*        thread {
            locationObj?.forEach {
                val distance = DistanceUtil.getDistance(
                    startLatLng,
                    convertBaidu(it.y.toDouble(), it.x.toDouble())
                )
                if (distance < 50) {
                    //如果已经弹出过提示则不在弹出对话框
                    if (hasShowNameLst.contains(it.name)) return@forEach
                    distanceListener?.invoke(it)
                    hasShowNameLst.add(it.name)
                }
//                Logger.i(distance.toString())
            }
        }*/
              mThread.run {
                  locationObj?.forEach {
                      val distance = DistanceUtil.getDistance(
                          startLatLng,
                          convertBaidu(it.y.toDouble(), it.x.toDouble())
                      )

                      if (distance < 200) {
                          //如果已经弹出过提示则不在弹出对话框
                          if (hasShowNameLst.contains(it.name)) return
                          distanceListener?.invoke(it)
                          hasShowNameLst.add(it.name)
                      }
      //                Logger.i(distance.toString())
                  }
              }


    }

    init {
        initOptions()
        val locationJson = readAssets(MVVMBaseApplication.appContext, "json/location.json")
        val type = object : TypeToken<List<LocationBean>>() {}.type
        locationObj = GsonUtil.str2Obj<List<LocationBean>>(locationJson, type)

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun stopLocation() {
        if (mLocationClient.isStarted) {
            mLocationClient.stop()
            mLocationClient.removeNotifyEvent(bdNotifyLister)
        }
    }

}