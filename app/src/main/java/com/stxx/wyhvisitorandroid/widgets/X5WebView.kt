package com.stxx.wyhvisitorandroid.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.AttributeSet
import com.gavindon.mvvm_lib.widgets.showToast
import com.orhanobut.logger.Logger
import com.tencent.smtt.export.external.interfaces.SslError
import com.tencent.smtt.export.external.interfaces.SslErrorHandler
import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebViewClient


/**
 * description:
 * Created by liNan on 2020/3/25 14:06

 */
class X5WebView : WebView {


//    private val mUrls: Stack<String> = Stack()

    private val mUrls = mutableSetOf<String?>()

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
     * 若没有设置 WebViewClient 则在点击链接之后由系统处理该 url，通常是使用浏览器打开或弹出浏览器选择对话框。
     * 若设置WebViewClient 且该方法返回 true ，则说明由应用的代码处理该 url，WebView 不处理。
     * 若设置WebViewClient 且该方法返回 false，则说明由 WebView 处理该 url，即用 WebView 加载该 url。
     */

    private var lastUrl = ""
    private var isFirst = true
    private val client = object : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            if (url == null) return false
            Logger.i("result=${url}")
            lastUrl = url
            when {
//                https://cloud.keytop.cn/pc/page/payment_confirm.html
                url.startsWith("https://wx.tenpay.com/cgi-bin/mmpayweb-bin/checkmweb?") -> {
                    val headers = HashMap<String, String>()
                    headers["Referer"] = "https://keytop.cn/"
                    view?.loadUrl(url, headers)
                    return true
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
                //当展示支付完成页面时
                (lastUrl.contains("https://cloud.keytop.cn/stcfront/Payment/Success")) -> {
                    if (isFirst) {
                        isFirst = !isFirst
                    } else {
                        goBack()
                    }
                    return false
                }
                //当重定向页面时
                (view?.x5HitTestResult?.extra == null) -> {
                    //转发交给webview自己处理
                    return false
                }
                else -> {
//                    view.loadUrl(url)
                    return false
                }
            }

        }

        override fun onReceivedSslError(p0: WebView?, p1: SslErrorHandler?, p2: SslError?) {
            p1?.proceed()
        }


        override fun onPageFinished(p0: WebView?, p1: String?) {
            //不能写在onStarted
            mUrls.add(p1)
            if (mUrls.size > 1) {
                urlListener?.invoke(mUrls)
            }

        }
    }

    private var urlListener: ((MutableSet<String?>) -> Unit)? = null

    fun addUrlListener(urlStack: ((MutableSet<String?>) -> Unit)) {
        this.urlListener = urlStack
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

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mUrls.clear()
        this.removeAllViews()
        this.clearCache(true)
        this.clearHistory()
        this.destroy()
        Logger.i("result=onDetached")
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