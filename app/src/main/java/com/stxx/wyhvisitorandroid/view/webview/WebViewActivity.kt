package com.stxx.wyhvisitorandroid.view.webview

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.stxx.wyhvisitorandroid.R
import com.stxx.wyhvisitorandroid.WEB_VIEW_URL
import com.tencent.smtt.sdk.WebChromeClient
import com.tencent.smtt.sdk.WebView
import kotlinx.android.synthetic.main.activity_web_view.*

class WebViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)
        onInit()
    }

    fun onInit() {
        x5WebView.webChromeClient = webChromeClient
        val url = intent.getStringExtra(WEB_VIEW_URL) ?: ""
        x5WebView.loadUrl(url)
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
//            toolbar?.title = p1
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

    override fun onBackPressed() {
        if (x5WebView.canGoBack()) {
            x5WebView.goBack()
        } else {
            finish()
        }
    }

}