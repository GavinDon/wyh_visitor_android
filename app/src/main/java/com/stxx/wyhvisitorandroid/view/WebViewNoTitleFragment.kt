package com.stxx.wyhvisitorandroid.view

import android.content.Intent
import android.graphics.Color
import android.net.Uri
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
import com.stxx.wyhvisitorandroid.view.helpers.WebCameraHelper
import com.stxx.wyhvisitorandroid.view.helpers.WebViewCameraHelper
import com.tencent.smtt.sdk.ValueCallback
import com.tencent.smtt.sdk.WebChromeClient
import com.tencent.smtt.sdk.WebView
import kotlinx.android.synthetic.main.fragment_webview_notitle.*
import kotlinx.android.synthetic.main.toolbar.*


/**
 * description:提供的webView 不带标题栏需要自已添加一个标题栏
 * Created by liNan on  2020/4/27 11:11
 */
open class WebViewNoTitleFragment : BaseFragment() {
    override val layoutId: Int = R.layout.fragment_webview_notitle

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        x5WebView.webChromeClient = webChromeClient
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
        toolbar_close?.setOnClickListener {
            findNavController().navigateUp()
        }

        x5WebView.addUrlListener {
            toolbar_close?.visibility = if (it.size > 1) View.VISIBLE else View.GONE
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

        override fun onReceivedTitle(p0: WebView?, p1: String?) {
            super.onReceivedTitle(p0, p1)
            val title = p0?.title ?: "详情"
            app_tv_Title?.text = title
            app_tv_Title?.isFocusable = true
        }

        override fun onShowFileChooser(
            p0: WebView?,
            uploadMsg: ValueCallback<Array<Uri>>,
            fileChooserParams: FileChooserParams?
        ): Boolean {
//            WebCameraHelper.getInstance().mUploadCallbackAboveL = uploadMsg
//            WebCameraHelper.getInstance().showOptions(this@WebViewNoTitleFragment.requireActivity())
            WebViewCameraHelper.mUploadCallbackAboveL = uploadMsg
            WebViewCameraHelper.showOptions(this@WebViewNoTitleFragment.requireActivity())
            return true
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