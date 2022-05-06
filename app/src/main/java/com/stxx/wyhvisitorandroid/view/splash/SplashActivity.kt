package com.stxx.wyhvisitorandroid.view.splash

import android.os.Bundle
import android.widget.ImageView
import com.gavindon.mvvm_lib.net.BR
import com.gavindon.mvvm_lib.net.RxScheduler
import com.gavindon.mvvm_lib.utils.SpUtils
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.gson.gsonDeserializer
import com.github.kittinunf.fuel.rx.rxResponseObject
import com.gyf.immersionbar.ImmersionBar
import com.orhanobut.logger.Logger
import com.stxx.wyhvisitorandroid.*
import com.stxx.wyhvisitorandroid.base.BaseActivity
import com.stxx.wyhvisitorandroid.bean.MapDateResp
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_splash.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
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
            splash.setData(null, ImageView.ScaleType.CENTER_CROP, R.mipmap.guide1)
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
        getMapDate()  //瓦片图更新时间
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

    private fun getMapDate() {
        val dis = Fuel.get(ApiService.CLEAR_MAP)
            .rxResponseObject(gsonDeserializer<BR<MapDateResp>>())
            .subscribe({
                Logger.i(it.data.code)
                if (it.code == 0) {
                    val mapDate = SpUtils.get(MAP_DATE_SP, "")
                    if (mapDate.isNotEmpty()) {
                        //如果地图更新日期不相等则清除地图缓存
                        if (mapDate != it.data.code) {
                            SpUtils.put(MAP_IS_CLEAR_SP, true)
                        } else {
                            SpUtils.put(MAP_IS_CLEAR_SP, false)
                        }
                    } else {
                        //新版本增加接口。如果没有更新地图日期，则直接清除地图缓存
                        SpUtils.put(MAP_IS_CLEAR_SP, true)
                    }
                    SpUtils.put(MAP_DATE_SP, it.data.code)
                }
            }, {
                Logger.i(it.localizedMessage)
            })
        compositeDisposable.add(dis)
    }
}
