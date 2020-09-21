package com.stxx.wyhvisitorandroid.view.home

import android.content.BroadcastReceiver
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.os.bundleOf
import androidx.core.text.HtmlCompat
import androidx.lifecycle.Observer
import androidx.transition.TransitionInflater
import com.baidu.mapapi.map.BitmapDescriptorFactory
import com.baidu.mapapi.map.MarkerOptions
import com.baidu.mapapi.map.OverlayOptions
import com.baidu.mapapi.model.LatLng
import com.baidu.mapapi.search.core.RouteNode
import com.baidu.mapapi.search.route.*
import com.gavindon.mvvm_lib.net.SuccessSource
import com.gavindon.mvvm_lib.net.http
import com.gavindon.mvvm_lib.utils.getStatusBarHeight
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
import com.stxx.wyhvisitorandroid.mplusvm.HomeVm
import com.stxx.wyhvisitorandroid.view.helpers.SimpleOnGetRoutePlanResultListener
import com.stxx.wyhvisitorandroid.view.helpers.WeChatRegister
import com.stxx.wyhvisitorandroid.view.helpers.WeChatUtil
import com.stxx.wyhvisitorandroid.view.overlayutil.WalkingRouteOverlay
import com.stxx.wyhvisitorandroid.widgets.*
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX
import com.tencent.mm.opensdk.modelmsg.WXImageObject
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject
import com.tencent.mm.opensdk.openapi.IWXAPI
import kotlinx.android.synthetic.main.fragment_scenic.*
import kotlinx.android.synthetic.main.fragment_scenic_news_detail.*
import kotlinx.android.synthetic.main.fragment_scenic_news_detail.mapView
import kotlinx.android.synthetic.main.toolbar.*
import org.jetbrains.anko.find


/**
 * description: 景点详情
 * Created by liNan on  2020/3/6 10:54
 */
class ScenicNewsDetailFragment : ToolbarFragment() {
    override val layoutId: Int = R.layout.fragment_scenic_news_detail

    private lateinit var mViewModel: HomeVm
    private lateinit var broadcastReceiver: BroadcastReceiver
    private val search by lazy { RoutePlanSearch.newInstance() }

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
//                webviewLine.visibility = View.VISIBLE
//                webviewLine.loadUrl("${LINE_WEB}${detailData.id}")
                registerApp()
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
    private fun shareWeChat(scenic: Int) {
        val req = SendMessageToWX.Req()
        val bmp = view2Image(ivNewsDetailHead)
        //分享媒体对象
        val msg = WXMediaMessage()

        val imageObj = WXImageObject(bmp)
        msg.mediaObject = imageObj
        val thumbBmp: Bitmap =
            Bitmap.createScaledBitmap(bmp, 100, 300, true)
        bmp.recycle()
        msg.mediaObject = imageObj

        req.transaction = buildTransaction("img")
        req.message = msg
        req.scene = scenic

        api?.sendReq(req)

    }

    private fun shareUrlWeChat(scenic: Int) {
        val data = arguments?.getSerializable(BUNDLE_DETAIL) as LineRecommendResp
        //初始化一个WXWebpageObject，填写url
        val webpage = WXWebpageObject()
        webpage.webpageUrl = "$SHARE_URL${data.id}"

        val msg = WXMediaMessage(webpage)
        msg.title = data.title
        val spanned = HtmlCompat.fromHtml(
            "${data.content}",
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

    /**
     * 注册 appId
     */
    private fun registerApp() {
        api = WeChatRegister.wxApi
    }

    private fun loadLineGuide(id: Int) {
        mViewModel = getViewModel()
        mapView.visibility = View.VISIBLE

        mapView?.init()
        mapView?.mapStatusBuild()
        mapView?.customMap(this.requireContext())
        mapView?.map?.addTileLayer(overLayOptions)

        mViewModel.getLinePointById(id).observe(this, Observer {
            if (it is SuccessSource) {
                searchRoute(it.body.data.points)
            }
        })

    }

    private fun searchRoute(points: List<Point>) {
        if (points.isNullOrEmpty()) return
        /*       val tRoutePlanResultListener = object : SimpleOnGetRoutePlanResultListener() {
                   override fun onGetWalkingRouteResult(walkResult: WalkingRouteResult) {
                       val overlay = WalkingRouteOverlay(mapView?.map)
                       val routeLines = walkResult.routeLines
                       if (!routeLines.isNullOrEmpty()) {
                           val walkingRouteLine = mutableListOf<WalkingRouteLine.WalkingStep>()
                           val routeLine = walkResult.routeLines[0]
                           for (point in points) {
                               val p = convertBaidu(
                                   point.y.toDouble(), point.x.toDouble()
                               )
                               val step = WalkingRouteLine.WalkingStep()
                               step.entrance = RouteNode.location(p)
                               walkingRouteLine.add(step)
                           }
                           routeLine.setSteps(walkingRouteLine)
                           overlay.setData(routeLine)
                           overlay.addToMap()
                       }
                   }
               }
               search.setOnGetRoutePlanResultListener(tRoutePlanResultListener)
               val start = convertBaidu(
                   points[0].y.toDouble(), points[0].x.toDouble()
               )
               val end = convertBaidu(
                   points[points.lastIndex].y.toDouble(), points[points.lastIndex].x.toDouble()
               )

               search.walkingSearch(
                   WalkingRoutePlanOption()
                       .from(PlanNode.withLocation(start))
                       .to(PlanNode.withLocation(end))
               )
       */

        val overlay = WalkingRouteOverlay(mapView?.map)
        val walkingRouteSteps = mutableListOf<WalkingRouteLine.WalkingStep>()
        val walkingRouteLine = WalkingRouteLine()
        val listLatLng = mutableListOf<LatLng>()
        val step = WalkingRouteLine.WalkingStep()
        points.forEachIndexed { _, point ->
            listLatLng.add(convertBaidu(point.y.toDouble(), point.x.toDouble()))
            step.wayPoints = listLatLng
        }
        walkingRouteLine.setSteps(listOf(step))
        overlay.setData(walkingRouteLine)
        overlay.addToMap()
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
            tvIndex.text = "${index.plus(1)}\n${point.name}"
            val bitmapDescriptorFactory = BitmapDescriptorFactory.fromView(markerView)
            val options = with(MarkerOptions()) {
                position(convertBaidu(point.y.toDouble(), point.x.toDouble()))
                icon(bitmapDescriptorFactory)
                zIndex(17)
                animateType(MarkerOptions.MarkerAnimateType.grow)
            }
            markerOptions.add(options)
        }
        mapView?.map?.addOverlays(markerOptions)
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
        search.destroy()
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