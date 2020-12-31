package com.stxx.wyhvisitorandroid.view

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.gavindon.mvvm_lib.utils.getStatusBarHeight
import com.gyf.immersionbar.ImmersionBar
import com.stxx.wyhvisitorandroid.R
import com.stxx.wyhvisitorandroid.WEB_VIEW_URL
import com.stxx.wyhvisitorandroid.WebViewUrl
import com.stxx.wyhvisitorandroid.base.BaseActivity
import com.tencent.smtt.sdk.WebChromeClient
import com.tencent.smtt.sdk.WebView
import kotlinx.android.synthetic.main.activity_push_receive.*
import org.jetbrains.anko.dip

class PushReceiveActivity : BaseActivity() {

    override val layoutId: Int = R.layout.activity_push_receive

    override fun onInit(savedInstanceState: Bundle?) {
        x5WebView.webChromeClient = webChromeClient
        val url = intent?.getStringExtra(WEB_VIEW_URL)
        if (url?.startsWith("http") == true) {
            x5WebView.loadUrl(url)
        } else {
            x5WebView.loadUrl("${WebViewUrl.WEB_BASE}${intent?.getStringExtra(WEB_VIEW_URL)}")
        }
    }

    override fun permissionForResult() {
    }

    override fun setStatusBar() {
        titleBar.setBackgroundColor(Color.WHITE)
        frame_layout_title.setBackgroundColor(Color.WHITE)
        titleBar.layoutParams.height = getStatusBarHeight(this)
        //H5标题栏高度固定为44
        val h5Offset = dip(56) - 45
        val lp = x5WebView.layoutParams as FrameLayout.LayoutParams
        lp.topMargin = h5Offset
        x5WebView.layoutParams = lp
        toolbar_back.setOnClickListener {
            this.finish()
        }
        ImmersionBar.with(this)
            .fitsSystemWindows(false)
            .statusBarDarkFont(true)
            .transparentStatusBar()
            .init()
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
            val title = p0?.title ?: "详情"
            app_tv_Title?.text = title
            app_tv_Title?.isFocusable = true
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        val parent = x5WebView.parent
        if (parent is ViewGroup) {
            parent.removeView(x5WebView)
            x5WebView.removeAllViews()
            x5WebView.destroy()
        }
    }
}