package com.stxx.wyhvisitorandroid.view.splash

import android.os.Bundle
import android.widget.ImageView
import androidx.viewpager.widget.ViewPager
import com.gavindon.mvvm_lib.utils.SpUtils
import com.gyf.immersionbar.ImmersionBar
import com.orhanobut.logger.Logger
import com.stxx.wyhvisitorandroid.FIRST_INSTALL
import com.stxx.wyhvisitorandroid.R
import com.stxx.wyhvisitorandroid.base.BaseActivity
import kotlinx.android.synthetic.main.activity_guide.*
import org.jetbrains.anko.startActivity

/**
 * 引导页
 */
class GuideActivity : BaseActivity() {


    override val layoutId: Int = R.layout.activity_guide

    override fun onInit(savedInstanceState: Bundle?) {
        banner_guide_foreground.setEnterSkipViewIdAndDelegate(
            0,
            R.id.tv_guide_skip
        ) {
            startActivity<MultiFragments>()
            SpUtils.put(FIRST_INSTALL, false)
            this.finish()
        }
        banner_guide_background.setData(
            listOf(
                layoutInflater.inflate(
                    R.layout.layout_guide1,
                    null
                ),
                layoutInflater.inflate(
                    R.layout.layout_guide2,
                    null
                ),
                layoutInflater.inflate(
                    R.layout.layout_guide3,
                    null
                )
            )
        )
        banner_guide_background.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                //往到最后一页时再往后滑进入首页
                if (position == banner_guide_background.views.size - 1 && positionOffset == 0f && positionOffsetPixels == 0) {
                    startActivity<MultiFragments>()
                    SpUtils.put(FIRST_INSTALL, false)
                    this@GuideActivity.finish()
                }
            }

            override fun onPageSelected(position: Int) {
            }
        })
    }

    override fun onResume() {
        super.onResume()
        banner_guide_background.setBackgroundResource(android.R.color.white)
    }

    override fun setStatusBar() {
        ImmersionBar.with(this)
            .transparentStatusBar()
            .init()
    }

    override fun permissionForResult() {
    }
}
