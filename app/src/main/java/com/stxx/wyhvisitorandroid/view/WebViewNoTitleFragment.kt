package com.stxx.wyhvisitorandroid.view

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.gavindon.mvvm_lib.utils.getStatusBarHeight
import com.gyf.immersionbar.ImmersionBar
import com.stxx.wyhvisitorandroid.R
import com.stxx.wyhvisitorandroid.WebViewUrl
import com.stxx.wyhvisitorandroid.base.BaseFragment
import com.tencent.smtt.sdk.WebChromeClient
import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebViewClient
import kotlinx.android.synthetic.main.fragment_webview.*
import kotlinx.android.synthetic.main.fragment_webview_notitle.*
import kotlinx.android.synthetic.main.fragment_webview_notitle.progressBar
import kotlinx.android.synthetic.main.fragment_webview_notitle.x5WebView
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.toolbar.app_tv_Title
import kotlinx.android.synthetic.main.toolbar.frame_layout_title
import kotlinx.android.synthetic.main.toolbar.titleBar
import kotlinx.android.synthetic.main.toolbar.toolbar_back

/**
 * description:提供的webView 不带标题栏需要自已添加一个标题栏
 * Created by liNan on  2020/4/27 11:11
 */
class WebViewNoTitleFragment : BaseFragment() {
    override val layoutId: Int = R.layout.fragment_webview_notitle

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        x5WebView.webChromeClient = webChromeClient
        x5WebView.webViewClient = webViewClient
        val url = arguments?.getString("url")
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
    }

    override fun setStatusBar() {
        if (context != null) {
            titleBar?.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white
                )
            )
            frame_layout_title?.setBackgroundColor(Color.WHITE)
            toolbar_back.setOnClickListener {
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