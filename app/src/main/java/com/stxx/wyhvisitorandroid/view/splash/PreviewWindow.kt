package com.stxx.wyhvisitorandroid.view.splash

import SmartTipDialog
import android.app.ActivityManager
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.gavindon.mvvm_lib.base.ViewModelProviders
import com.gavindon.mvvm_lib.utils.SpUtils
import com.stxx.wyhvisitorandroid.AGREE_PROTOCOL
import com.stxx.wyhvisitorandroid.FIRST_INSTALL
import com.stxx.wyhvisitorandroid.mplusvm.BaiDuAsr
import org.jetbrains.anko.startActivity

/**
 * description:
 * Created by liNan on  2020/5/25 14:11
 */
class PreviewWindow : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getAsrToken()
        onInit()
    }

    fun onInit() {
        val isFirstInstall = SpUtils.get(FIRST_INSTALL, true)
        val isAgreeProto = SpUtils.get(AGREE_PROTOCOL, false)

        if (!isAgreeProto) {
            val dialog = SmartTipDialog()
            dialog.show(supportFragmentManager, "protocol")
        } else {
            if (isFirstInstall) {
                startActivity<GuideActivity>()
                this.finish()
            } else {
                Thread.sleep(1000)
                startActivity<SplashActivity>()
                this.finish()
            }
        }


    }


    private fun getAsrToken() {
        val asrViewModel = ViewModelProviders.of(this).get(BaiDuAsr::class.java)
        asrViewModel.getAuth()
    }
}