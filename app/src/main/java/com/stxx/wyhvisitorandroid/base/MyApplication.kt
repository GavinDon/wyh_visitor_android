package com.stxx.wyhvisitorandroid.base

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import androidx.multidex.MultiDex
import cn.jpush.android.api.JPushInterface
import com.baidu.mapapi.SDKInitializer
import com.gavindon.mvvm_lib.base.MVVMBaseApplication
import com.gavindon.mvvm_lib.net.HttpFrame
import com.gavindon.mvvm_lib.net.HttpManager
import com.quyuanfactory.artmap.ArtMap
import com.stxx.wyhvisitorandroid.BuildConfig
import com.tencent.bugly.Bugly
import com.tencent.bugly.beta.Beta
import com.tencent.smtt.export.external.TbsCoreSettings
import com.tencent.smtt.sdk.QbSdk
import io.reactivex.plugins.RxJavaPlugins

/**
 * description:
 * Created by liNan on 2020/1/14 16:31

 */
class MyApplication : MVVMBaseApplication() {


    override fun onCreate() {
        super.onCreate()
        /**
         *初始化http
         */
        HttpManager.instance.apply {
            initHttp(HttpFrame.FUEL)
            baseUrl = BuildConfig.baseUrl
        }.build()

        //百度地图
        SDKInitializer.initialize(this)
        RxJavaPlugins.setErrorHandler { }
        //腾讯bugly
        Bugly.init(this, "4fa626abc1", false)
        Bugly.setIsDevelopmentDevice(this, true)
        //腾讯x5
        initX5()
        //极光推送
        JPushInterface.setDebugMode(true)
        JPushInterface.init(this)
        ArtMap.Init(this)
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(base)
        Beta.installTinker()

    }

    private fun initX5() {
        //多进程优化
        val map = HashMap<String, Any>()
        map[TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER] = true
        map[TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE] = true
        QbSdk.initTbsSettings(map)

        QbSdk.initX5Environment(this, null)

    }


}