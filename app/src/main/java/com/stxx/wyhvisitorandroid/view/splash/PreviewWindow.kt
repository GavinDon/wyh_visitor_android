package com.stxx.wyhvisitorandroid.view.splash

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gavindon.mvvm_lib.base.ViewModelProviders
import com.gavindon.mvvm_lib.utils.SpUtils
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

        if (isFirstInstall) {
            startActivity<GuideActivity>()
            this.finish()
        } else {
            Thread.sleep(1000)
            startActivity<MultiFragments>()
            this.finish()
        }

    }

    private fun getAsrToken() {
        val asrViewModel = ViewModelProviders.of(this).get(BaiDuAsr::class.java)
        asrViewModel.getAuth()
    }
}