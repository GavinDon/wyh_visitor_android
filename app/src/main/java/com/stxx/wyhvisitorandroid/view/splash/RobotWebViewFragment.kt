package com.stxx.wyhvisitorandroid.view.splash

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import com.gavindon.mvvm_lib.utils.getStatusBarHeight
import com.gyf.immersionbar.ImmersionBar
import com.stxx.wyhvisitorandroid.R
import com.stxx.wyhvisitorandroid.WEB_VIEW_TITLE
import com.stxx.wyhvisitorandroid.WEB_VIEW_URL
import com.stxx.wyhvisitorandroid.WebViewUrl
import com.stxx.wyhvisitorandroid.base.BaseFragment
import com.stxx.wyhvisitorandroid.view.splash.MultiFragments
import com.tencent.smtt.sdk.WebChromeClient
import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebViewClient
import kotlinx.android.synthetic.main.fragment_webview.*

/**
 * description: 提供的webView带标题栏
 * 自定义title 遮住webView title
 * Created by liNan on  2020/4/15 14:18
 */
class RobotWebViewFragment : BaseFragment() {


    override val layoutId: Int = R.layout.fragment_webview


    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)

        x5WebView.loadUrl("file:///android_asset/hx_robot.html")
        x5WebView.webChromeClient = webChromeClient
        app_tv_Title.text = getString(R.string.visitor_server_khzx)
        toolbar_back.setOnClickListener {
            it.findNavController().navigateUp()
        }
        (this.activity as MultiFragments).dragView?.dragView?.visibility = View.GONE
        x5WebView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(webView: WebView?, url: String?) {
                //进行预点击事件
                /* webView?.loadUrl(
                     "javascript:(function(){" +
                             "let robot = document.getElementsByClassName(\"easemobim-iframe-shadow\")[0];" +
                             " robot.click(); " +
                             "})()"
                 )*/
                /*  webView?.loadUrl(
                      """
                       let robot = document.getElementsByClassName(easemobim-iframe-shadow)[0];
                       robot.click();
                        }
                    """
                  )*/

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
//                x5WebView?.loadUrl(
//                    "javascript:(function(){" +
//                            "let robot = document.getElementsByClassName(\"easemobim-iframe-shadow\")[0];" +
//                            " robot.click(); " +
//                            "})();"
//                )
            }
            super.onProgressChanged(p0, p1)
        }
    }
}