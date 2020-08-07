package com.stxx.wyhvisitorandroid.view

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.webkit.JavascriptInterface
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.gavindon.mvvm_lib.utils.getStatusBarHeight
import com.gyf.immersionbar.ImmersionBar
import com.orhanobut.logger.Logger
import com.stxx.wyhvisitorandroid.R
import com.stxx.wyhvisitorandroid.WEB_VIEW_TITLE
import com.stxx.wyhvisitorandroid.WEB_VIEW_URL
import com.stxx.wyhvisitorandroid.WebViewUrl
import com.stxx.wyhvisitorandroid.base.BaseFragment
import com.stxx.wyhvisitorandroid.view.splash.MultiFragments
import com.tencent.smtt.sdk.WebChromeClient
import com.tencent.smtt.sdk.WebView
import kotlinx.android.synthetic.main.fragment_webview.*
import java.util.*


/**
 * description: 提供的webView带标题栏
 * 自定义title 遮住webView title
 * Created by liNan on  2020/4/15 14:18
 */
class WebViewFragment : BaseFragment() {


    override val layoutId: Int = R.layout.fragment_webview

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        x5WebView.addJavascriptInterface(JavaInterfaceClose(), "android")
        x5WebView.webChromeClient = webChromeClient
        val url = arguments?.getString(WEB_VIEW_URL)
        if (url?.startsWith("http") == true) {
            x5WebView.loadUrl(url)
        } else {
            x5WebView.loadUrl("${WebViewUrl.WEB_BASE}${url}")
            Logger.i("${WebViewUrl.WEB_BASE}${url}")
        }
        //获取title
        val title = arguments?.get(WEB_VIEW_TITLE)
        val strTitle = if (title is String) {
            title
        } else {
            getString(arguments?.getInt(WEB_VIEW_TITLE) ?: R.string.app_name)
        }
        app_tv_Title.text = strTitle

        toolbar_back.setOnClickListener {
            if (x5WebView?.canGoBack() == true) {
                x5WebView?.goBack()
            } else {
                findNavController().navigateUp()
            }
        }
        (this.activity as MultiFragments).dragView?.dragView?.visibility = View.GONE

        requireActivity().onBackPressedDispatcher.addCallback(this, true) {
            if (x5WebView?.canGoBack() == true) {
                x5WebView?.goBack()
            } else {
                findNavController().navigateUp()
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        (this.activity as MultiFragments).dragView?.dragView?.visibility = View.VISIBLE
    }

    /**
     * toolbar 通用的状态栏
     */
    override fun setStatusBar() {
        //使用ImmersionBar直接设置颜色fragment状态栏会跳动一下。
        // 设置状态栏为透明。并不使用fitSystem 动态设置view高度侵占状态栏(使状态栏和contentView 为一体)
        //使用此方式设置状态栏
        if (context != null) {
            titleBar.setBackgroundColor(
                ContextCompat.getColor(
                    this.requireContext(),
                    R.color.white
                )
            )
            frame_layout_title.setBackgroundColor(Color.WHITE)
            titleBar.layoutParams.height = getStatusBarHeight(this.context)
            ImmersionBar.with(this)
                .fitsSystemWindows(false)
                .statusBarDarkFont(true)
                .transparentStatusBar()
                .init()
        }

    }


    private val webChromeClient = object : WebChromeClient() {
        override fun onProgressChanged(p0: WebView?, p1: Int) {
            progressBar?.visibility = View.VISIBLE
            progressBar?.progress = p1
            if (p1 == 100) {
                progressBar?.visibility = View.GONE
                progressBar?.progress = 0
            }
            super.onProgressChanged(p0, p1)
        }

    }

    inner class JavaInterfaceClose {
        @JavascriptInterface
        fun closeH5() {
            findNavController().navigateUp()
        }
    }
}