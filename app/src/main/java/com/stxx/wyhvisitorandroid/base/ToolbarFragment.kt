package com.stxx.wyhvisitorandroid.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.gavindon.mvvm_lib.utils.getStatusBarHeight
import com.gyf.immersionbar.ImmersionBar
import com.stxx.wyhvisitorandroid.R
import kotlinx.android.synthetic.main.toolbar.*

/**
 * description:统一标题栏Fragment
 * Created by liNan on  2020/2/17 23:47
 */
abstract class ToolbarFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val mView = layoutInflater.inflate(R.layout.toolbar_title, container, false)
        try {
            val contentFrameLayout = mView.findViewById<FrameLayout>(R.id.flContent)
            val content = layoutInflater.inflate(layoutId, null, false)
            if (content.parent == null) {
                contentFrameLayout?.addView(content)
            }
        } catch (e: Exception) {
        }

        return mView
    }

    /**
     * toolbar 通用的状态栏
     */
    override fun setStatusBar() {
        //使用ImmersionBar直接设置颜色fragment状态栏会跳动一下。
        // 设置状态栏为透明。并不使用fitSystem 动态设置view高度侵占状态栏(使状态栏和contentView 为一体)
        //使用此方式设置状态栏
        //titleBar为状态栏，不是标题栏，要改变颜色不要忘记标题栏的颜色也要改一下
        if (context != null) {
            titleBar.setBackgroundColor(
                ContextCompat.getColor(
                    this.requireContext(),
                    R.color.colorPrimaryYellow
                )
            )
            titleBar.layoutParams.height = getStatusBarHeight(this.context)
            ImmersionBar.with(this)
                .fitsSystemWindows(false)
                .transparentStatusBar()
                .init()
        }

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initToolbar()
    }

    private fun initToolbar() {
        app_tv_Title?.text = this.context?.resources?.getString(toolbarName)
        toolbar_back.setOnClickListener { this.findNavController().navigateUp() }
    }

    @get: StringRes
    abstract val toolbarName: Int


}