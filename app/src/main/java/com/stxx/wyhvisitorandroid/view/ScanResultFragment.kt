package com.stxx.wyhvisitorandroid.view

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.navigation.fragment.findNavController
import com.gavindon.mvvm_lib.utils.getStatusBarHeight
import com.gyf.immersionbar.ImmersionBar
import com.stxx.wyhvisitorandroid.R
import com.stxx.wyhvisitorandroid.WebViewUrl
import com.stxx.wyhvisitorandroid.base.BaseFragment
import com.stxx.wyhvisitorandroid.view.helpers.WebCameraHelper
import com.stxx.wyhvisitorandroid.view.helpers.WebViewCameraHelper
import com.tencent.smtt.sdk.ValueCallback
import com.tencent.smtt.sdk.WebChromeClient
import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebViewClient
import kotlinx.android.synthetic.main.fragment_webview_primeval.progressBar
import kotlinx.android.synthetic.main.fragment_webview_primeval.x5WebView
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
//            x5WebView.webViewClient = webViewClient
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
            toolbar_close?.setOnClickListener {
                findNavController().navigateUp()
            }
            x5WebView.addUrlListener {
                //set集合中first数据和WebView当前展示的url相同则说明当前在第一页。应该隐藏关闭按钮
                if (x5WebView.url == it.first()) {
                    toolbar_close?.visibility = View.GONE
                } else {
                    toolbar_close?.visibility = View.VISIBLE
                }
            }
        } catch (e: Exception) {
            println(e.stackTrace)
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

        override fun onShowFileChooser(
            p0: WebView?,
            uploadMsg: ValueCallback<Array<Uri>>,
            fileChooserParams: WebChromeClient.FileChooserParams?
        ): Boolean {
            WebViewCameraHelper.mUploadCallbackAboveL = uploadMsg
            WebViewCameraHelper.showOptions(this@ScanResultFragment.requireActivity())
            return true
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        WebCameraHelper.getInstance().onActivityResult(requestCode, resultCode, intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val parent = x5WebView.parent
        WebViewCameraHelper.cancelChoose()
        if (parent is ViewGroup) {
            parent.removeView(x5WebView)
            x5WebView.removeAllViews()
            x5WebView.destroy()
        }
    }
}