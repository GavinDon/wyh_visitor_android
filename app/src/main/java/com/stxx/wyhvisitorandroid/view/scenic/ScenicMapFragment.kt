package com.stxx.wyhvisitorandroid.view.scenic

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentFilter
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.baidu.geofence.GeoFence
import com.baidu.geofence.GeoFenceClient
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.baidu.mapapi.map.*
import com.baidu.mapapi.model.LatLng
import com.baidu.mapapi.model.LatLngBounds
import com.baidu.mapapi.utils.CoordinateConverter
import com.baidu.mapapi.walknavi.WalkNavigateHelper
import com.baidu.mapapi.walknavi.adapter.IWEngineInitListener
import com.baidu.mapapi.walknavi.params.WalkNaviLaunchParam
import com.baidu.mapapi.walknavi.params.WalkRouteNodeInfo
import com.gavindon.mvvm_lib.base.MVVMBaseApplication
import com.gavindon.mvvm_lib.net.SuccessSource
import com.gavindon.mvvm_lib.net.http
import com.gavindon.mvvm_lib.utils.NotificationUtil
import com.gavindon.mvvm_lib.utils.getStatusBarHeight
import com.gavindon.mvvm_lib.utils.phoneWidth
import com.gavindon.mvvm_lib.widgets.showToast
import com.google.android.material.tabs.TabLayout
import com.gyf.immersionbar.ImmersionBar
import com.huawei.hms.hmsscankit.ScanUtil
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions
import com.orhanobut.logger.Logger
import com.stxx.wyhvisitorandroid.*
import com.stxx.wyhvisitorandroid.adapter.ScenicMapServerPointAdapter
import com.stxx.wyhvisitorandroid.base.BaseFragment
import com.stxx.wyhvisitorandroid.bean.ServerPointResp
import com.stxx.wyhvisitorandroid.enums.ScenicMApPointEnum
import com.stxx.wyhvisitorandroid.location.BdLocation
import com.stxx.wyhvisitorandroid.location.GeoBroadCast
import com.stxx.wyhvisitorandroid.location.showWakeApp
import com.stxx.wyhvisitorandroid.mplusvm.ScenicVm
import com.stxx.wyhvisitorandroid.service.PlaySoundService
import com.stxx.wyhvisitorandroid.widgets.BottomSheetBehavior3
import kotlinx.android.synthetic.main.fragment_scenic.*
import kotlinx.android.synthetic.main.title_bar.*
import org.jetbrains.anko.support.v4.dip
import org.jetbrains.anko.support.v4.toast
import java.io.InputStream
import java.nio.ByteBuffer


/**
 * description:电子地图
 * Created by liNan on 2020/1/14 15:53

 */
class ScenicMapFragment : BaseFragment(), TabLayout.OnTabSelectedListener,
    BaiduMap.OnMapLoadedCallback {


    private val tabsText: Array<String> by lazy { resources.getStringArray(R.array.scenic_map) }

    override val layoutId: Int = R.layout.fragment_scenic

    private val mViewModel: ScenicVm by lazy { getViewModel<ScenicVm>() }

    private lateinit var walkNaviLaunchParam: WalkNaviLaunchParam

    //跳转过来时应该选中的索引
    private var defaultSelectTab = 0

    //上一次选中tab的索引
    private var lastSelectTab: Int? = 0
    private val toiletTabIndex = 5
    private val parkTabIndex = 6

    //第一次加载进来默认隐藏bottomSheet,再次选中tab时需要显示bottomSheet
    private var isFirstLoad = true

    private var tileUrl = "http://223.221.37.181:8082/tiles/"

    private var bottomSheetBehavior: BottomSheetBehavior3<NestedScrollView>? = null
    private var mapLoaded = false

    //当前经纬度
    private var currentLatitude: Double? = null
    private var currentLongitude: Double? = null

    //上一次请求的type类型
    private var lastType = -10
    private val mGeoFenceClient = GeoFenceClient(MVVMBaseApplication.appContext)

    private val geoBroadCast by lazy { GeoBroadCast() }

    private val serverPointAdapter: ScenicMapServerPointAdapter by lazy {
        ScenicMapServerPointAdapter(R.layout.adapter_server_point, null)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //创建围栏广播
        val filter = IntentFilter()
        filter.addAction(GeoBroadCast.fenceaction)
        this.context?.registerReceiver(geoBroadCast, filter)
    }

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        lifecycle.addObserver(mViewModel)
        val saveState = savedInstanceState?.getInt(BUNDLE_SELECT_TAB)
        //是否从机器人页面跳转过来
        val isRobot = arguments?.getBoolean(BUNDLE_IS_ROBOT) ?: false
        if (isRobot) {
            defaultSelectTab = arguments?.getInt(BUNDLE_SELECT_TAB, 0) ?: 0
            lastSelectTab = defaultSelectTab
        } else {
            //恢复选中的tab
            when {
                saveState != null -> {
                    lastSelectTab = saveState
                }
                SaveMapObj.needSaveState?.first != null -> {
                    lastSelectTab = SaveMapObj.needSaveState?.first
                }
                else -> {
                    defaultSelectTab = arguments?.getInt(BUNDLE_SELECT_TAB, 0) ?: 0
                    lastSelectTab = defaultSelectTab
                }
            }

        }
        if (SaveMapObj.needSaveState?.second != null) {
            isFirstLoad = SaveMapObj.needSaveState?.second!!
        }
        tabsText.forEach {
            val tab = tabLayout.newTab()
            val cView = layoutInflater.inflate(R.layout.tablayout_scenic_tab, null, false)
            cView.findViewById<TextView>(R.id.tvTabText).text = it
            tab.customView = cView
            tabLayout.addTab(tab)
        }
        tabLayout.addOnTabSelectedListener(this)
        //移动到选中的tab并居中
        tabLayout.postDelayed({
            tabLayout.selectTab(tabLayout.getTabAt(lastSelectTab!!))
        }, 100)
        //服务点适配器初始化
        rvServerPoint.adapter = serverPointAdapter
        //点击服务点列表
        serverPointAdapter.setOnItemClickListener { adapter, _, position ->
            val data = adapter.data[position] as ServerPointResp
            val latLng = LatLng(
                data.y?.toDouble() ?: 0.toDouble(),
                data.x?.toDouble() ?: 0.toDouble()
            )
            //显示infoWindow
            showInfoWindow(latLng, data)
            //点击服务点时称动到地图中心并隐藏bottomSheet
            fixedLocation2Center(latLng)
            hiddenSheet()
        }
        //infoWindow上导航按钮点击事件
        serverPointAdapter.onItemClick { resp: ServerPointResp ->
            //初始化导航引擎
//            initEngine(resp.lngLat, 1)
            goWalkNav(resp)
        }

        ibMessage.setOnClickListener {
            it.findNavController().navigate(R.id.fragment_push_message, null, navOption)
        }
        ibScanQr.setOnClickListener {
//            startActivity<ScanKitActivity>()
            ScanUtil.startScan(
                this.activity,
                SCAN_CODE,
                HmsScanAnalyzerOptions.Creator().create()
            )
        }
        //搜索
        llSearchDestination.setOnClickListener {
            findNavController().navigate(R.id.fragment_search, null, navOptions {
                anim {
                    enter = R.anim.alpha_enter
                    exit = R.anim.alpha_exit
                }
            })
        }
        initMap()
        initLocation()
        //每次都需要重新创建否则从返回到该页面时添加的callback无效
        bottomSheetBehavior = BottomSheetBehavior3.from(scrollBottom)
    }

    /**
     * @param lngLat 导航途经点以及起终点经纬度
     * @param navMode 0 普通导航 1 AR导航
     * 加载百度导航引擎
     */
    private fun initEngine(lngLat: List<String>, navMode: Int = 0) {

        //如果没有线路点位不进行导航跳转
        if (lngLat.isNullOrEmpty()) {
            this.context?.showToast("点位正在采集中,暂不能导航")
            return
        }
        val context = this.activity
        if (context != null) {
            WalkNavigateHelper.getInstance().initNaviEngine(
                context,
                object : IWEngineInitListener {
                    override fun engineInitSuccess() {
                        initHelper(lngLat, navMode)
                    }

                    override fun engineInitFail() {
                        toast("初始化引擎失败")
                        WalkNavigateHelper.getInstance().unInitNaviEngine()
                    }
                })
        }
    }

    /**
     *
     * 算路以及算路的参数处理
     * 算路完成进行导航
     */
    private fun initHelper(lngLat: List<String>, navMode: Int) {
        val points = mutableListOf<WalkRouteNodeInfo>()
        walkNaviLaunchParam = WalkNaviLaunchParam()

        try {
            for (i in lngLat) {
                val ll = i.split(",")
                val walkInfo = WalkRouteNodeInfo()
                if (ll.size == 2) {
                    walkInfo.location = LatLng(ll[0].toDouble(), ll[1].toDouble())
                    points.add(walkInfo)
                }

            }
            this.findNavController().navigate(
                R.id.fragment_route_plant,
                bundleOf(
                    "start" to points[0].location,
                    "end" to points[lngLat.size - 1].location
                )
            )
            walkNaviLaunchParam.apply {
                startNodeInfo(points[0])
                endNodeInfo(points[points.size - 1])
                viaNodes = points.toList()
                extraNaviMode(navMode)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }


        /*       *//*算路成功之后跳转到导航页面*//*
        WalkNavigateHelper.getInstance()
            .routePlanWithRouteNode(walkNaviLaunchParam, object : IWRoutePlanListener {
                override fun onRoutePlanStart() {
                }

                override fun onRoutePlanSuccess() {
                    val intent = Intent(
                        this@ScenicMapFragment.requireActivity(),
                        WNaviGuideActivity::class.java
                    )
                    startActivity(intent)
                }

                override fun onRoutePlanFail(p0: WalkRoutePlanError?) {
                }
            })

        WalkNavigateHelper.getInstance().setWalkNaviStatusListener(object : IWNaviStatusListener {
            override fun onWalkNaviModeChange(p0: Int, p1: WalkNaviModeSwitchListener?) {
                WalkNavigateHelper.getInstance()
                    .switchWalkNaviMode(this@ScenicMapFragment.requireActivity(), p0, p1)
            }

            override fun onNaviExit() {
            }
        })*/
    }

    override fun setStatusBar() {
        titleBar?.layoutParams?.height = getStatusBarHeight(requireContext())
        ImmersionBar.with(this)
            .fitsSystemWindows(false)
            .statusBarDarkFont(true)
            .init()
    }

    /**
     * 点击景点时上传后台历史纪录
     */
    private fun findById(id: Int) {
        when (lastSelectTab) {
            0, 1, 2, 3, 4 -> {
                http?.getWithoutLoading(ApiService.SCENIC_MAP_POINT_ID, listOf("id" to id))
                    ?.subscribe({ Logger.i(it) }, {})
            }
            5 -> {
                //厕所
                http?.getWithoutLoading(ApiService.TOILET_LST_URL_ID, listOf("id" to id))
                    ?.subscribe({ Logger.i(it) }, {})

            }
            6 -> {
                //停车场
                http?.getWithoutLoading(ApiService.PARK_LST_URL_ID, listOf("id" to id))
                    ?.subscribe({ Logger.i(it) }, {})
            }
        }
    }

    private fun initLocation() {
        val locationListener = object : BDAbstractLocationListener() {
            override fun onReceiveLocation(location: BDLocation) {
                if (location.locType != BDLocation.TypeServerError &&
                    location.locType != BDLocation.TypeOffLineLocationFail &&
                    location.locType != BDLocation.TypeCriteriaException
                ) {
                    //先移除围栏再添加围栏可使再次调用广播进行回调位置信息
                    mGeoFenceClient.removeGeoFence()
                    mGeoFenceClient.addGeoFence("沙子营湿地公园", "旅游景点", "北京", 1, " 0001")
                    //防止定位回调时 View已经注销
                    val map = mapView?.map
                    currentLatitude = location.latitude //获取纬度信息
                    currentLongitude = location.longitude //获取经度信息
                    //设置显示在地图上的定位数据
                    val locationData = MyLocationData.Builder()
                        .direction(location.direction)
                        .accuracy(location.radius)
                        .latitude(location.latitude)
                        .longitude(location.longitude)
                        .build()
                    map?.setMyLocationData(locationData)
                }
            }
        }
        //开始定位
        requestPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) {
            val location = BdLocation(locationListener)
            lifecycle.addObserver(location)
            mapView.map?.isMyLocationEnabled = true
            location.startLocation()
        }
        //初始化围栏(在位置回调中先进行移除再添加达到每隔2s回调一次)
        mGeoFenceClient.createPendingIntent(GeoBroadCast.fenceaction)
        mGeoFenceClient.setActivateAction(GeoFenceClient.GEOFENCE_IN)
        /*//当监测到用户已经离开园区时定位到瓦片图中心。
        geoBroadCast.setOnReceiveLocation {
            if (geoBroadCast.status == GeoFence.INIT_STATUS_OUT || geoBroadCast.status == GeoFence.STATUS_OUT) {
                fixedLocation2Center()
            }
        }*/
        showLocationInMap()
    }

    /**
     * 在地图上显示当前位置
     */
    private fun showLocationInMap() {
        val locationConfig = MyLocationConfiguration(
            MyLocationConfiguration.LocationMode.NORMAL,
            true, null
        )
        //设置当前用户所在位置
        mapView?.map?.setMyLocationConfiguration(locationConfig)
    }


    private fun initMap() {
        mapView.showZoomControls(false)
        val map = mapView.map
        map.setOnMapLoadedCallback(this)
        map.setMaxAndMinZoomLevel(17f, 15f)
        val builder = MapStatus.Builder()
        builder.zoom(SaveMapObj.mapZoom)
        builder.target(SaveMapObj.target)
        val mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(builder.build())
        map.setMapStatus(mMapStatusUpdate)
        map.setOnMapClickListener(object : BaiduMap.OnMapClickListener {
            override fun onMapClick(p0: LatLng?) {
                hiddenSheet()
                mapView.map.hideInfoWindow()
            }

            override fun onMapPoiClick(p0: MapPoi?) {
                Log.i("customLocation", p0.toString())
            }
        })
        val tilePair = object : FileTileProvider() {
            override fun getMinDisLevel(): Int = 14

            override fun getMaxDisLevel(): Int = 18

            override fun getTile(x: Int, y: Int, z: Int): Tile? {

                val fileDir = "tiles/${z}/tile${x}_${y}.png"
                val bm = getFromAssets(fileDir) ?: return null
                val tile = Tile(bm.width, bm.height, toRawData(bm))
                bm.recycle()
                return tile
            }
        }

        val urlTileProvider = object : UrlTileProvider() {
            override fun getMinDisLevel(): Int = 14
            override fun getMaxDisLevel(): Int = 19

            // "http://online1.map.bdimg.com/tile/?qt=vtile&x={x}&y={y}&z={z}&styles=pl&scaler"
            //            + "=1&udt=20190528";
            //http://223.221.37.181:8082/tiles/15/tile6330_2367.png
            override fun getTileUrl(): String {
                //  val fileDir = "tiles/${z}/tile${x}_${y}.png"
                /*    "http://online1.map.bdimg.com/tile/?qt=vtile&x={x}&y={y}&z={z}&styles=pl&scaler" + "=1&udt=20190528";
                    "http://223.221.37.181:8082/tiles?z={z}&x={x}_y={y}.png"*/
                return "http://223.221.37.181:8082/tiles?z={z}&tile\"x={x}\"_\"&y={y}\".png"

            }

        }

        val options = TileOverlayOptions().apply {
            //            40.06048593512643, longitude: 116.39524272759365
//            40.09364835966737, 116.4920968191294
            //40.071822098761984, 116.46385409389569
            //40.09526869141341, longitude: 116.52661420314884
            val northEast = LatLng(40.06048593512643, 116.39524272759365)
            val southEast = LatLng(40.071822098761984, 116.46385409389569)
            tileProvider(tilePair)
//            setPositionFromBounds(LatLngBounds.Builder().include(northEast).include(southEast).build())
        }
        map.addTileLayer(options)
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
        //在息屏时后再次打开时需要重新添加bottomCallback否则上一个callback无效
        bottomSheetBehavior?.addBottomSheetCallback(bottomCallBack)
        //如果从机器人页面寻问的是子项目,让它处于显示状态
        if (arguments?.getBoolean(BUNDLE_IS_SUB_SCENIC) == true) {
            showDownArrow()
            bottomSheetBehavior?.state = BottomSheetBehavior3.STATE_EXPANDED
        } else {
            hiddenSheet()
            showUpArrow()
        }
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {
        tabSelect(tab)
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        tabSelect(tab)
    }

    private fun tabSelect(tab: TabLayout.Tab?) {
        fun executeSelect() {
            when (val index = tab?.position ?: 0) {
                0, 1, 2, 3 -> {
                    //type 从1开始所以把index加1
                    loadData(
                        ScenicMApPointEnum.values()[index].ordinal + 1,
                        ApiService.SCENIC_MAP_POINT
                    )
                }
                4 -> {
                    //服务区
                    loadData(
                        ScenicMApPointEnum.values()[index].ordinal + 2,
                        ApiService.SCENIC_MAP_POINT
                    )
                }
                5 -> {
                    //厕所 不用传type
                    loadData(0, ApiService.TOILET_LST_URL)
                }
                6 -> {
                    //停车场
                    loadData(-1, ApiService.PARK_LST_URL)
                }
            }
        }
        executeSelect()
        lastSelectTab = tab?.position
    }

    private fun loadData(type: Int, url: String) {
        //设置是否是停车场数据
        serverPointAdapter.isPark = type == -1
        //是否是厕所
        serverPointAdapter.isToilet = type == 0
        val value = mViewModel.pointLiveData.value
        if (type == lastType && value != null && value is SuccessSource) {
            serverPointAdapter.setList(value.body.data.toMutableList())
            createMarket(value.body.data)
        } else {
            mViewModel.getServicePoint(type, url)
                .observe(this, Observer {
                    handlerResponseData(it, { resp ->
                        serverPointAdapter.setList(resp.data.toMutableList())
                        createMarket(resp.data)
                    }, {
                        this.context?.showToast("暂无数据")
                    })
                })
        }
        //保存当前请求的type
        lastType = type
    }


    /**
     * 控制bottomSheet显示高度
     */

    private val bottomCallBack = object : BottomSheetBehavior3.BottomSheetCallback() {
        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            rvServerPoint.smoothScrollToPosition(0)
            //-1 to 0 it is between hidden and collapsed states.
            //slideOffset为0时表示处于折叠状态 -1 表示hidden时 1表示完全展开
            if (slideOffset == 0f) {
                ivScenicShowArrow?.run {
                    frame = 0
                    cancelAnimation()
                    setAnimation(R.raw.bounce_line)
                }

            } else if (slideOffset in 0..1) {
                //上箭头转换成往下的箭头
                ivScenicShowArrow?.run {
                    rotation = -180f * slideOffset
                    setAnimation(R.raw.up_arrow)
                    repeatCount = ValueAnimator.INFINITE
                    playAnimation()
                    scrollBottom.setBackgroundColor(Color.WHITE)
                }
            } else if (slideOffset in -1..0) {
                //箭头转一圈继续保持往上的状态
                ivScenicShowArrow?.run {
                    rotation = -360f * slideOffset
                    setAnimation(R.raw.up_arrow)
                    repeatCount = ValueAnimator.INFINITE
                    playAnimation()
                    //当为折叠的动作时底层颜色设置为白色
                    scrollBottom.setBackgroundColor(Color.WHITE)
                }
            }
        }

        //当用手势拖拉时
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior3.STATE_EXPANDED || newState == BottomSheetBehavior3.STATE_COLLAPSED) {
                scrollBottom.setBackgroundColor(Color.WHITE)
            } else if (newState == BottomSheetBehavior3.STATE_HIDDEN) {
                scrollBottom.setBackgroundColor(Color.TRANSPARENT)
            }
        }
    }

    //点击空白处隐藏bottomSheet或者自动折叠时改变状态
    private fun hiddenSheet() {
        bottomSheetBehavior?.state = BottomSheetBehavior3.STATE_HIDDEN
        //防止隐藏时不能够显示箭头
        rvServerPoint.smoothScrollToPosition(0)
    }

    /**
     * 显示上拉箭头
     */
    private fun showUpArrow() {
//        bottomSheetBehavior?.state = BottomSheetBehavior3.STATE_COLLAPSED
        //从上层fragment返回时没有手势滑动不执行onSlide
        ivScenicShowArrow?.run {
            frame = 0
            cancelAnimation()
            setAnimation(R.raw.up_arrow)
            repeatCount = ValueAnimator.INFINITE
            playAnimation()
        }
        scrollBottom.setBackgroundColor(Color.TRANSPARENT)
    }

    /**
     * 显示下拉箭头
     */
    private fun showDownArrow() {
        ivScenicShowArrow?.run {
            setAnimation(R.raw.up_arrow)
            repeatCount = ValueAnimator.INFINITE
            playAnimation()
            rotation = 180f
        }
    }

    /**
     * 创建景点的marker
     */
    private fun createMarket(data: List<ServerPointResp>) {

        var bitmap: BitmapDescriptor? = getMarkerBitmap(data[0])
        val xy = data.map { Pair(it.x, it.y) }
        //所有的marker点位
        val markerOptions = mutableListOf<OverlayOptions>()
        for (item in xy.indices) {
            //经度
            val lng = xy[item].first?.toDouble() ?: 0.toDouble()
            //经度
            val lat = xy[item].second?.toDouble() ?: 0.toDouble()
            if (lastSelectTab == ScenicMApPointEnum.TOILET.ordinal || lastSelectTab == ScenicMApPointEnum.PARK.ordinal) {
                bitmap = getMarkerBitmap(data[item])
            }
            val converter = CoordinateConverter()
                .from(CoordinateConverter.CoordType.GPS)
                .coord(LatLng(lat, lng))
            val options = with(MarkerOptions()) {
                position(converter.convert())
                icon(bitmap)
                zIndex(17)
                animateType(MarkerOptions.MarkerAnimateType.grow)
                extraInfo(bundleOf("marketExtra" to data[item]))
            }
            markerOptions.add(options)

            markerOptions.add(options)

        }
        mapView.map.clear()
        //给map添加创建好的marker
        mapView.map.addOverlays(markerOptions)
        //marker点击事件
        mapView.map.setOnMarkerClickListener {
            //右下角定位到前位置
            val locationMarker = it.extraInfo.getString("locationMarker")
            if (locationMarker != null && currentLatitude != null && currentLongitude != null) {
                fixedLocation2Center(LatLng(currentLatitude!!, currentLongitude!!))
            } else {
                val pointData = it.extraInfo.getSerializable("marketExtra") as ServerPointResp
                showInfoWindow(it.position, pointData)
            }
            return@setOnMarkerClickListener true
        }
    }

    /**
     * 提供需要在地图上打点的bitmap
     */
    @SuppressLint("SetTextI18n")
    private fun getMarkerBitmap(data: ServerPointResp): BitmapDescriptor? {
        when (lastSelectTab) {
            ScenicMApPointEnum.MAIN_SCENIC.ordinal -> {
                return BitmapDescriptorFactory.fromResource(R.mipmap.marker_scenic_point)
            }
            ScenicMApPointEnum.FEATURES_AREA.ordinal -> {
                //特色展区
                return BitmapDescriptorFactory.fromResource(R.mipmap.marker_featuter_area)
            }
            ScenicMApPointEnum.SCENIC_PLANT.ordinal -> {
                return BitmapDescriptorFactory.fromResource(R.mipmap.marker_plant)
            }
            ScenicMApPointEnum.SHOP.ordinal -> {
                return BitmapDescriptorFactory.fromResource(R.mipmap.marker_shop)
            }
            ScenicMApPointEnum.SERVICE_AREA.ordinal -> {
                return BitmapDescriptorFactory.fromResource(R.mipmap.marker_server_area)
            }
            ScenicMApPointEnum.TOILET.ordinal -> {
                val markerView = layoutInflater.inflate(R.layout.custom_marker, null, false)
                val iv = markerView.findViewById<ImageView>(R.id.customMarkerIv)
                val tv = markerView.findViewById<TextView>(R.id.customMarkerParkTv)
                iv.setBackgroundResource(R.mipmap.marker_toilet)

                var total = 0
                var occupation = 0
                if (data.woMenInfo != null) {
                    total += data.woMenInfo.sum
                    occupation += data.woMenInfo.occupation
                }
                if (data.manInfo != null) {
                    total += data.manInfo.sum
                    occupation += data.manInfo.occupation
                }
                if (data.thirdInfo != null) {
                    total += data.thirdInfo.sum
                    occupation += data.thirdInfo.occupation
                }
                tv.text = "${total - occupation}/$total"
                return BitmapDescriptorFactory.fromView(markerView)

            }
            ScenicMApPointEnum.PARK.ordinal -> {
                val markerView = layoutInflater.inflate(R.layout.custom_marker, null, false)
                val iv = markerView.findViewById<ImageView>(R.id.customMarkerIv)
                val tv = markerView.findViewById<TextView>(R.id.customMarkerParkTv)
                iv.setBackgroundResource(R.mipmap.marker_park)
                tv.text = "${data.residue}/${data.sum}"
                return BitmapDescriptorFactory.fromView(markerView)
            }
            else -> {
                return BitmapDescriptorFactory.fromResource(R.mipmap.ic_location_market)
            }
        }
    }

    /**
     * 弹出marker的infoWindow
     */
    private fun showInfoWindow(
        latLng: LatLng,
        pointData: ServerPointResp
    ) {
        fixedLocation2Center(latLng)
        if (lastSelectTab!! >= toiletTabIndex) {
            showInfoWindowToiletAndPark(pointData, latLng)
            return
        }
        val dialog = layoutInflater.inflate(R.layout.scenic_map_bubble_dialog, null, false)
        val blName = dialog.findViewById<TextView>(R.id.tvBubbleName)
        val blPre = dialog.findViewById<TextView>(R.id.tvBubblePre)
        val blContent = dialog.findViewById<TextView>(R.id.tvBubbleContent)
        val blNav = dialog.findViewById<ImageView>(R.id.ivBubbleNav)
        val blVoice = dialog.findViewById<ImageView>(R.id.ivBubbleVoice)
        blName.text = pointData.name
        blPre.text = pointData.position
        blContent.text = pointData.introduction
        //设置dialog的layoutParam无效。设置内容的宽度撑大父布局为屏幕宽度
        blContent.layoutParams.width = phoneWidth - dip(80)

        val explain = pointData.explain
        blVoice.visibility =
            if (!explain.isNullOrEmpty() && !explain.startsWith("http")) View.GONE else View.VISIBLE
        //设置宽度 设置match 地图会
        val infoWindow = InfoWindow(dialog, latLng, -96)
        mapView.map.showInfoWindow(infoWindow)

        dialog.setOnClickListener {
            loadMarkerScenicDetail(pointData)
        }
        blNav.setOnClickListener {
            goWalkNav(pointData)
        }
        blVoice.setOnClickListener {
            playVoice(pointData)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showInfoWindowToiletAndPark(pointData: ServerPointResp, latLng: LatLng) {

        val toiletParkDialog = layoutInflater.inflate(R.layout.scenic_map_park_dialog, null, false)

        val windowName = toiletParkDialog.findViewById<TextView>(R.id.infoWindowTvName)
        val rule = toiletParkDialog.findViewById<TextView>(R.id.infoWindowTvRule)
        val total = toiletParkDialog.findViewById<TextView>(R.id.infoWindowTvTotal)
        val totalNum = toiletParkDialog.findViewById<TextView>(R.id.infoWindowTVTotalNum)
        val retail = toiletParkDialog.findViewById<TextView>(R.id.infoWindowTvRetail)
        val retailNum = toiletParkDialog.findViewById<TextView>(R.id.infoWindowTvRetailNum)
        val blNav = toiletParkDialog.findViewById<ImageView>(R.id.infoWindowIvBubbleNav)
        //停车场预约按钮
        val parkSubscriber = toiletParkDialog.findViewById<ImageView>(R.id.ivBubbleSubscriber)

        //卫生间
        val toiletRoot = toiletParkDialog.findViewById<LinearLayout>(R.id.llToilet)
        val parkRoot = toiletParkDialog.findViewById<ConstraintLayout>(R.id.cslPark)

        windowName.text = pointData.name
        rule.text = pointData.details
        //停车场与厕所
        if (lastSelectTab == parkTabIndex) {
            toiletRoot.visibility = View.GONE
            parkRoot.visibility = View.VISIBLE

            total.text = "共有车位:"
            retail.text = "剩余车位:"
            rule.text = "收费规则:${pointData.parkingFee}元/小时"
            retailNum.text = pointData.residue
            parkSubscriber.visibility = View.VISIBLE
            parkSubscriber.setOnClickListener {
                val token = judgeLogin()
                if (token.isNotEmpty()) {
                    findNavController().navigate(
                        R.id.fragment_webview,
                        bundleOf(
                            WEB_VIEW_URL to "${WebViewUrl.SMART_PARK}?parkId=${pointData.id}&token=${token}",
                            WEB_VIEW_TITLE to R.string.grid_smart_book
                        ),
                        navOption
                    )
                } else {
                    toast("请先登录")
                    findNavController().navigate(R.id.login_activity, null, navOption)
                }
            }

        } else if (lastSelectTab == toiletTabIndex) {
            toiletRoot.visibility = View.VISIBLE
            parkRoot.visibility = View.GONE
            //item
            val cslMan = toiletParkDialog.findViewById<ConstraintLayout>(R.id.cslMan)
            val cslWoman = toiletParkDialog.findViewById<ConstraintLayout>(R.id.cslWoman)
            val cslBarrierFree =
                toiletParkDialog.findViewById<ConstraintLayout>(R.id.cslBarrierFree)
            //男卫生间
            val manTotal = toiletParkDialog.findViewById<TextView>(R.id.infoWindowTotalToilet)
            val manRetailNumber =
                toiletParkDialog.findViewById<TextView>(R.id.infoWindowRetailToilet)
            //女卫生间
            val womanTotalNumber =
                toiletParkDialog.findViewById<TextView>(R.id.infoWindowWomanTotalToilet)
            val womanRetailNumber =
                toiletParkDialog.findViewById<TextView>(R.id.infoWindowWomanRetailToilet)
            //无障碍
            val brTotalNumber =
                toiletParkDialog.findViewById<TextView>(R.id.infoWindowBrTotalToilet)
            val brRetailNumber =
                toiletParkDialog.findViewById<TextView>(R.id.infoWindowBrRetailToilet)


            if (pointData.manInfo != null) {
                manRetailNumber.text =
                    "剩余:${pointData.manInfo.sum - pointData.manInfo.occupation}/${pointData.manInfo.sum}"
            } else {
                cslMan.visibility = View.GONE
            }
            if (pointData.woMenInfo != null) {
                cslWoman.visibility = View.VISIBLE
                womanRetailNumber.text =
                    "剩余:${pointData.woMenInfo.sum - pointData.woMenInfo.occupation}/${pointData.woMenInfo.sum}"
            } else {
                cslWoman.visibility = View.GONE
            }
            if (pointData.thirdInfo != null) {
                brRetailNumber.text =
                    "剩余:${pointData.thirdInfo.sum - pointData.thirdInfo.occupation}/${pointData.thirdInfo.sum}"
            } else {
                cslBarrierFree.visibility = View.GONE
            }
        }
        totalNum.text = "${pointData.sum?.toInt() ?: 0}"

        //设置dialog的layoutParam无效。设置内容的宽度撑大父布局为屏幕宽度
        rule.layoutParams.width = phoneWidth - dip(80)
        //设置宽度 设置match 地图会
        val infoWindow = InfoWindow(toiletParkDialog, latLng, -96)
        mapView.map.showInfoWindow(infoWindow)
        blNav.setOnClickListener {
            goWalkNav(pointData)
        }
    }


    private fun goWalkNav(pointData: ServerPointResp) {
        if (geoBroadCast.status == GeoFence.STATUS_IN || geoBroadCast.status == GeoFence.INIT_STATUS_IN) {
            //进入园区则使用园区导航
            findNavController().navigate(
                R.id.fragment_ar_nav,
                bundleOf(Pair(BUNDLE_SCENIC_DETAIL, pointData)),
                navOption
            )
        } else {
            //未进入园区
            if (this.context != null && currentLongitude != null) {
                showWakeApp(this.requireContext(), currentLatitude!!, currentLongitude!!)
            } else {
                this.context?.showToast("无法获取当前位置,暂不能导航")
            }
        }
    }

    private fun playVoice(pointData: ServerPointResp) {

        if (!NotificationUtil.hasNotifyEnable()) {
            this.context?.showToast("使用该功能需要打开通知权限!")
            NotificationUtil.settingNotify()
        } else {

            Intent(this.context, PlaySoundService::class.java).also {
                it.putExtra(PlaySoundService.SOUND_SOURCE, pointData)
                it.putExtra(
                    PlaySoundService.ALL_SOUND,
                    (serverPointAdapter.data).toTypedArray()
                )
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    this.context?.startForegroundService(it)
                } else {
                    this.context?.startService(it)
                }
            }
        }
    }

    /**
     * marker点击之后跳转景点详情
     */
    private fun loadMarkerScenicDetail(pointData: ServerPointResp) {
        findById(pointData.id)
        findNavController().navigate(
            R.id.fragment_scenic_comment,
            bundleOf(BUNDLE_SCENIC_DETAIL to pointData),
            navOption
        )
    }

    /**
     * 显示在中间位置
     */
    private fun fixedLocation2Center(latLng: LatLng = LatLng(40.082681, 116.474134)) {
        val mapStatus = MapStatus.Builder()
            .target(latLng)
            .build()
        val mapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mapStatus)
        mapView.map.setMapStatus(mapStatusUpdate)
    }


    override fun onMapLoaded() {
        mapLoaded = true
        ivMoveToCenterLocation.visibility = View.VISIBLE
        //移动到定位中心
        try {
            ivMoveToCenterLocation?.setOnClickListener {
                if (geoBroadCast.status == GeoFence.INIT_STATUS_IN || geoBroadCast.status == GeoFence.STATUS_IN) {
                    val latLng =
                        LatLng(currentLatitude ?: 0.toDouble(), currentLongitude ?: 0.toDouble())
                    fixedLocation2Center(latLng)

                } else if (geoBroadCast.status == GeoFence.INIT_STATUS_OUT || geoBroadCast.status == GeoFence.STATUS_OUT) {
                    this.context?.showToast("您当前未在园区!")
                } else {
                    this.context?.showToast("请定位成功之后重试!")
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        //移动到地图中心
        ivMoveToScenicCenterLocation?.setOnClickListener {
            fixedLocation2Center(LatLng(40.082681, 116.474134))
        }
        //限制地图只显示在某一区域
        val builders = LatLngBounds.Builder()
        builders.include(LatLng(40.09364835966737, 116.4920968191294))
            .include(LatLng(40.071822098761984, 116.46385409389569))
//        mapView.map.setMapStatusLimits(builders.build())
    }

    override fun onPause() {
        super.onPause()
        SaveMapObj.mapZoom = mapView.map.mapStatus.zoom
        SaveMapObj.target = mapView.map.mapStatus.target
        bottomSheetBehavior?.removeBottomSheetCallback(bottomCallBack)
        mapView.onPause()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        hiddenSheet()
        mGeoFenceClient.removeGeoFence()
        //跳转页面时 第二个参数true表示 hidden
        SaveMapObj.needSaveState =
            Pair(lastSelectTab!!, bottomSheetBehavior?.state == BottomSheetBehavior3.STATE_HIDDEN)
        mapView.onDestroy()
    }

    override fun onDestroy() {
        super.onDestroy()
        this.context?.unregisterReceiver(geoBroadCast)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(BUNDLE_SELECT_TAB, lastSelectTab!!)
    }

    private fun toRawData(bitmap: Bitmap): ByteArray? {
        val buffer: ByteBuffer = ByteBuffer.allocate(
            bitmap.width
                    * bitmap.height * 4
        )
        bitmap.copyPixelsToBuffer(buffer)
        val data: ByteArray = buffer.array()
        buffer.clear()
        return data
    }

    private fun getFromAssets(fileName: String): Bitmap? {
        val am: AssetManager? = this.activity?.assets
        val `is`: InputStream?
        val bm: Bitmap
        return try {
            `is` = am?.open(fileName)
            bm = BitmapFactory.decodeStream(`is`)
            bm
        } catch (e: Exception) {
//            e.printStackTrace()
            null
        }
    }

}

object SaveMapObj {
    //second  true表示bottomSheet hidden false表示show
    var needSaveState: Pair<Int, Boolean>? = null
    var bottomState: Int = -1

    //onDestroyView时地图的绽放级别
    var mapZoom = 17f

    //onDestroyView时保存地图的中心点
    var target: LatLng = LatLng(40.082681, 116.474134)
    var geoBroadCastStatus: Int? = null
}

