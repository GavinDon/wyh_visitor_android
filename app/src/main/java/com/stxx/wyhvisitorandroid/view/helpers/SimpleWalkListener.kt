package com.stxx.wyhvisitorandroid.view.helpers

import android.graphics.drawable.Drawable
import android.os.Message
import com.baidu.mapapi.search.route.*
import com.baidu.mapapi.walknavi.adapter.IWRouteGuidanceListener
import com.baidu.mapapi.walknavi.model.RouteGuideKind

/**
 * description:
 * Created by liNan on  2020/8/23 21:51
 */
abstract class SimpleIWRouteGuidanceListener : IWRouteGuidanceListener {
    override fun onRoutePlanYawing(p0: CharSequence?, p1: Drawable?) {
    }

    override fun onRouteGuideKind(p0: RouteGuideKind?) {
    }

    override fun onIndoorEnd(p0: Message?) {
    }

    override fun onRoadGuideTextUpdate(p0: CharSequence?, p1: CharSequence?) {
    }

    override fun onFinalEnd(p0: Message?) {
    }

    override fun onRemainDistanceUpdate(p0: CharSequence?) {
    }

    override fun onRemainTimeUpdate(p0: CharSequence?) {
    }

    override fun onArriveDest() {
    }

    override fun onRouteGuideIconUpdate(p0: Drawable?) {
    }

    override fun onReRouteComplete() {
    }

    override fun onVibrate() {
    }

    override fun onRouteFarAway(p0: CharSequence?, p1: Drawable?) {
    }

    override fun onGpsStatusChange(p0: CharSequence?, p1: Drawable?) {
    }

}

abstract class SimpleOnGetRoutePlanResultListener : OnGetRoutePlanResultListener {
    override fun onGetIndoorRouteResult(p0: IndoorRouteResult?) {
    }

    override fun onGetTransitRouteResult(p0: TransitRouteResult?) {
    }

    override fun onGetDrivingRouteResult(p0: DrivingRouteResult?) {
    }

    override fun onGetMassTransitRouteResult(p0: MassTransitRouteResult?) {
    }

    override fun onGetBikingRouteResult(p0: BikingRouteResult?) {
    }
}