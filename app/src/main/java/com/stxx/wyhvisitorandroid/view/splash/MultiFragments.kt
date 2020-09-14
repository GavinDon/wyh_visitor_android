package com.stxx.wyhvisitorandroid.view.splash

import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.os.Bundle
import android.view.Gravity
import androidx.activity.addCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.*
import androidx.navigation.ui.NavigationUI
import com.baidu.geofence.GeoFenceClient
import com.gavindon.mvvm_lib.base.MVVMBaseApplication
import com.gavindon.mvvm_lib.utils.SpUtils
import com.gavindon.mvvm_lib.utils.phoneHeight
import com.gavindon.mvvm_lib.utils.phoneWidth
import com.gavindon.mvvm_lib.utils.rxRequestPermission
import com.huawei.hms.hmsscankit.ScanUtil
import com.huawei.hms.ml.scan.HmsScan
import com.stxx.wyhvisitorandroid.*
import com.stxx.wyhvisitorandroid.R
import com.stxx.wyhvisitorandroid.base.BaseActivity
import com.stxx.wyhvisitorandroid.location.BdLocation2
import com.stxx.wyhvisitorandroid.location.GeoBroadCast
import com.stxx.wyhvisitorandroid.view.helpers.WeChatRegister
import com.stxx.wyhvisitorandroid.view.helpers.WebViewCameraHelper
import com.stxx.wyhvisitorandroid.view.home.HomeFragment
import com.stxx.wyhvisitorandroid.view.mine.MineFragment
import com.stxx.wyhvisitorandroid.view.scenic.ScenicMapFragment
import com.stxx.wyhvisitorandroid.widgets.SuspensionDragView
import kotlinx.android.synthetic.main.fragment_multi_root.*
import org.jetbrains.anko.dip
import org.jetbrains.anko.longToast
import java.lang.Exception

/**
 * description: 多fragment根activity
 * Created by liNan on  2020/2/17 19:39
 */
class MultiFragments : BaseActivity() {

    private val navController: NavController by lazy { findNavController(R.id.fragments) }
    override val layoutId: Int = R.layout.fragment_multi_root
    private val homeFragment1: HomeFragment by lazy { HomeFragment() }
    private val homeFragment2: ScenicMapFragment by lazy { ScenicMapFragment() }
    private val homeFragment3: MineFragment by lazy { MineFragment() }

    var dragView: SuspensionDragView? = null
    private lateinit var mFragments: List<Fragment>
    private val getFragments
        get() = listOf(homeFragment1, homeFragment2, homeFragment3)


    private val mGeoFenceClient = GeoFenceClient(MVVMBaseApplication.appContext)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //创建围栏广播
        val filter = IntentFilter()
        filter.addAction(GeoBroadCast.fenceaction)
        this.registerReceiver(GeoBroadCast, filter)
        //注册微信
        WeChatRegister.register(this)
    }

    override fun onInit(savedInstanceState: Bundle?) {
//        val type = SDKInitializer.getCoordType()
        mFragments = getFragments
        //需id一致 replace替换fragment
        NavigationUI.setupWithNavController(bottomBarView, navController)
        //在xml中不设置tint的话会默认createDefaultColorStateList导致自定义的图标失效
        bottomBarView.itemIconTintList = null
        initDrag()
        BdLocation2.startLocation
        lifecycle.addObserver(BdLocation2)
        volumeControlStream = AudioManager.STREAM_MUSIC
        //返回后台
        this.onBackPressedDispatcher.addCallback {
            if (navController.currentDestination?.id == R.id.fragment_home) {
                moveTaskToBack(false)
            }
        }
    }

    /**
     * 初始化悬浮按钮
     */
    private fun initDrag() {
        requestPermission()
        val lottieView = layoutInflater.inflate(R.layout.custom_robot, null, false)
        lottieView.setOnClickListener {
            requestPermission(
                this@MultiFragments,
                android.Manifest.permission.RECORD_AUDIO,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) {
                start()
            }
        }
        dragView = SuspensionDragView.Builder().setActivity(this)
            .setSize(dip(100))
            .setDefaultTop(phoneHeight - dip(130))
            .setDefaultLeft(phoneWidth - dip(70))
            .setView(lottieView)
            .setVisible(SpUtils.get(OPEN_ROBOT_SP, true))
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        mGeoFenceClient.removeGeoFence()
        try {
            if (WeChatRegister.broadcastReceiver != null)
                this.unregisterReceiver(WeChatRegister.broadcastReceiver)
            this.unregisterReceiver(GeoBroadCast)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun requestPermission() {
        this.rxRequestPermission(
            android.Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.CAMERA
        ) {
            BdLocation2.startLocation.setDistanceListener {
                navController.navigate(R.id.dialog_smart_tip,
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
        mGeoFenceClient.addGeoFence("沙子营湿地公园", "旅游景点", "北京", 1, " 0001")
        //初始化围栏(在位置回调中先进行移除再添加达到每隔6s回调一次)
        mGeoFenceClient.createPendingIntent(GeoBroadCast.fenceaction)
        mGeoFenceClient.setTriggerCount(Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE)
        mGeoFenceClient.setActivateAction(GeoFenceClient.GEOFENCE_IN)

    }

    private fun start() {
        //如果已经打开了机器人页面点击事件不被触发。
        val label = navController.currentDestination?.label.toString()
        if (label != "asrFragment") {
            navController.navigate(
                R.id.fragment_asr,
                null,
                navOptions {
                    anim {
                        enter = R.anim.alpha_enter
                        exit = R.anim.alpha_exit
                    }
                    launchSingleTop = true
                    popUpTo(R.id.fragment_asr) { inclusive = true }
                })
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //扫码结果
        if (requestCode == SCAN_CODE) {
            if (resultCode != Activity.RESULT_OK) return
            val obj = data?.getParcelableExtra(ScanUtil.RESULT) as HmsScan
            /*  val intent = Intent(this, DisPlayActivity::class.java)
              intent.putExtra("SCAN_RESULT", obj)
              startActivity(intent)*/
            val value = obj.originalValue
            if (value.startsWith("http")) {
                navController.navigate(
                    R.id.fragment_webview_primeval,
                    bundleOf(WEB_VIEW_URL to value),
                    navOption
                )
            } else {
                longToast("无效的二维码..").setGravity(Gravity.CENTER, 0, 0)
            }

        } else if (requestCode == WebViewCameraHelper.TYPE_CAMERA) {
            //ai步道拍照
//            WebCameraHelper.getInstance().onActivityResult(requestCode, resultCode, intent)
            WebViewCameraHelper.onActivityResult(requestCode, resultCode, intent)
        }
    }

    override fun permissionForResult() {
    }
}

