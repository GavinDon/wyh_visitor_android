package com.stxx.wyhvisitorandroid.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Build
import android.util.AttributeSet
import android.view.View
import com.gavindon.mvvm_lib.widgets.ToastUtil
import com.gavindon.mvvm_lib.widgets.showToast
import com.tencent.smtt.sdk.QbSdk
import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebViewClient


/**
 * description:
 * Created by liNan on 2020/3/25 14:06

 */
class X5WebView : WebView {
    constructor(p0: Context?) : super(p0) {
        setBackgroundColor(85621)
    }

    constructor(p0: Context?, p1: AttributeSet?) : super(p0, p1) {
        this.webViewClient = client
        // this.setWebChromeClient(chromeClient);
        // WebStorage webStorage = WebStorage.getInstance();
        removeJavascriptInterface("searchBoxJavaBridge_")
        removeJavascriptInterface("accessibility")
        removeJavascriptInterface("accessibilityTraversal")
        initWebViewSettings()
        this.view.isClickable = true
    }


    /**
     * 防止加载网页时调起系统浏览器
     */
    private val client = object : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            view?.loadUrl(url)
            return true
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebViewSettings() {
        this.settings.apply {
            javaScriptEnabled = true
            javaScriptCanOpenWindowsAutomatically = true
            setSupportZoom(true)
            setSupportMultipleWindows(true)
            allowFileAccess = false
            useWideViewPort = true
            setAppCacheEnabled(true)
            domStorageEnabled = true
        }
    }

    /*override fun drawChild(canvas: Canvas?, child: View?, drawingTime: Long): Boolean {
        val ret = super.drawChild(canvas, child, drawingTime)
        canvas!!.save()
        val paint = Paint()
        paint.color = 0x7fff0000
        paint.textSize = 24f
        paint.isAntiAlias = true
        if (x5WebViewExtension != null) {
            canvas.drawText(
                this.context.packageName + "-pid:"
                        + android.os.Process.myPid(), 10f, 50f, paint
            )
            canvas.drawText(
                "X5  Core:" + QbSdk.getTbsVersion(this.context), 10f,
                100f, paint
            )
        } else {
            canvas.drawText(
                this.context.packageName + "-pid:"
                        + android.os.Process.myPid(), 10f, 50f, paint
            )
            canvas.drawText("Sys Core", 10f, 100f, paint)
        }
        canvas.drawText(Build.MANUFACTURER, 10f, 150f, paint)
        canvas.drawText(Build.MODEL, 10f, 200f, paint)
        canvas.restore()
        this.context?.showToast("sys core")
        return ret

    }*/
}