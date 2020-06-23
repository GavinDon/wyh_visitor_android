package com.stxx.wyhvisitorandroid.transformer

import android.view.View
import androidx.viewpager2.widget.ViewPager2

/**
 * description:
 * Created by liNan on 2020/1/20 17:01

 */
class ScaleInOutTransformer : ViewPager2.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        page.pivotX = (if (position < 0) 0 else page.width).toFloat()
        page.pivotY = page.height / 2f
        val scale = if (position < 0) 1f + position else 1f - position
        page.scaleX = scale
        page.scaleY = scale
    }

}