package com.stxx.wyhvisitorandroid.transformer

import android.view.View
import androidx.viewpager2.widget.ViewPager2


/**
 * description:
 * Created by liNan on 2020/1/15 9:34

 */
class FlipHorizontalTransformer : ViewPager2.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        val rotation = 180f * position
        if (position > -0.5f && position < 0.5f) {
            page.visibility = View.VISIBLE
        } else {
            page.visibility = View.INVISIBLE
        }
        page.alpha = if (rotation > 90f || rotation < -90f) 0f else 1f
        page.pivotX = page.width * 0.5f
        page.pivotY = page.height * 0.5f
        page.rotationY = rotation
    }
}