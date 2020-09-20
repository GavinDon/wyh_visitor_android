package com.stxx.wyhvisitorandroid.view.mine

import android.os.Bundle
import com.stxx.wyhvisitorandroid.R
import com.stxx.wyhvisitorandroid.base.ToolbarFragment

/**
 * description:
 * Created by liNan on  2020/9/20 10:40
 */
class OrderListFragment : ToolbarFragment() {
    override val toolbarName: Int = R.string.str_my_order
    override val layoutId: Int = R.layout.comment_recyclerview

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        mStatusView?.showEmpty()

    }

/*    override fun setStatusBar() {
        frame_layout_title?.setBackgroundColor(Color.WHITE)
        titleBar?.setBackgroundColor(Color.WHITE)
        titleBar?.layoutParams?.height = getStatusBarHeight(this.requireContext())
        ImmersionBar.with(this)
            .fitsSystemWindows(false)
            .statusBarDarkFont(true)
            .transparentStatusBar()
            .init()
    }*/
}