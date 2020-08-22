package com.stxx.wyhvisitorandroid.view

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.navigation.fragment.findNavController
import com.gavindon.mvvm_lib.utils.getStatusBarHeight
import com.gyf.immersionbar.ImmersionBar
import com.huawei.hms.hmsscankit.ScanUtil
import com.huawei.hms.ml.scan.HmsScan
import com.stxx.wyhvisitorandroid.R
import com.stxx.wyhvisitorandroid.WebViewUrl
import com.stxx.wyhvisitorandroid.base.BaseFragment
import com.tencent.smtt.sdk.WebChromeClient
import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebViewClient
import kotlinx.android.synthetic.main.fragment_webview_primeval.*
import kotlinx.android.synthetic.main.toolbar.*

/**
 * description:什么也不干涉，直接加载
 * Created by liNan on  2020/4/27 11:11
 */
class ScanResultFragment : BaseFragment() {
    override val layoutId: Int = R.layout.fragment_webview_primeval

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)

        try {
            val url = arguments?.getString("url")
            x5WebView.webChromeClient = webChromeClient
            x5WebView.webViewClient = webViewClient
            if (url?.startsWith("http") == true) {
                x5WebView.loadUrl(url)
            } else {
                x5WebView.loadUrl("${WebViewUrl.WEB_BASE}${arguments?.getString("url")}")
            }
            requireActivity().onBackPressedDispatcher.addCallback(this, true) {
                if (x5WebView?.canGoBack() == true) {
                    x5WebView?.goBack()
                } else {
                    findNavController().navigateUp()
                }
            }
        } catch (e: Exception) {

        }
    }

    override fun setStatusBar() {
        if (context != null) {
            titleBar?.setBackgroundColor(Color.WHITE)
            frame_layout_title?.setBackgroundColor(Color.WHITE)
            toolbar_back?.setOnClickListener {
                if (x5WebView?.canGoBack() == true) {
                    x5WebView?.goBack()
                } else {
                    findNavController().navigateUp()
                }
            }
            titleBar?.layoutParams?.height = getStatusBarHeight(this.context)
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

    private val webViewClient = object : WebViewClient() {
        override fun onPageFinished(view: WebView?, p1: String?) {
            super.onPageFinished(view, p1)
            val title = view?.title ?: "详情"
            app_tv_Title?.text = title
            app_tv_Title?.isFocusable = true

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val parent = x5WebView.parent
        if (parent is ViewGroup) {
            parent.removeView(x5WebView)
            x5WebView.removeAllViews()
            x5WebView.destroy()
        }
    }
}