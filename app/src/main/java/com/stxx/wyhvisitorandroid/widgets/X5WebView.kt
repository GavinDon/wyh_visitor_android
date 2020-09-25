package com.stxx.wyhvisitorandroid.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.AttributeSet
import com.gavindon.mvvm_lib.utils.getStatusBarHeight
import com.gavindon.mvvm_lib.widgets.showToast
import com.orhanobut.logger.Logger
import com.tencent.smtt.export.external.interfaces.SslError
import com.tencent.smtt.export.external.interfaces.SslErrorHandler
import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebViewClient
import org.jetbrains.anko.dip


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
            Logger.i(url.toString())
            if (url == null) return false
            //调用微信或者支付宝支付 在header中添加refrer
            when {
//                https://cloud.keytop.cn/pc/page/payment_confirm.html
                url.startsWith("https://wx.tenpay.com/cgi-bin/mmpayweb-bin/checkmweb?") -> {
                    val headers = HashMap<String, String>()
                    headers["Referer"] = "https://keytop.cn/"
                    view?.loadUrl(url, headers)
                }
                //第一步添加ref 再跳转支付
                url.startsWith("weixin://wap/pay?") || url.startsWith("http://weixin/wap/pay") -> {
                    return try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        view?.context?.startActivity(intent)
                        true
                    } catch (e: Exception) {
                        view?.context?.showToast("未安装微信")
                        false
                    }
                }
                //支付宝支付
                url.contains("alipays://platformapi") -> {
                    return try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        view?.context?.startActivity(intent)
                        true
                    } catch (e: Exception) {
                        view?.context?.showToast("未安装支付宝")
                        false
                    }
                }
                //停车场没有标题  动态设置margin
                url.startsWith("http://s.keytop.cn/wewm16") -> {
                    view?.loadUrl(url)
                    val lp = view?.layoutParams as LayoutParams
                    lp.topMargin = dip(44).plus(getStatusBarHeight(view.context))
                    view.layoutParams = lp
                }
                else -> {
                    view?.loadUrl(url)
                }
            }
            return true
        }

        override fun onReceivedSslError(p0: WebView?, p1: SslErrorHandler?, p2: SslError?) {
            p1?.proceed()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebViewSettings() {
        this.settings.apply {
            javaScriptEnabled = true
            javaScriptCanOpenWindowsAutomatically = true
            setSupportZoom(true)
            setSupportMultipleWindows(false)
            allowFileAccess = true
            useWideViewPort = true
            setAppCacheEnabled(true)
            setAppCachePath(context.externalCacheDir?.path + "webview")
            setGeolocationEnabled(true)
            domStorageEnabled = true
            useWideViewPort = true
            setAllowFileAccessFromFileURLs(true)
            setAllowUniversalAccessFromFileURLs(true)
            allowContentAccess = true
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