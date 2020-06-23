package com.stxx.wyhvisitorandroid.view.mine

import android.os.Bundle
import com.stxx.wyhvisitorandroid.R
import com.stxx.wyhvisitorandroid.base.ToolbarFragment

/**
 * description:关于
 * Created by liNan on  2020/5/21 11:07
 */
class AboutFragment : ToolbarFragment() {
    override val toolbarName: Int = R.string.about
    override val layoutId: Int = R.layout.fragment_about

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
    }
}