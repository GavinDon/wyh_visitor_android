package com.stxx.wyhvisitorandroid.view.mine

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import cn.jpush.android.api.JPushInterface
import cn.jpush.android.api.JPushInterface.isPushStopped
import com.gavindon.mvvm_lib.base.MVVMBaseApplication
import com.gavindon.mvvm_lib.utils.NotificationUtil
import com.gavindon.mvvm_lib.utils.SpUtils
import com.gavindon.mvvm_lib.utils.clearCache.LinCleanData
import com.gavindon.mvvm_lib.utils.getStatusBarHeight
import com.gavindon.mvvm_lib.utils.getVersionName
import com.gavindon.mvvm_lib.widgets.TipDialog
import com.gyf.immersionbar.ImmersionBar
import com.stxx.wyhvisitorandroid.OPEN_ROBOT_SP
import com.stxx.wyhvisitorandroid.R
import com.stxx.wyhvisitorandroid.base.ToolbarFragment
import com.stxx.wyhvisitorandroid.navOption
import com.stxx.wyhvisitorandroid.view.splash.MultiFragments
import com.tencent.bugly.beta.Beta
import kotlinx.android.synthetic.main.fragment_setting.*
import kotlinx.android.synthetic.main.toolbar.*
import org.jetbrains.anko.support.v4.toast

/**
 * description: 设置
 * Created by liNan on  2020/4/29 19:35
 */
class SettingFragment : ToolbarFragment() {


    override val toolbarName: Int = R.string.setting

    override val layoutId: Int = R.layout.fragment_setting

    override fun setStatusBar() {
        if (context != null) {
            titleBar.setBackgroundColor(Color.WHITE)
            frame_layout_title.setBackgroundColor(Color.WHITE)
            titleBar.layoutParams.height = getStatusBarHeight(this.context)
            ImmersionBar.with(this)
                .fitsSystemWindows(false)
                .statusBarDarkFont(true)
                .transparentStatusBar()
                .init()
        }
        ImmersionBar.with(this)
            .statusBarColor(android.R.color.white)
            .init()
    }

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        clearCacheData()
        checkVersion()
        toggleRobot()
        flexAboutUs.setOnClickListener {
            findNavController().navigate(R.id.fragment_about, null, navOption)
        }
    }

    override fun onResume() {
        super.onResume()
        toggleNotify()
    }

    /**
     * 清除缓存
     */
    private fun clearCacheData() {
        tvCacheSize.text = LinCleanData.getAllCacheSize(this.context)
        val tvCacheSize = tvCacheSize
        flexClearCache.setOnClickListener {
            TipDialog().builds {
                message = "确认要清除缓存?"
                confirm {
                    LinCleanData.clearAllCache(this.context)
                    tvCacheSize.text = LinCleanData.getAllCacheSize(this.context)
                }
                cancel("取消")
            }.show(this.requireFragmentManager(), "setting")
        }

    }

    /**
     * 开启/关闭通知
     */
    private fun toggleNotify() {
        //如果已经打开了通知权限
        switchCompatPush.isChecked = NotificationUtil.isNotifyEnable()
        switchCompatPush.setOnClickListener {
            NotificationUtil.settingNotify()
        }
    }

    /*   */
    /**
     * 打开/关闭 推送
     *//*
    private fun onOffPush() {
        val isStopPush = isPushStopped(MVVMBaseApplication.appContext)
        switchCompatPush.isChecked = !isStopPush
        switchCompatPush.setOnClickListener {
            if (switchCompatPush.isChecked) {
                JPushInterface.resumePush(MVVMBaseApplication.appContext)
            } else {
                JPushInterface.stopPush(MVVMBaseApplication.appContext)
            }
        }
    }*/


    /**
     * 检查是否有新版本
     */
    private fun checkVersion() {
        if (!this.isDetached) {
            tvVersionNum.text = getVersionName()
            flexCheckVersion.setOnClickListener {
                Beta.checkUpgrade(true, false)
            }
        }
    }

    private fun toggleRobot() {
        //是否打开机器人按钮默认打开
        switchToggleRobot.setOnClickListener {
            //true 为打开机器人
            val isOpenRobot = SpUtils.get(OPEN_ROBOT_SP, true)
            val dragView = (activity as MultiFragments).dragView?.dragView
            //如果已经打开机器人则切换为隐藏
            if (isOpenRobot) {
                //隐藏机器人
                SpUtils.put(OPEN_ROBOT_SP, false)
                dragView?.visibility = View.GONE
            } else {
                //打开机器人
                SpUtils.put(OPEN_ROBOT_SP, true)
                dragView?.visibility = View.VISIBLE
            }
        }
        //改变Switch选中状态(由机器人是否显示来控制状态)
        switchToggleRobot.isChecked = SpUtils.get(OPEN_ROBOT_SP, true)
    }

}