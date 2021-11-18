package com.stxx.wyhvisitorandroid.view.scenic

import android.Manifest
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Build
import android.os.Bundle
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
import com.baidu.mapapi.map.*
import com.baidu.mapapi.model.LatLng
import com.baidu.mapapi.model.LatLngBounds
import com.baidu.mapapi.utils.route.BaiduMapRoutePlan
import com.baidu.mapapi.walknavi.params.WalkNaviLaunchParam
import com.gavindon.mvvm_lib.base.MVVMBaseApplication
import com.gavindon.mvvm_lib.net.SuccessSource
import com.gavindon.mvvm_lib.net.http
import com.gavindon.mvvm_lib.utils.GsonUtil
import com.gavindon.mvvm_lib.utils.NotificationUtil
import com.gavindon.mvvm_lib.utils.getStatusBarHeight
import com.gavindon.mvvm_lib.utils.phoneWidth
import com.gavindon.mvvm_lib.widgets.showToast
import com.google.android.material.tabs.TabLayout
import com.google.gson.reflect.TypeToken
import com.gyf.immersionbar.ImmersionBar
import com.huawei.hms.hmsscankit.ScanUtil
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions
import com.orhanobut.logger.Logger
import com.quyuanfactory.artmap.ArtMapMark
import com.stxx.wyhvisitorandroid.*
import com.stxx.wyhvisitorandroid.adapter.ScenicMapServerPointAdapter
import com.stxx.wyhvisitorandroid.base.BaseFragment
import com.stxx.wyhvisitorandroid.bean.NavigationData
import com.stxx.wyhvisitorandroid.bean.ServerPointResp
import com.stxx.wyhvisitorandroid.bean.VisitGridData
import com.stxx.wyhvisitorandroid.enums.ScenicMApPointEnum
import com.stxx.wyhvisitorandroid.location.BdLocation2
import com.stxx.wyhvisitorandroid.location.GeoBroadCast
import com.stxx.wyhvisitorandroid.location.showWakeApp
import com.stxx.wyhvisitorandroid.mplusvm.ScenicVm
import com.stxx.wyhvisitorandroid.service.PlaySoundService
import com.stxx.wyhvisitorandroid.view.ar.WalkNavUtil
import com.stxx.wyhvisitorandroid.widgets.*
import kotlinx.android.synthetic.main.fragment_scenic.*
import kotlinx.android.synthetic.main.title_bar.*
import org.jetbrains.anko.support.v4.dip
import org.jetbrains.anko.support.v4.longToast
import org.jetbrains.anko.support.v4.toast
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.util.*


/**
 * description:电子地图
 * Created by liNan on 2020/1/14 15:53

 */
class ScenicMapFragment : BaseFragment(), TabLayout.OnTabSelectedListener,
    BaiduMap.OnMapLoadedCallback {


    private var robotNavData: List<NavigationData>? = null
    private val tabsText: Array<String> by lazy { resources.getStringArray(R.array.scenic_map) }

    override val layoutId: Int = R.layout.fragment_scenic

    private val mViewModel: ScenicVm by lazy { getViewModel<ScenicVm>() }

    private lateinit var walkNaviLaunchParam: WalkNaviLaunchParam

    //跳转过来时应该选中的索引
    private var defaultSelectTab = 0
    private var navFunName = ""

    //上一次选中tab的索引
    private var lastSelectTab: Int? = 0
    private val toiletTabIndex = 5
    private val parkTabIndex = 6

    //第一次加载进来默认隐藏bottomSheet,再次选中tab时需要显示bottomSheet
    private var isFirstLoad = true

    private var bottomSheetBehavior: BottomSheetBehavior3<NestedScrollView>? = null
    private var mapLoaded = false

    //当前经纬度
    private var currentLatitude: Double? = null
    private var currentLongitude: Double? = null

    //上一次请求的type类型
    private var lastType = -10
    private val mGeoFenceClient = GeoFenceClient(MVVMBaseApplication.appContext)

    //搜索过来的厕所名称/停车场名称 用来弹出infoWindow
    private val searchName by lazy { arguments?.get("name") }

    private val serverPointAdapter: ScenicMapServerPointAdapter by lazy {
        ScenicMapServerPointAdapter(R.layout.adapter_server_point, null)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //创建围栏广播
//        val filter = IntentFilter()
//        filter.addAction(GeoBroadCast.fenceaction)
//        this.context?.registerReceiver(GeoBroadCast, filter)
        getNavData()
    }

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        lifecycle.addObserver(mViewModel)
        val saveState = savedInstanceState?.getInt(BUNDLE_SELECT_TAB)
        //从搜索或者机器人问答跳转过来
        val isRobot = arguments?.getBoolean(BUNDLE_IS_ROBOT) ?: false
        if (isRobot) {
            defaultSelectTab = arguments?.getInt(BUNDLE_SELECT_TAB, 0) ?: 0
            lastSelectTab = defaultSelectTab
            navFunName = arguments?.getString(BUNDLE_NAV_NAME) ?: ""
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
            val convertLatLng =
                convertBaidu(data.y?.toDouble() ?: 0.toDouble(), data.x?.toDouble() ?: 0.toDouble())
            //显示infoWindow
            showInfoWindow(convertLatLng, data)
            //点击服务点时称动到地图中心并隐藏bottomSheet
            mapView?.fixedLocation2Center(convertLatLng)
            hiddenSheet()
        }
        //infoWindow上导航按钮点击事件
        serverPointAdapter.onItemClick { resp: ServerPointResp ->
            goWalkNav(resp, convertBaidu(resp.y?.toDouble()!!, resp.x?.toDouble()!!))
        }

        ibMessage.setOnClickListener {
            it.findNavController().navigate(R.id.fragment_push_message, null, navOption)
        }
        ibScanQr.setOnClickListener {
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
                launchSingleTop = true
                popUpTo(R.id.fragment_search) { inclusive = true }
            })
        }
        initMap()
        initLocation2()
        //每次都需要重新创建否则从返回到该页面时添加的callback无效
        bottomSheetBehavior = BottomSheetBehavior3.from(scrollBottom)

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
            toiletTabIndex -> {
                //厕所
                http?.getWithoutLoading(ApiService.TOILET_LST_URL_ID, listOf("id" to id))
                    ?.subscribe({ Logger.i(it) }, {})

            }
            parkTabIndex -> {
                //停车场
                http?.getWithoutLoading(ApiService.PARK_LST_URL_ID, listOf("id" to id))
                    ?.subscribe({ Logger.i(it) }, {})
            }
        }
    }

    private fun initLocation2() {
        //开始定位
        requestPermission2(Manifest.permission.ACCESS_FINE_LOCATION) {
            mapView.map?.isMyLocationEnabled = true
            BdLocation2.startLocation.bdLocationListener {
                //先移除围栏再添加围栏可使再次调用广播进行回调位置信息
                mGeoFenceClient.removeGeoFence()
                mGeoFenceClient.addGeoFence("温榆河公园", "旅游景点", "北京", 1, " 0001")
                //防止定位回调时 View已经注销
                val map = mapView?.map
                currentLatitude = it.latitude //获取纬度信息
                currentLongitude = it.longitude //获取经度信息
                //设置显示在地图上的定位数据
                val locationData = MyLocationData.Builder()
                    .direction(it.direction)
                    .accuracy(it.radius)
                    .latitude(it.latitude)
                    .longitude(it.longitude)
                    .build()
                map?.setMyLocationData(locationData)
            }.setDistanceListener {
                if (findNavController().currentDestination?.id == R.id.dialog_smart_tip) return@setDistanceListener
                findNavController().navigate(R.id.dialog_smart_tip,
                    bundleOf("locationBean" to it),
                    navOptions {
                        launchSingleTop = true
                        popUpTo(R.id.dialog_smart_tip) { inclusive = true }
                        anim {
                            enter = R.anim.alpha_enter
                            exit = R.anim.alpha_exit
                        }
                    })

            }
        }
        //初始化围栏(在位置回调中先进行移除再添加达到每隔2s回调一次)
        mGeoFenceClient.createPendingIntent(GeoBroadCast.fenceaction)
        mGeoFenceClient.setTriggerCount(Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE)
        mGeoFenceClient.setActivateAction(GeoFenceClient.GEOFENCE_IN)
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
        val map = mapView.map
        map.setOnMapLoadedCallback(this)
        mapView?.init()
        mapView?.mapStatusBuild(SaveMapObj.target, SaveMapObj.mapZoom)
        mapView?.customMap(this.requireContext())
        map.addTileLayer(overLayOptions)
        map.setOnMapClickListener(object : BaiduMap.OnMapClickListener {
            override fun onMapClick(p0: LatLng?) {
                hiddenSheet()
                mapView.map.hideInfoWindow()
            }

            override fun onMapPoiClick(p0: MapPoi?) {
            }
        })
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
        //在息屏时后再次打开时需要重新添加bottomCallback否则上一个callback无效
        bottomSheetBehavior?.addBottomSheetCallback(bottomCallBack)
        //如果从机器人页面询问的是子项目,让它处于展开状态
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
                    val enumType = ScenicMApPointEnum.values()[index].ordinal + 1
                /*    //替换特色景点为交通游览设施
                    if (enumType == 2) {
                        enumType = 8
                    }*/
                    //type 从1开始所以把index加1
                    loadData(
                        enumType,
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
                toiletTabIndex -> {
                    //厕所 不用传type
                    loadData(0, ApiService.TOILET_LST_URL)
                }
                parkTabIndex -> {
                    //停车场
                    loadData(-1, ApiService.PARK_LST_URL)
                }
            }
        }
        executeSelect()
        lastSelectTab = tab?.position
        //恢复地图状态
        mapView?.fixedLocation2Center()
        mapView?.mapStatusBuild()
    }

    private fun loadData(type: Int, url: String) {
        //设置是否是停车场数据
        serverPointAdapter.isPark = type == -1
        //是否是厕所
        serverPointAdapter.isToilet = type == 0
        val value = mViewModel.pointLiveData.value
        if (type == lastType && value != null && value is SuccessSource) {
            val newData = if (navFunName.isNotEmpty()) {
                val navItemObj =
                    robotNavData?.filter { f -> f.navFuncName == navFunName }
                if (!navItemObj.isNullOrEmpty()) {
                    val filterData = value.body.data.filter { f ->
                        f.name.trim().toLowerCase(Locale.CHINA) == navItemObj[0].name.toLowerCase(
                            Locale.CHINA
                        )
                    }
                    if (filterData.isNullOrEmpty()) {
                        value.body.data
                    } else {
                        filterData
                    }
                } else {
                    value.body.data
                }
            } else {
                value.body.data
            }
            serverPointAdapter.setList(newData.toMutableList())
            createMarket(newData)
            //重置导航函数名
            navFunName = ""
        } else {
            mViewModel.getServicePoint(type, url)
                .observe(this, Observer {
                    handlerResponseData(it, { resp ->
                        val newData = if (navFunName.isNotEmpty()) {
                            //获取navigation.json中的和百度unit返回的函数名相同的对象数据
                            val navItemObj =
                                robotNavData?.filter { f -> f.navFuncName == navFunName }
                            //如果json文件中存在则进行过滤获取name相同的数据
                            if (!navItemObj.isNullOrEmpty()) {
                                val filterData = resp.data.filter { f ->
                                    f.name.trim()
                                        .toLowerCase(Locale.CHINA) == navItemObj[0].name.toLowerCase(
                                        Locale.CHINA
                                    )
                                }
                                if (filterData.isNullOrEmpty()) {
                                    resp.data
                                } else {
                                    filterData
                                }
                            } else {
                                resp.data
                            }
                        } else {
                            resp.data
                        }
                        serverPointAdapter.setList(newData.toMutableList())
                        createMarket(newData)
                    }, {
                        this.context?.showToast("暂无数据")
                    })
                    //重置导航函数名
                    navFunName = ""
                })
        }

        //保存当前请求的type
        lastType = type

    }

    /**
     * 机器人对话导航关系表
     */
    private fun getNavData() {
        var inputStream: InputStream? = null
        var inputStreamReader: InputStreamReader? = null
        var bufferedReader: BufferedReader? = null

        try {
            inputStream = resources.assets.open("json/navigation.json")
            inputStreamReader = InputStreamReader(inputStream)
            bufferedReader = BufferedReader(inputStreamReader)
            var strJson = bufferedReader.readLine()
            val stringBuilder = StringBuilder()
            while (strJson != null) {
                stringBuilder.append(strJson)
                strJson = bufferedReader.readLine()
            }
            val type = object : TypeToken<List<NavigationData>>() {}.type
            robotNavData = GsonUtil.str2Obj<List<NavigationData>>(stringBuilder.toString(), type)
        } catch (ex: Exception) {
        } finally {
            inputStream?.close()
            inputStreamReader?.close()
            bufferedReader?.close()
        }
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
        //infoWindow数据的index
        var searchInfoIndex = -1
        for (item in xy.indices) {
            //经度
            val lng = xy[item].first?.toDouble() ?: 0.toDouble()
            //经度
            val lat = xy[item].second?.toDouble() ?: 0.toDouble()
            if (lastSelectTab == ScenicMApPointEnum.TOILET.ordinal || lastSelectTab == ScenicMApPointEnum.PARK.ordinal) {
                bitmap = getMarkerBitmap(data[item])
                //如果是厕所/停车场 在地图上显示infoWindow不去评论页面
                if (searchName == data[item].name) {
                    searchInfoIndex = item
                }
            }
            val options = with(MarkerOptions()) {
                position(convertBaidu(lat, lng))
                icon(bitmap)
                zIndex(17)
                animateType(MarkerOptions.MarkerAnimateType.grow)
                extraInfo(bundleOf("marketExtra" to data[item]))
            }

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
                mapView?.fixedLocation2Center(LatLng(currentLatitude!!, currentLongitude!!))
            } else {
                val pointData = it.extraInfo.getSerializable("marketExtra") as ServerPointResp
                showInfoWindow(it.position, pointData)
            }
            return@setOnMarkerClickListener true
        }
        //如果是厕所/停车场 在地图上显示infoWindow不去评论页面
        if (searchInfoIndex != -1) {
            showInfoWindow(
                convertBaidu(
                    (data[searchInfoIndex].y ?: "0").toDouble(),
                    (data[searchInfoIndex].x ?: "0").toDouble()
                ), data[searchInfoIndex]
            )
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
                //厕所-1代表非智慧厕所则不显示marker下的文字
                if (data.type != "-1") {
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
                } else {
                    tv.setBackgroundResource(android.R.color.transparent)
                }
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
        mapView?.fixedLocation2Center(latLng)
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
        if (explain.isNullOrEmpty() || !explain.startsWith("http")) {
            blVoice.visibility = View.GONE
        } else {
            blVoice.visibility = View.VISIBLE
        }
        //设置宽度 设置match 地图会
        val infoWindow = InfoWindow(dialog, latLng, -96)
        mapView.map?.showInfoWindow(infoWindow)

        dialog.setOnClickListener {
            loadMarkerScenicDetail(pointData)
        }
        blNav.setOnClickListener {
            goWalkNav(pointData, latLng)
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
            rule.text = "收费规则:${pointData.parkingFee ?: "--"}${pointData.until}"
            retailNum.text = pointData.residue
            parkSubscriber.visibility = View.INVISIBLE
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
                cslMan.visibility = View.VISIBLE
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
                cslBarrierFree.visibility = View.VISIBLE
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
            goWalkNav(pointData, latLng)
        }
    }

    /**
     * 后台返回
     * @param latLng 导航终点经纬度(百度坐标)
     */
    private fun goWalkNav(
        pointData: ServerPointResp,
        latLng: LatLng
    ) {
        if (GeoBroadCast.status == GeoFence.STATUS_IN || GeoBroadCast.status == GeoFence.INIT_STATUS_IN) {
            //进入园区则使用园区导航
            requestPermission2(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            ) {
                //                customWalkNav(latLng)
                baiduWalkNav(latLng)
            }
        } else {
            //未进入园区
            if (this.context != null && currentLongitude != null) {
                showWakeApp(
                    this.requireContext(),
                    LatLng(currentLatitude!!, currentLongitude!!),
                    latLng,
                    pointData.name
                )
            } else {
                this.context?.showToast("无法获取当前位置,暂不能导航")
            }
        }
    }

    /**
     * 使用第三方步行导航
     * @param latLng 终点经纬度
     */
    private fun customWalkNav(latLng: LatLng) {
        //40.08140775275331==116.47102257186532
        //松云华盖
        // val start = ArtMapMark("A", 116.4734195350233, 40.085884758824925, 1)
        //intent.putExtra("start", start)
        val end = ArtMapMark("B", latLng.longitude, latLng.latitude, 1)
        val intent = Intent(this.context, ArNavActivity2::class.java)
        intent.putExtra("stop", end)
        startActivity(intent)
    }

    /**
     * 使用百度步行导航
     */
    private fun baiduWalkNav(latLng: LatLng) {
        WalkNavUtil.setParam(
            LatLng(currentLatitude!!, currentLongitude!!),
            latLng,
            this.requireActivity()
        ).startNav()
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
        //上传历史足迹
        findById(pointData.id)
        findNavController().navigate(
            R.id.fragment_scenic_comment,
            bundleOf(
                BUNDLE_SCENIC_DETAIL to pointData,
                "end" to convertBaidu(
                    (pointData.y ?: "0").toDouble(),
                    ((pointData.x ?: "0").toDouble())
                )
            ),
            navOption
        )
    }

    override fun onMapLoaded() {
        mapLoaded = true
        ivMoveToCenterLocation.visibility = View.VISIBLE
        //移动到定位中心
        try {
            ivMoveToCenterLocation?.setOnClickListener {
                if (GeoBroadCast.status == GeoFence.INIT_STATUS_IN || GeoBroadCast.status == GeoFence.STATUS_IN) {
                    val latLng =
                        LatLng(currentLatitude ?: 0.toDouble(), currentLongitude ?: 0.toDouble())
                    mapView?.fixedLocation2Center(latLng)

                } else if (GeoBroadCast.status == GeoFence.INIT_STATUS_OUT || GeoBroadCast.status == GeoFence.STATUS_OUT) {
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
            mapView?.fixedLocation2Center()
        }
        //限制地图只显示在某一区域
        val builders = LatLngBounds.Builder()
        builders.include(LatLng(40.09364835966737, 116.4920968191294))
            .include(LatLng(40.071822098761984, 116.46385409389569))
//        mapView.map.setMapStatusLimits(builders.build())
        //设置不显示百度底图
//        mapView?.map?.mapType = BaiduMap.MAP_TYPE_NONE
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
        try {
            this.context?.unregisterReceiver(GeoBroadCast)
            BaiduMapRoutePlan.finish(this.context)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(BUNDLE_SELECT_TAB, lastSelectTab!!)
    }

}

object SaveMapObj {
    //second  true表示bottomSheet hidden false表示show
    var needSaveState: Pair<Int, Boolean>? = null
    var bottomState: Int = -1

    //onDestroyView时地图的绽放级别
    var mapZoom = 17f

    //onDestroyView时保存地图的中心点
    var target: LatLng = SCENIC_CENTER_LATLNG
    var geoBroadCastStatus: Int? = null
}

