package com.gavindon.mvvm_lib.base

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import com.gavindon.mvvm_lib.utils.SpUtils
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.FormatStrategy
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import java.util.*


/**
 * description:
 * Created by liNan on 2019/12/27 16:49

 */
open class MVVMBaseApplication : Application() {

    private lateinit var activityLifecycle: ActivityLifecycle


    companion object {
        lateinit var appContext: Context
        private val activityStack = Stack<Activity>()
        val instance: MVVMBaseApplication by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { MVVMBaseApplication() }
        fun getCurActivity(): Activity? {
            return activityStack.lastElement()
        }
    }

    override fun onCreate() {
        super.onCreate()
        appContext = this

        /*LOGGER初始化*/
        val formatStrategy: FormatStrategy = PrettyFormatStrategy.newBuilder()
            .showThreadInfo(true)
            .tag("wyh-logger") // (Optional) Global tag for every log. Default PRETTY_LOGGER
            .build()
        Logger.addLogAdapter(AndroidLogAdapter(formatStrategy))

        /*SharedPrefres*/
        SpUtils.init()

        /*管理activity栈*/
        activityLifecycle = ActivityLifecycle()
        registerActivityLifecycleCallbacks(ActivityLifecycle())

       /* RxJavaPlugins.setErrorHandler {
            it.printStackTrace()
        }*/
    }

    class ActivityLifecycle : ActivityLifecycleCallbacks {
        override fun onActivityPaused(p0: Activity) {
        }

        override fun onActivityStarted(p0: Activity) {
        }

        override fun onActivityDestroyed(p0: Activity) {
            activityStack.remove(p0)
        }

        override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {
        }

        override fun onActivityStopped(p0: Activity) {
        }

        override fun onActivityCreated(p0: Activity, p1: Bundle?) {
            activityStack.add(p0)
        }

        override fun onActivityResumed(p0: Activity) {
        }

    }

    override fun onTerminate() {
        super.onTerminate()
        unregisterActivityLifecycleCallbacks(activityLifecycle)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        unregisterActivityLifecycleCallbacks(activityLifecycle)
    }


}