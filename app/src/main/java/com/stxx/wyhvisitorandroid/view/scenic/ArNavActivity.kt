package com.stxx.wyhvisitorandroid.view.scenic

import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowManager
import android.view.WindowManager.LayoutParams.FLAG_DITHER
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.quyuanfactory.artmap.ArtMap
import com.quyuanfactory.artmap.ArtMapPoi
import com.stxx.wyhvisitorandroid.R
import kotlinx.android.synthetic.main.fragment_ar_nav.*
import org.jetbrains.anko.find
import org.jetbrains.anko.matchParent


/**
 * description:
 * Created by liNan on  2020/8/10 09:36
 */
class ArNavActivity : AppCompatActivity() {

   /* //marker点击回调数据
    private var curPoi: ArtMapPoi? = null


    //导航弹出框
    private val bottomNavView by lazy { layoutInflater.inflate(R.layout.bottom_nav, null, false) }

    //init bottomDialog
    private val bottomSheetDialog by lazy {
        val bottomSheetDialog = BottomSheetDialog(this, R.style.bstDialog)
        bottomSheetDialog.setContentView(bottomNavView)
        bottomSheetDialog.window?.decorView?.layoutParams?.height=300
        return@lazy bottomSheetDialog
    }

    //提示是否导航
    private val navView by lazy {
        bottomNavView.find<ConstraintLayout>(R.id.cslNav)
    }

    //正在导航
    private val navingView by lazy {
        bottomNavView.find<ConstraintLayout>(R.id.cslNaving)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_ar_nav)
        requestLocation()
        mapCallback()

    }

    private fun requestLocation() {
        //开启定位
        *//*Handler().postDelayed({
            ArtMap.EnableLocation(true)
        }, 300)*//*
        //进行导航
        btnNav.setOnClickListener {
            showBottomNav()
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


    private fun showBottomNav() {
        //进行导航按钮
        val tvStartNav = bottomNavView.findViewById<TextView>(R.id.tvNav)
        //未进行导航,关闭
        val btnClose = bottomNavView.findViewById<ImageButton>(R.id.ibNavClose)
        //导航景点名
        val tvScenicName = bottomNavView.findViewById<TextView>(R.id.tvNavScenicName)
        //退出导航
        val tvExitNav = bottomNavView.findViewById<TextView>(R.id.tvNavExit)
//        val navingView = bottomNavView.find<ConstraintLayout>(R.id.cslNaving)
//        val navView = bottomNavView.find<ConstraintLayout>(R.id.cslNav)
        bottomSheetDialog.window?.findViewById<FrameLayout>(R.id.design_bottom_sheet)
            ?.setBackgroundResource(android.R.color.transparent)

        bottomSheetDialog.window?.findViewById<FrameLayout>(R.id.design_bottom_sheet)
        btnClose.setOnClickListener {
            controlBottomCanClose(true)
            bottomSheetDialog.dismiss()

        }
        tvStartNav.setOnClickListener {
            controlBottomCanClose(false)
            navingView.visibility = View.VISIBLE
            navView.visibility = View.GONE
        }
        tvExitNav.setOnClickListener {
            controlBottomCanClose(true)
            bottomSheetDialog.dismiss()
        }

        if (!bottomSheetDialog.isShowing) {
            bottomSheetDialog.show()
            controlBottomCanClose(false)
        }
    }

    *//**
     * 控制是否可以下滑关闭bottomSheetDialog
     *//*
    private fun controlBottomCanClose(close: Boolean = true) {
        bottomSheetDialog.let {
            it.setCancelable(close)
            it.setCanceledOnTouchOutside(close)
        }
    }

    *//**
     * 回调处理
     *//*
    private fun mapCallback() {
        ArtMap.SetCallBack(object : ArtMap.CallBack {
            override fun arClicked(p0: String?) {
            }

            override fun arLoaded(p0: Boolean) {
            }

            override fun poiClicked(p0: ArtMapPoi?) {
            }

            override fun Routed(p0: Boolean) {
            }

            override fun Navigated(p0: Float, p1: Float, p2: String?) {
            }

            override fun mapLoaded(p0: Boolean) {
                if (p0) ArtMap.EnableLocation(true)
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

    }*/

}