package com.stxx.wyhvisitorandroid.view.splash

import SmartTipDialog
import android.os.Bundle
import android.widget.ImageView
import com.gavindon.mvvm_lib.net.RxScheduler
import com.gavindon.mvvm_lib.utils.SpUtils
import com.gyf.immersionbar.ImmersionBar
import com.stxx.wyhvisitorandroid.FIRST_INSTALL
import com.stxx.wyhvisitorandroid.R
import com.stxx.wyhvisitorandroid.base.BaseActivity
import com.stxx.wyhvisitorandroid.mplusvm.SearchVm
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_splash.*
import org.jetbrains.anko.startActivity
import java.util.concurrent.TimeUnit

/**
 * 闪屏页面
 */
class SplashActivity : BaseActivity() {

    private val downTime = 3


    private val compositeDisposable = CompositeDisposable()
    override val layoutId: Int = R.layout.activity_splash


    override fun onInit(savedInstanceState: Bundle?) {
        val isFirstInstall = SpUtils.get(FIRST_INSTALL, true)
        requestPermiss()
        if (isFirstInstall) {
            startActivity<GuideActivity>()
            this.finish()
        } else {
            splash.setData(null, ImageView.ScaleType.CENTER_CROP, R.mipmap.splash_pic)
            compositeDisposable.add(
                Observable.interval(1, TimeUnit.SECONDS)
                    .compose(RxScheduler.applyScheduler())
                    .map {
                        downTime - it
                    }.take(downTime + 1L)
                    .subscribe({
                        if (it == 0L) {
                            //倒计时完成进入主界面
                            startActivity<MultiFragments>()
                            this.finish()
                        } else {
                            tvSplashJump.setOnClickListener {
                                startActivity<MultiFragments>()
                                this.finish()
                            }
                            tvSplashJump.text = ("跳过${it}s")
                        }

                    }, {

                    })
            )
        }

    }

    private fun requestPermiss() {
        requestPermission(
            this,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_WIFI_STATE
        ) {}

    }

    override fun setStatusBar() {
        ImmersionBar.with(this)
            .transparentStatusBar()
            .init()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

    override fun permissionForResult() {
    }

    /**
     * 生成用户id
     */
    fun generateOnlyId() {

    }
}
