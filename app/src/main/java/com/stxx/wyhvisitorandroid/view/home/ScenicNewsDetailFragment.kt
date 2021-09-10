package com.stxx.wyhvisitorandroid.view.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.os.bundleOf
import androidx.core.text.HtmlCompat
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.transition.TransitionInflater
import com.baidu.geofence.GeoFence
import com.baidu.geofence.GeoFenceClient
import com.baidu.mapapi.map.BitmapDescriptorFactory
import com.baidu.mapapi.map.MarkerOptions
import com.baidu.mapapi.map.OverlayOptions
import com.baidu.mapapi.model.LatLng
import com.baidu.mapapi.search.route.PlanNode
import com.baidu.mapapi.search.route.RoutePlanSearch
import com.baidu.mapapi.search.route.WalkingRoutePlanOption
import com.baidu.mapapi.search.route.WalkingRouteResult
import com.gavindon.mvvm_lib.base.MVVMBaseApplication
import com.gavindon.mvvm_lib.net.SuccessSource
import com.gavindon.mvvm_lib.net.http
import com.gavindon.mvvm_lib.utils.getStatusBarHeight
import com.gavindon.mvvm_lib.widgets.showToast
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.gyf.immersionbar.ImmersionBar
import com.orhanobut.logger.Logger
import com.stxx.wyhvisitorandroid.ApiService
import com.stxx.wyhvisitorandroid.BUNDLE_DETAIL
import com.stxx.wyhvisitorandroid.R
import com.stxx.wyhvisitorandroid.WebViewUrl.SHARE_URL
import com.stxx.wyhvisitorandroid.base.ToolbarFragment
import com.stxx.wyhvisitorandroid.bean.*
import com.stxx.wyhvisitorandroid.convertBaidu
import com.stxx.wyhvisitorandroid.graphics.HtmlTagHandler
import com.stxx.wyhvisitorandroid.graphics.ImageLoader
import com.stxx.wyhvisitorandroid.location.BdLocation2
import com.stxx.wyhvisitorandroid.location.GeoBroadCast
import com.stxx.wyhvisitorandroid.location.showWakeApp
import com.stxx.wyhvisitorandroid.mplusvm.HomeVm
import com.stxx.wyhvisitorandroid.view.ar.WalkNavUtil
import com.stxx.wyhvisitorandroid.view.helpers.SimpleOnGetRoutePlanResultListener
import com.stxx.wyhvisitorandroid.view.helpers.WeChatRegister
import com.stxx.wyhvisitorandroid.view.helpers.WeChatUtil
import com.stxx.wyhvisitorandroid.view.overlayutil.WalkingRouteOverlay
import com.stxx.wyhvisitorandroid.widgets.*
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject
import com.tencent.mm.opensdk.openapi.IWXAPI
import kotlinx.android.synthetic.main.activity_display.*
import kotlinx.android.synthetic.main.fragment_scenic_news_detail.*
import kotlinx.android.synthetic.main.toolbar.*
import org.jetbrains.anko.find
import org.jetbrains.anko.support.v4.toast


/**
 * description: 景点详情
 * Created by liNan on  2020/3/6 10:54
 */
class ScenicNewsDetailFragment : ToolbarFragment() {
    override val layoutId: Int = R.layout.fragment_scenic_news_detail

    private lateinit var mViewModel: HomeVm
    private lateinit var broadcastReceiver: BroadcastReceiver
    private var walkSearchLst = mutableListOf<RoutePlanSearch>()

    //当前经纬度
    private var currentLatitude: Double? = null
    private var currentLongitude: Double? = null

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        frame_layout_title.setBackgroundColor(Color.WHITE)

        /*热门推荐,新闻资讯, 景区百科*/
        when (val detailData = arguments?.getSerializable(BUNDLE_DETAIL)) {
            is ScenicNewsResp -> {
                //景区资讯
                findById(ApiService.SCENIC_NEWS_ID, detailData.id)
                initView(
                    detailData.imgurl,
                    detailData.title,
                    detailData.content,
                    detailData.gmt_modfy
                )
            }
            is VegetationWikiResp -> {
                //景区百科
                findById(ApiService.SCENIC_WIKI_ID, detailData.id)
                initView(detailData.img, detailData.name, detailData.content, detailData.synopsis)
            }
            is HotRecommendResp -> {
                //热门推荐
                findById(ApiService.HOT_RECOMMEND_ID, detailData.id)
                initView(
                    detailData.imgurl,
                    detailData.title,
                    detailData.content ?: "",
                    detailData.gmt_modfy
                )
                /*  tv_menu?.text = "分享"
                  tv_menu?.visibility = View.VISIBLE
                  //初始化位置和微信sdk
                  registerApp()
                  //规划线路
                  loadLineGuide(detailData.id)*/
            }
            is LineRecommendResp -> {
                //推荐路线
                findById(ApiService.VISIT_PARK_LINE_ID, detailData.id)
                initView(
                    detailData.imgurl,
                    detailData.title,
                    detailData.content,
                    detailData.gmt_modfy
                )
                tv_menu?.text = "分享"
                tv_menu?.visibility = View.VISIBLE
                //初始化位置和微信sdk
                registerApp()
                //规划线路
                loadLineGuide(detailData.id)
            }
            is PushMessageResp -> {
                initView(
                    detailData.imgurl,
                    detailData.title,
                    detailData.content,
                    detailData.gmt_modfy
                )
            }
            is PlantWikiResp -> {
                initView(
                    detailData.imgurl,
                    detailData.name,
                    detailData.introduction,
                    detailData.gmt_modified
                )
            }
        }


        sharedElementEnterTransition =
            TransitionInflater.from(this.requireContext()).inflateTransition(
                android.R.transition.move
            )
        tv_menu?.setOnClickListener {
            createShareDialog()
        }

    }


    /**
     * 查找景区百科根据id(详情不根据此获取)
     * 方法只是用来上传服务器 获取足迹使用
     *
     */
    private fun findById(url: String, id: Int) {
        http?.getWithoutLoading(url, listOf("id" to id))
    }

    private fun initView(url: String, title: String, content: String?, key: String) {
        ImageLoader.with().load(url).into(ivNewsDetailHead)
        tvNewsDetailTitle.text = title
//        tvNewsDetailDate.text = key
        HtmlUtil().show(this.context, content ?: "暂无内容", Handler {
            //        tvNewsDetailContent.text = it.obj.toString()
            tvNewsDetailContent?.text = it.obj as Spanned
            return@Handler false
        })
        tvNewsDetailContent?.movementMethod = LinkMovementMethod.getInstance()
    }

    /**
     * 路线推荐分享至微信
     */
    private fun shareUrlWeChat(scenic: Int) {
        val data = arguments?.getSerializable(BUNDLE_DETAIL)
        var shareBean: Share? = null
        if (data is LineRecommendResp) {
            shareBean = Share(data.id, data.title, data.content)
        } else if (data is HotRecommendResp) {
            shareBean = Share(data.id, data.title, data.content)
        }
        val webpage = WXWebpageObject()
        webpage.webpageUrl = "$SHARE_URL${shareBean?.id}"
        val msg = WXMediaMessage(webpage)
        msg.title = shareBean?.title
        val spanned = HtmlCompat.fromHtml(
            "${shareBean?.content}",
            HtmlCompat.FROM_HTML_MODE_COMPACT,
            null,
            HtmlTagHandler()
        )
        msg.description = spanned.toString()
        val drawable = ivNewsDetailHead.drawable
        if (drawable != null) {
            val bmp: Bitmap = drawable.toBitmap(100, 100)
            msg.thumbData = WeChatUtil.bmpToByteArray(bmp, true)
        }
        val req = SendMessageToWX.Req()
        req.transaction = buildTransaction("webPage")
        req.message = msg
        req.scene = scenic
        api?.sendReq(req)
    }

    private var api: IWXAPI? = null
    private val mGeoFenceClient = GeoFenceClient(MVVMBaseApplication.appContext)

    /**
     * 注册 appId
     */
    private fun registerApp() {
        api = WeChatRegister.wxApi
        initLocation2()
    }

    private fun initLocation2() {
        //开始定位
        requestPermission2(Manifest.permission.ACCESS_FINE_LOCATION) {
            BdLocation2.startLocation.bdLocationListener {
                //先移除围栏再添加围栏可使再次调用广播进行回调位置信息
                mGeoFenceClient.removeGeoFence()
                mGeoFenceClient.addGeoFence("沙子营湿地公园", "旅游景点", "北京", 1, " 0001")
                //防止定位回调时 View已经注销
                currentLatitude = it.latitude //获取纬度信息
                currentLongitude = it.longitude //获取经度信息
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
    }

    private fun loadLineGuide(id: Int) {
        mViewModel = getViewModel()
        lifecycle.addObserver(mViewModel)
        frlMapView.visibility = View.VISIBLE

        mapView?.init()
        mapView?.mapStatusBuild()
        mapView?.customMap(this.requireContext())
        ivMoveToScenicCenterLocation.setOnClickListener { mapView?.fixedLocation2Center() }
        mapView?.map?.addTileLayer(overLayOptions)

        mViewModel.getLinePointById(id).observe(this, Observer {
            if (it is SuccessSource) {
                searchRoute(it.body.data.points)
            }
        })
        mapViewDispatch()
    }

    private val tRoutePlanResultListener = object : SimpleOnGetRoutePlanResultListener() {
        override fun onGetWalkingRouteResult(walkResult: WalkingRouteResult) {
            val overlay = WalkingRouteOverlay(mapView?.map)
            val routeLines = walkResult.routeLines
            if (!routeLines.isNullOrEmpty()) {
                val routeLine = walkResult.routeLines[0]
                overlay.setData(routeLine)
                overlay.addToMap()
            }
        }
    }


    private fun searchRoute(points: List<Point>) {
        if (points.isNullOrEmpty()) return

        points.forEachIndexed { index, point ->
            //如果经纬度数据不是最后一个则设置起终点进行路线规划
            if (index != points.lastIndex) {
                val start = convertBaidu(
                    point.y.toDouble(), point.x.toDouble()
                )
                val end = convertBaidu(
                    points[index + 1].y.toDouble(), points[index + 1].x.toDouble()
                )

                val search = RoutePlanSearch.newInstance()
                walkSearchLst.add(search)

                search.setOnGetRoutePlanResultListener(tRoutePlanResultListener)
                search.walkingSearch(
                    WalkingRoutePlanOption().from(PlanNode.withLocation(start))
                        .to(PlanNode.withLocation(end))
                )
            }

        }
        markerPointIndex(points)
    }

    /**
     * 标注景点顺序
     */
    private fun markerPointIndex(points: List<Point>) {
        val markerView = layoutInflater.inflate(R.layout.marker_number, null, false)
        val markerOptions = mutableListOf<OverlayOptions>()
        points.forEachIndexed { index, point ->
            val tvIndex = markerView.findViewById<TextView>(R.id.tvMarkerIndex)
            tvIndex.text = "${index.plus(1)}-${point.name}"
            val bitmapDescriptorFactory = BitmapDescriptorFactory.fromView(markerView)
            val options = with(MarkerOptions()) {
                position(convertBaidu(point.y.toDouble(), point.x.toDouble()))
                icon(bitmapDescriptorFactory)
                zIndex(17)
                animateType(MarkerOptions.MarkerAnimateType.grow)
                    .extraInfo(bundleOf(Pair("index", index)))
            }
            markerOptions.add(options)
        }
        mapView?.map?.addOverlays(markerOptions)
        //点击marker进行导航
        mapView?.map?.setOnMarkerClickListener { marker ->
            val index = marker.extraInfo.getInt("index")
            val point = points[index]
            goWalkNav(point, marker.position)
            return@setOnMarkerClickListener true
        }
    }

    /*解决mapView与ScrollView滑动冲突*/
    @SuppressLint("ClickableViewAccessibility")
    private fun mapViewDispatch() {
        val view = mapView?.getChildAt(0)
        view?.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                scrollView.requestDisallowInterceptTouchEvent(false)
            } else {
                scrollView.requestDisallowInterceptTouchEvent(true)
            }
            return@setOnTouchListener false

        }
    }

    /**
     * 后台返回
     * @param latLng 导航终点经纬度(百度坐标)
     */
    private fun goWalkNav(
        pointData: Point,
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
     * 使用百度步行导航
     */
    private fun baiduWalkNav(latLng: LatLng) {
        WalkNavUtil.setParam(
            LatLng(currentLatitude!!, currentLongitude!!),
            latLng,
            this.requireActivity()
        ).startNav()
    }

    private fun createShareDialog() {

        val view = layoutInflater.inflate(R.layout.bottom_share, null, false)
        val ivWeChatFriend = view.find<ImageView>(R.id.ivWeChatFriend)
        val ivWeChatGroup = view.find<ImageView>(R.id.ivWeChatGroup)
        val ivWeChatCollect = view.find<ImageView>(R.id.ivWeChatCollect)

        val bsd = BottomSheetDialog(this.requireContext())
        bsd.setContentView(view)
        if (!bsd.isShowing) bsd.show()
        ivWeChatFriend.setOnClickListener {
            shareUrlWeChat(SendMessageToWX.Req.WXSceneSession)
//            shareWeChat(SendMessageToWX.Req.WXSceneSession)
        }
        ivWeChatGroup.setOnClickListener {
            //分享朋友圈
            shareUrlWeChat(SendMessageToWX.Req.WXSceneTimeline)

//            shareWeChat(SendMessageToWX.Req.WXSceneTimeline)
        }
        ivWeChatCollect.setOnClickListener {
            shareUrlWeChat(SendMessageToWX.Req.WXSceneFavorite)

//            shareWeChat(SendMessageToWX.Req.WXSceneFavorite)
        }

    }

    private fun buildTransaction(type: String?): String? {
        return if (type == null) System.currentTimeMillis()
            .toString() else type + System.currentTimeMillis()
    }

    private fun view2Image(view: View): Bitmap {
        val shareBitmap = Bitmap.createBitmap(
            view.measuredWidth,
            view.measuredHeight,
            Bitmap.Config.ARGB_8888
        )
        val c = Canvas(shareBitmap)
        view.draw(c)
        return shareBitmap
    }

    override fun setStatusBar() {
        titleBar.setBackgroundColor(ContextCompat.getColor(this.requireContext(), R.color.white))
        titleBar.layoutParams.height = getStatusBarHeight(this.requireContext())
        ImmersionBar.with(this)
            .fitsSystemWindows(false)
            .statusBarDarkFont(true)
            .transparentStatusBar()
            .init()
    }

    override fun onDestroyView() {
        super.onDestroyView()
//        search.destroy()
        walkSearchLst.forEach {
            it.destroy()
        }
        mapView?.onDestroy()
        /* if (this::broadcastReceiver.isInitialized) {
             this.context?.unregisterReceiver(broadcastReceiver)
         }*/
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }


    override val toolbarName: Int = R.string.detail

}