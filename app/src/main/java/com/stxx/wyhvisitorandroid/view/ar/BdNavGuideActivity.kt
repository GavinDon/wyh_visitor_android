package com.stxx.wyhvisitorandroid.view.ar

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import androidx.core.graphics.drawable.toDrawable
import com.baidu.mapapi.map.*
import com.baidu.mapapi.model.LatLng
import com.baidu.mapapi.model.LatLngBounds
import com.baidu.mapapi.search.route.PlanNode
import com.baidu.mapapi.search.route.RoutePlanSearch
import com.baidu.mapapi.search.route.WalkingRoutePlanOption
import com.baidu.mapapi.search.route.WalkingRouteResult
import com.baidu.mapapi.walknavi.adapter.IWNaviStatusListener
import com.baidu.mapapi.walknavi.model.WalkNaviDisplayOption
import com.baidu.platform.comapi.walknavi.WalkNaviModeSwitchListener
import com.baidu.platform.comapi.walknavi.widget.ArCameraView
import com.baidu.tts.client.SpeechSynthesizer
import com.baidu.tts.client.TtsMode
import com.gavindon.mvvm_lib.utils.requestPermission
import com.gavindon.mvvm_lib.widgets.showToast
import com.stxx.wyhvisitorandroid.R
import com.stxx.wyhvisitorandroid.SCENIC_CENTER_LATLNG
import com.stxx.wyhvisitorandroid.location.BdLocation2
import com.stxx.wyhvisitorandroid.view.ar.WalkNavUtil.walkNavigateHelper
import com.stxx.wyhvisitorandroid.view.asr.Auth
import com.stxx.wyhvisitorandroid.view.helpers.SimpleIWRouteGuidanceListener
import com.stxx.wyhvisitorandroid.view.helpers.SimpleOnGetRoutePlanResultListener
import com.stxx.wyhvisitorandroid.view.overlayutil.WalkingRouteOverlay


/**
 * description: AR导航类
 * Created by liNan on  2020/8/24 9:27
 */
class BdNavGuideActivity : Activity() {

    private var mSpeechSynthesizer: SpeechSynthesizer? = null
    private var baiduMapView: MapView? = null
    private var btnLocation: ImageButton? = null
    private val startLatLng: LatLng by lazy { intent.getParcelableExtra("start") as LatLng }
    private val endLatLng: LatLng by lazy { intent.getParcelableExtra("end") as LatLng }
    private val search by lazy { RoutePlanSearch.newInstance() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        initSpeech()
        navStateListener()
        //开启导航
        walkNavigateHelper.startWalkNavi(this)
        searchRoute()
        initMap()
        requestLocation()
    }

    private fun initView() {
        try {
            //AR导航view
            val navView: View? = walkNavigateHelper.onCreate(this)
            //必须判断是否为null
            if (navView != null) {
                val rootView = layoutInflater.inflate(R.layout.activity_bdnav_guide, null, false)
                val frlNavView = rootView.findViewById<FrameLayout>(R.id.frlNavGuide)
                baiduMapView = rootView.findViewById(R.id.navGuideMapView)
                btnLocation = rootView.findViewById(R.id.ivMoveToCenterLocation)
                //添加到frameLayout容器中
                frlNavView.addView(navView)
                //设置options
                walkNavigateHelper.setWalkNaviDisplayOption(initNavOption())
                setContentView(rootView)
            }

        } catch (e: Exception) {

        }

    }

    private fun initNavOption(): WalkNaviDisplayOption {
        val option = WalkNaviDisplayOption()
        val close = BitmapFactory.decodeResource(resources, R.mipmap.grid_enter_book)
        val drawable = close.toDrawable(resources)
        drawable.alpha = 100
        option.setArNaviResources(
            drawable.bitmap
            , close, close
        )
        return option
    }

    /**
     * 添加手绘图
     */
    private fun initMap() {
        baiduMapView?.showZoomControls(false)
        val map = baiduMapView?.map

        val builder = MapStatus.Builder()
        builder.zoom(15f)
        builder.target(SCENIC_CENTER_LATLNG)
        val mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(builder.build())
        map?.setMapStatus(mMapStatusUpdate)
        map?.apply {
            setOnMapLoadedCallback(onMapLoadListener)
            setMaxAndMinZoomLevel(18f, 14f)
        }
        val urlTileProvider = object : UrlTileProvider() {
            override fun getMinDisLevel(): Int = 14
            override fun getMaxDisLevel(): Int = 18
            override fun getTileUrl(): String {
                return "https://tourist.wenyuriverpark.com/mapTiles/{z}/tile{x}_{y}.png"
            }
        }
        val options = TileOverlayOptions().apply {
            val northEast = LatLng(40.06048593512643, 116.39524272759365)
            val southEast = LatLng(40.071822098761984, 116.46385409389569)
            tileProvider(urlTileProvider)
            setPositionFromBounds(
                LatLngBounds.Builder().include(northEast).include(southEast).build()
            )
        }
        map?.addTileLayer(options)
        btnLocation?.setOnClickListener {
            fixedLocation2Center()
        }
    }

    /**
     * 显示在中间位置
     */
    private fun fixedLocation2Center(latLng: LatLng = SCENIC_CENTER_LATLNG) {
        val mapStatus = MapStatus.Builder()
            .target(latLng)
            .build()
        val mapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mapStatus)
        baiduMapView?.map?.setMapStatus(mapStatusUpdate)
    }

    private fun requestLocation() {
        val map = baiduMapView?.map
        map?.isMyLocationEnabled = true
        //开始定位
        requestPermission(Manifest.permission.ACCESS_FINE_LOCATION) {
            BdLocation2.startLocation.bdLocationListener {
                //防止定位回调时 View已经注销
                val locationData = MyLocationData.Builder()
                    .direction(it.direction)
                    .accuracy(it.radius)
                    .latitude(it.latitude)
                    .longitude(it.longitude)
                    .build()
                map?.setMyLocationData(locationData)
            }
        }
    }

    private val onMapLoadListener = BaiduMap.OnMapLoadedCallback { }

    private fun searchRoute() {

        val tRoutePlanResultListener = object : SimpleOnGetRoutePlanResultListener() {
            override fun onGetWalkingRouteResult(walkResult: WalkingRouteResult) {
                val overlay = WalkingRouteOverlay(baiduMapView?.map)
                val routeLines = walkResult.routeLines
                if (!routeLines.isNullOrEmpty()) {
                    overlay.setData(walkResult.routeLines[0])
                    overlay.addToMap()
                }
            }
        }
        search.setOnGetRoutePlanResultListener(tRoutePlanResultListener)
        search.walkingSearch(
            WalkingRoutePlanOption()
                .from(PlanNode.withLocation(startLatLng))
                .to(
                    PlanNode.withLocation(endLatLng)
                )
        )
    }

    override fun onPause() {
        super.onPause()
        walkNavigateHelper.pause()
    }

    override fun onResume() {
        super.onResume()
        walkNavigateHelper.resume()

    }

    override fun onDestroy() {
        super.onDestroy()
        walkNavigateHelper.quit()
        search.destroy()

    }

    /**
     * 初始化语音播报
     */
    private fun initSpeech() {
        mSpeechSynthesizer = SpeechSynthesizer.getInstance()
        val auth = Auth.getInstance(this)
        mSpeechSynthesizer?.apply {
            setApiKey(auth.appKey, auth.secretKey)
            setAppId(auth.appId)
            setContext(this@BdNavGuideActivity)
            setParam(SpeechSynthesizer.PARAM_SPEAKER, "0")
            initTts(TtsMode.ONLINE)
        }
        walkNavigateHelper.setTTsPlayer { s, _ ->
            mSpeechSynthesizer!!.speak(s)
            return@setTTsPlayer 0
        }
    }


    /**
     * 导航状态
     */
    private fun navStateListener() {
        walkNavigateHelper.setWalkNaviStatusListener(object : IWNaviStatusListener {
            override fun onWalkNaviModeChange(mode: Int, p1: WalkNaviModeSwitchListener?) {
                walkNavigateHelper.switchWalkNaviMode(this@BdNavGuideActivity, mode, p1)
            }

            override fun onNaviExit() {
            }
        })
        walkNavigateHelper.setRouteGuidanceListener(
            this,
            object : SimpleIWRouteGuidanceListener() {})
    }


    /**
     * 处理相机权限
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == ArCameraView.WALK_AR_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                showToast("没有相机权限,请打开后重试")
            } else if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                walkNavigateHelper.startCameraAndSetMapView(this)
            }
        }
    }
}