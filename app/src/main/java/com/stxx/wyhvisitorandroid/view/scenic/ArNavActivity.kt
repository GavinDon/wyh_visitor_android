package com.stxx.wyhvisitorandroid.view.scenic

import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.gavindon.mvvm_lib.utils.requestPermission
import com.orhanobut.logger.Logger
import com.quyuanfactory.artmap.ArtMap
import com.quyuanfactory.artmap.ArtMapPoi
import com.stxx.wyhvisitorandroid.R
import kotlinx.android.synthetic.main.fragment_ar_nav.*


/**
 * description:
 * Created by liNan on  2020/8/10 09:36
 */
class ArNavActivity : AppCompatActivity() {

    //marker点击回调数据
    private var curPoi: ArtMapPoi? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_ar_nav)
        requestLocation()
        mapCallback()

    }

    private fun requestLocation() {
        //开启定位
        Handler().postDelayed({
            ArtMap.EnableLocation(true)
        }, 300)
        //进行导航
        btnNav.setOnClickListener {
            requestPermission(android.Manifest.permission.CAMERA) {
                if (curPoi != null) {
                    if (ArtMap.isRouted()) ArtMap.CancelRoute()
                    ArtMap.SearchRoute(curPoi!!)
                    ArtMap.StartAR()
                    artmapArview.visibility = View.VISIBLE
                }
            }

        }

    }

    /**
     * 回调处理
     */
    private fun mapCallback() {
        ArtMap.SetCallBack(object : ArtMap.CallBack {
            override fun poiClicked(p0: ArtMapPoi?) {
                curPoi = ArtMapPoi(p0)
            }

            override fun arClicked(p0: String?) {
            }

            override fun arLoaded(p0: Boolean) {
                Logger.i(p0.toString())
            }

            override fun Routed(ret: Boolean) {
                Logger.i(ret.toString())
            }

            override fun Navigated(p0: Float, p1: Float, p2: String?) {
            }
        })
    }

    override fun onPause() {
        super.onPause()
        artmapview?.onPause()
    }

    override fun onResume() {
        super.onResume()
        artmapview?.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        ArtMap.Destroy()
        artmapview?.onDestroy()

    }

}