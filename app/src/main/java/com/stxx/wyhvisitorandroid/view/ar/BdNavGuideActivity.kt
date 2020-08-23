package com.stxx.wyhvisitorandroid.view.ar

import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.drawable.toDrawable
import com.baidu.mapapi.walknavi.adapter.IWNaviStatusListener
import com.baidu.mapapi.walknavi.model.WalkNaviDisplayOption
import com.baidu.platform.comapi.walknavi.WalkNaviModeSwitchListener
import com.baidu.platform.comapi.walknavi.widget.ArCameraView
import com.baidu.tts.client.SpeechSynthesizer
import com.baidu.tts.client.TtsMode
import com.bumptech.glide.load.resource.bitmap.BitmapResource
import com.gavindon.mvvm_lib.widgets.showToast
import com.stxx.wyhvisitorandroid.R
import com.stxx.wyhvisitorandroid.view.ar.WalkNavUtil.walkNavigateHelper
import com.stxx.wyhvisitorandroid.view.asr.Auth
import com.stxx.wyhvisitorandroid.view.helpers.SimpleIWRouteGuidanceListener
import kotlinx.android.synthetic.main.fragment_scenic.view.*

/**
 * description: AR导航类
 * Created by liNan on  2020/8/24 9:27
 */
class BdNavGuideActivity : Activity() {

    private var mSpeechSynthesizer: SpeechSynthesizer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        initSpeech()
        navStateListener()
        //开启导航
        walkNavigateHelper.startWalkNavi(this)
    }

    private fun initView() {
        try {
            //AR导航view
            val navView: View? = walkNavigateHelper.onCreate(this)
            //必须判断是否为null
            if (navView != null) {
                val rootView = layoutInflater.inflate(R.layout.activity_bdnav_guide, null, false)
                val frlNavView = rootView.findViewById<FrameLayout>(R.id.frlNavGuide)
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