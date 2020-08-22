package com.stxx.wyhvisitorandroid.view.ar

import android.app.Activity
import android.content.Intent
import com.baidu.mapapi.model.LatLng
import com.baidu.mapapi.walknavi.WalkNavigateHelper
import com.baidu.mapapi.walknavi.adapter.IWEngineInitListener
import com.baidu.mapapi.walknavi.adapter.IWRoutePlanListener
import com.baidu.mapapi.walknavi.model.WalkRoutePlanError
import com.baidu.mapapi.walknavi.params.WalkNaviLaunchParam
import com.baidu.mapapi.walknavi.params.WalkRouteNodeInfo
import com.gavindon.mvvm_lib.widgets.showToast
import com.orhanobut.logger.Logger


/**
 * description: 百度步行导航
 * Created by liNan on  2020/8/21 18:03
 */
object WalkNavUtil {


    private lateinit var startParam: LatLng
    private lateinit var endParam: LatLng
    private lateinit var act: Activity

    fun setParam(start: LatLng, end: LatLng, act: Activity): WalkNavUtil {
        this.startParam = start
        this.endParam = end
        this.act = act
        return this
    }

    fun startNav() {
        initEngine()
    }

    val walkNavigateHelper: WalkNavigateHelper
        get() = WalkNavigateHelper.getInstance()

    private fun initEngine() {
        // 获取导航控制类
        // 引擎初始化
        walkNavigateHelper.initNaviEngine(act, object : IWEngineInitListener {
            override fun engineInitSuccess() {
                routePlan()
            }

            override fun engineInitFail() {
                walkNavigateHelper.unInitNaviEngine()
            }
        })

    }

    /**
     * 发起算路
     */
    private fun routePlan() {
        val endInfo = WalkRouteNodeInfo()
        val startInfo = WalkRouteNodeInfo()
        startInfo.location = startParam
        endInfo.location = endParam
        val walkParam = WalkNaviLaunchParam()
        walkParam.apply {
            startNodeInfo(startInfo)
            endNodeInfo(endInfo)
            extraNaviMode(1)
        }
        walkNavigateHelper.routePlanWithRouteNode(walkParam,
            object : IWRoutePlanListener {
                override fun onRoutePlanStart() {
                    Logger.i("算路开始......")
                }

                override fun onRoutePlanSuccess() {
                    Logger.i("算路成功")
                    val intent =
                        Intent(act, WNaviGuideActivity::class.java)
                    act.startActivity(intent)
                }

                override fun onRoutePlanFail(p0: WalkRoutePlanError?) {
                    act.showToast("未找到路线")
                }

            })
    }
}