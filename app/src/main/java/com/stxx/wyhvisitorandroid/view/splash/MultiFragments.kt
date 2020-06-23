package com.stxx.wyhvisitorandroid.view.splash

import android.app.Activity
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.addCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.*
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import cn.jpush.android.api.JPushInterface
import com.baidu.mapapi.CoordType
import com.baidu.mapapi.SDKInitializer
import com.baidu.speech.asr.SpeechConstant
import com.gavindon.mvvm_lib.utils.SpUtils
import com.gavindon.mvvm_lib.utils.phoneHeight
import com.gavindon.mvvm_lib.utils.phoneWidth
import com.orhanobut.logger.Logger
import com.stxx.wyhvisitorandroid.R
import com.stxx.wyhvisitorandroid.BUNDLE_SELECT_TAB
import com.stxx.wyhvisitorandroid.OPEN_ROBOT_SP
import com.stxx.wyhvisitorandroid.base.BaseActivity
import com.stxx.wyhvisitorandroid.enums.ScenicMApPointEnum
import com.stxx.wyhvisitorandroid.navOption
import com.stxx.wyhvisitorandroid.view.home.HomeFragment
import com.stxx.wyhvisitorandroid.view.mine.MineFragment
import com.stxx.wyhvisitorandroid.view.scenic.ScenicMapFragment
import com.stxx.wyhvisitorandroid.widgets.SuspensionDragView
import com.tencent.bugly.Bugly
import kotlinx.android.synthetic.main.fragment_multi_root.*
import kotlinx.android.synthetic.main.fragment_webview_notitle.*
import org.jetbrains.anko.dip
import org.jetbrains.anko.toast
import java.util.*

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

    //    private lateinit var input: DigitalDialogInput
/*    private lateinit var chainRecogListener: ChainRecogListener
    private lateinit var myRecognizer: MyRecognizer*/
    private var running: Boolean = false

    var dragView: SuspensionDragView? = null
    private lateinit var mFragments: List<Fragment>
    private val getFragments
        get() = listOf(homeFragment1, homeFragment2, homeFragment3)


    override fun onInit(savedInstanceState: Bundle?) {
//        val type = SDKInitializer.getCoordType()
//        toast(type.name)
        mFragments = getFragments
        //需id一致 replace替换fragment
        NavigationUI.setupWithNavController(bottomBarView, navController)
        //在xml中不设置tint的话会默认createDefaultColorStateList导致自定义的图标失效
        bottomBarView.itemIconTintList = null
        initDrag()
        volumeControlStream = AudioManager.STREAM_MUSIC
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

    private fun requestPermission() {
        requestPermission(
            this,
            android.Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) {}
    }

/*    protected fun startRecognition() {
        running = true
        val params = input.startParams
        params[SpeechConstant.ACCEPT_AUDIO_VOLUME] = true // 强制改为true，否则没有动画效果
        myRecognizer.start(input.startParams)
    }*/


    /**
     * 创建asr listener
     */
    /* override fun onCreate(savedInstanceState: Bundle?) {
         super.onCreate(savedInstanceState)
         chainRecogListener = ChainRecogListener()
         chainRecogListener.addListener(MessageListener())
         myRecognizer = MyRecognizer(this, chainRecogListener)
         myRecognizer.setEventListener(chainRecogListener)
         val p = NavUtils.checkInstallPackage(this)
         Logger.i(p.toString())
     }*/

    private fun start() {
        // params ="accept-audio-data":false,"disable-punctuation":false,"accept-audio-volume":true,"pid":1736
        /* input = DigitalDialogInput(
             myRecognizer,
             chainRecogListener,
             mapOf(
                 "accept-audio-volume" to true,
                 "accept-audio-data" to false,
                 "disable-punctuation" to false,
                 "pid" to "15373"
             )
         )
         CustomBaiduAsrDigitalDialog.setInput(input)
         val intent =
             Intent(this, CustomBaiduAsrDigitalDialog::class.java)
         running = true
         startActivityForResult(intent, 2)*/
        //如果已经打开了机器人页面点击事件不被触发。
        val label = navController.currentDestination?.label.toString()
//                || label != "webViewFragment"
        if (label != "asrFragment") {
            val navBuilder = NavOptionsBuilder()
            navBuilder.launchSingleTop = true
            navBuilder.popUpTo(R.id.fragment_asr) { inclusive = true }

            navController.navigate(
                R.id.fragment_asr,
                null,
                navOptions { popUpTo(R.id.fragment_asr) { inclusive = true } })
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        running = false
        if (requestCode == 2) {
            var message = "对话框的识别结果："
            if (resultCode == Activity.RESULT_OK) {
                val results: ArrayList<*>? = data!!.getStringArrayListExtra("results")
                if (results != null && results.size > 0) {
                    message += results[0]
                }
                when {
                    message.contains("百科") -> {
                        navController.navigate(R.id.fragment_vegetation_wiki)
                    }
                    message.contains("景点") -> {
                        navController.navigate(R.id.fragment_scenic)
                    }
                    message.contains("历史") -> {
                        navController.navigate(R.id.fragment_search)
                    }
                    message.contains("返回") -> {
                        navController.navigateUp()
                    }
                    message.contains("厕所") -> {
                        navController.navigate(
                            R.id.fragment_scenic,
                            bundleOf(BUNDLE_SELECT_TAB to ScenicMApPointEnum.TOILET.ordinal)
                        )
                    }
                    message.contains("卫生间") -> {
                        navController.navigate(
                            R.id.fragment_scenic,
                            bundleOf(BUNDLE_SELECT_TAB to ScenicMApPointEnum.TOILET.ordinal)
                        )
                    }
                    message.contains("服务区") -> {
                        navController.navigate(
                            R.id.fragment_scenic,
                            bundleOf(BUNDLE_SELECT_TAB to ScenicMApPointEnum.SERVICE_AREA.ordinal)
                        )
                    }
                    message.contains("商铺") -> {
                        navController.navigate(
                            R.id.fragment_scenic,
                            bundleOf(BUNDLE_SELECT_TAB to ScenicMApPointEnum.SHOP.ordinal)
                        )
                    }
                    message.contains("景区植物") -> {
                        navController.navigate(
                            R.id.fragment_scenic,
                            bundleOf(BUNDLE_SELECT_TAB to ScenicMApPointEnum.SCENIC_PLANT.ordinal)
                        )
                    }


                    else -> {
                        try {
                            val assetFileDescriptor = resources.assets.openFd("asr.mp3")
                            val player = MediaPlayer()
                            if (!player.isPlaying) {
                                player.setDataSource(
                                    assetFileDescriptor.fileDescriptor,
                                    assetFileDescriptor.startOffset,
                                    assetFileDescriptor.length
                                )
                                player.prepare()
                                player.start()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }
                }

            } else {
                message += "没有结果"
            }
//            MyLogger.info(message)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
    }

    override fun permissionForResult() {
    }
}

