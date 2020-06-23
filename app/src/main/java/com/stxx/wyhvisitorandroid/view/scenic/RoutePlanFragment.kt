package com.stxx.wyhvisitorandroid.view.scenic

import android.content.Intent
import android.os.Bundle
import androidx.navigation.fragment.findNavController
import com.baidu.mapapi.model.LatLng
import com.baidu.mapapi.walknavi.WalkNavigateHelper
import com.baidu.mapapi.walknavi.adapter.IWEngineInitListener
import com.baidu.mapapi.walknavi.adapter.IWNaviStatusListener
import com.baidu.mapapi.walknavi.adapter.IWRoutePlanListener
import com.baidu.mapapi.walknavi.model.WalkRoutePlanError
import com.baidu.mapapi.walknavi.params.WalkNaviLaunchParam
import com.baidu.mapapi.walknavi.params.WalkRouteNodeInfo
import com.baidu.platform.comapi.walknavi.WalkNaviModeSwitchListener
import com.gavindon.mvvm_lib.widgets.showToast
import com.gyf.immersionbar.ImmersionBar
import com.stxx.wyhvisitorandroid.BUNDLE_SCENIC_DETAIL
import com.stxx.wyhvisitorandroid.R
import com.stxx.wyhvisitorandroid.base.BaseFragment
import com.stxx.wyhvisitorandroid.base.ToolbarFragment
import com.stxx.wyhvisitorandroid.bean.ServerPointResp
import com.stxx.wyhvisitorandroid.view.ar.WNaviGuideActivity
import kotlinx.android.synthetic.main.fragment_route_plan.*
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.support.v4.toast

/**
 * description:
 * Created by liNan on  2020/5/1 00:08
 */
class RoutePlanFragment : BaseFragment() {


    private lateinit var detailData: ServerPointResp

    private lateinit var mStart: LatLng
    private lateinit var mEnd: LatLng

    override fun setStatusBar() {
        ImmersionBar.with(this)
            .transparentStatusBar()
            .fullScreen(true)
            .init()
    }

    override val layoutId: Int = R.layout.fragment_route_plan

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        if (SaveDetail.detail != null) {
            detailData = SaveDetail.detail!!
        } else {
            detailData = arguments?.getSerializable(BUNDLE_SCENIC_DETAIL) as ServerPointResp
        }
        val lnglatList = detailData.lngLat
        val points = mutableListOf<WalkRouteNodeInfo>()

        try {
            for (i in lnglatList) {
                val ll = i.split(",")
                val walkInfo = WalkRouteNodeInfo()
                if (ll.size == 2) {
                    walkInfo.location = LatLng(ll[0].toDouble(), ll[1].toDouble())
                    points.add(walkInfo)
                }
            }
        } catch (e: Exception) {

        }

        tvArNav.setOnClickListener {
            if (points.size >= 2) {
                mStart = points[0].location
                mEnd = points[lnglatList.size - 1].location
                initEngine()
            } else {
                this.context?.showToast("经纬度坐标不正确")
            }
        }
    }

    private fun initEngine() {

        val context = this.activity
        if (context != null) {
            WalkNavigateHelper.getInstance().initNaviEngine(
                context,
                object : IWEngineInitListener {
                    override fun engineInitSuccess() {
                        initHelper()
                    }

                    override fun engineInitFail() {
                        toast("初始化引擎失败")
                        WalkNavigateHelper.getInstance().unInitNaviEngine()
                    }
                })
        }
    }

    private fun initHelper() {
        val startNodeInfo = WalkRouteNodeInfo()
        startNodeInfo.location = mStart
        val endNodeInfo = WalkRouteNodeInfo()
        endNodeInfo.location = mEnd
        val walkNaviLaunchParam = WalkNaviLaunchParam()
        walkNaviLaunchParam.apply {
            startNodeInfo(startNodeInfo)
            endNodeInfo(endNodeInfo)
            extraNaviMode(1)
        }
        /*算路成功之后跳转到导航页面*/
        WalkNavigateHelper.getInstance()
            .routePlanWithRouteNode(walkNaviLaunchParam, object : IWRoutePlanListener {
                override fun onRoutePlanStart() {
                }

                override fun onRoutePlanSuccess() {
                    val intent = Intent(
                        this@RoutePlanFragment.requireActivity(),
                        WNaviGuideActivity::class.java
                    )
                    startActivity(intent)
                }

                override fun onRoutePlanFail(p0: WalkRoutePlanError?) {
                }
            })

        WalkNavigateHelper.getInstance().setWalkNaviStatusListener(object : IWNaviStatusListener {
            override fun onWalkNaviModeChange(p0: Int, p1: WalkNaviModeSwitchListener?) {
                /* WalkNavigateHelper.getInstance()
                     .switchWalkNaviMode(this@RoutePlanFragment.requireActivity(), p0, p1)*/
            }

            override fun onNaviExit() {
            }
        })
    }

    override fun onStop() {
        super.onStop()
        SaveDetail.detail = detailData
    }

}

object SaveDetail {
    var detail: ServerPointResp? = null
}