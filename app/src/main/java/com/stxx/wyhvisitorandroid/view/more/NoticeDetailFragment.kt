package com.stxx.wyhvisitorandroid.view.more

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.URLSpan
import android.view.View
import com.stxx.wyhvisitorandroid.R
import com.stxx.wyhvisitorandroid.base.ToolbarFragment
import com.stxx.wyhvisitorandroid.view.helpers.WeChatRegister
import com.stxx.wyhvisitorandroid.widgets.HtmlUtil
import com.stxx.wyhvisitorandroid.wxappid
import com.tencent.mm.opensdk.modelbiz.OpenWebview
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import kotlinx.android.synthetic.main.fragment_notice.*
import org.jetbrains.anko.support.v4.toast

/**
 * description:通知公告详情
 * Created by liNan on  2020/5/18 17:22
 */
class NoticeDetailFragment : ToolbarFragment() {
    override val toolbarName: Int = R.string.notice
    override val layoutId: Int = R.layout.fragment_notice

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        val detail = arguments?.getString("detail").toString()
        //设置可以点击超链接
        tvNoticeDetail.movementMethod = LinkMovementMethod.getInstance()
        val handler = Handler {
            val spanned = it.obj as Spanned
            val clickableHtmlBuilder = SpannableStringBuilder(spanned)
            //必须在setLinkClickable之后设置才会生效,如果想用系统浏览器可以把这行代码放到最上面
            tvNoticeDetail.text = clickableHtmlBuilder

            /*   val spans = clickableHtmlBuilder.getSpans(0, spanned.length, URLSpan::class.java)
               for (value in spans) {
                   setLinkClickable(clickableHtmlBuilder, value)
               }*/

            return@Handler false
        }
        HtmlUtil().show(this.context, detail, handler)
    }


    /**
     * 捕获<a>标签点击事件
     */
    private fun setLinkClickable(clickableHtmlBuilder: SpannableStringBuilder, urlSpan: URLSpan?) {
        val start = clickableHtmlBuilder.getSpanStart(urlSpan)
        val end = clickableHtmlBuilder.getSpanEnd(urlSpan)
        val flags = clickableHtmlBuilder.getSpanEnd(urlSpan)
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(p0: View) {
                val url = urlSpan?.url
                //能够点击的关键在于removeSpan(urlSpan)
//                if (url?.contains("fw.html") == true) {
//                    startActivity<WebViewActivity>(Pair(WEB_VIEW_URL, FWXY))
//                } else if (url?.contains("ys.html") == true) {
//                    startActivity<WebViewActivity>(Pair(WEB_VIEW_URL, YSZC))
//                }
                /*目前来讲 微信还是不支持此功能。
                * （官方回复）https://developers.weixin.qq.com/community/develop/doc/00026e32c24eb86df958fb2ed59400
                * */
                val req = OpenWebview.Req()
                req.url = "https://www.baidu.com"
                WeChatRegister.wxApi?.sendReq(req)
            }

            override fun updateDrawState(ds: TextPaint) {
                //设置颜色
                ds.color = Color.parseColor("#FF9800")
                //设置是否要下划线
                ds.isUnderlineText = false
            }

        }
        clickableHtmlBuilder.setSpan(clickableSpan, start, end, flags)
        // its too important
        clickableHtmlBuilder.removeSpan(urlSpan)

    }
}