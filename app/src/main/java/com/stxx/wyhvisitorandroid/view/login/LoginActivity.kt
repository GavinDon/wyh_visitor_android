package com.stxx.wyhvisitorandroid.view.login

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.URLSpan
import android.view.View
import androidx.lifecycle.Observer
import com.gavindon.mvvm_lib.utils.SpUtils
import com.gavindon.mvvm_lib.utils.phoneRegex
import com.gavindon.mvvm_lib.widgets.showToast
import com.gyf.immersionbar.ImmersionBar
import com.stxx.wyhvisitorandroid.*
import com.stxx.wyhvisitorandroid.base.BaseActivity
import com.stxx.wyhvisitorandroid.bean.WXLoginResp
import com.stxx.wyhvisitorandroid.mplusvm.LoginVm
import com.stxx.wyhvisitorandroid.mplusvm.MineVm
import com.stxx.wyhvisitorandroid.view.helpers.WeChatRegister
import com.stxx.wyhvisitorandroid.view.splash.WxLoginBindPhoneActivity
import com.stxx.wyhvisitorandroid.view.webview.WebViewActivity
import com.stxx.wyhvisitorandroid.wxapi.WXEntryActivity.WXAUTHDATA
import com.tencent.mm.opensdk.modelmsg.SendAuth
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.dialog_protocol.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.startActivity
import java.util.regex.Pattern

class LoginActivity : BaseActivity() {

    private lateinit var loginVm: LoginVm
    private lateinit var mineVm: MineVm

    override val layoutId: Int = R.layout.activity_login

    override fun onInit(savedInstanceState: Bundle?) {
        btnLogin.setOnClickListener {
            goLogin()
        }
        tvForget.setOnClickListener {
            startActivity<ForgetPasswordActivity>()
        }
        tvGoRegister.setOnClickListener {
            startActivity<RegisterActivity>()
        }
        btnWxLogin.setOnClickListener {
            wakeWxApp()
        }
        val li =
            """ 我已阅读并同意<a href=$FWXY>《服务协议》</a>和 <a href=$YSZC>《隐私政策》</a>"""
        cbAgree.text = getClickableHtml(li)
        cbAgree.isChecked = SpUtils.get(AGREE_PROTOCOL_LOGIN, false)
        cbAgree.movementMethod = LinkMovementMethod.getInstance()
        cbAgree.setOnCheckedChangeListener { _, isChecked ->
            SpUtils.put(AGREE_PROTOCOL_LOGIN, isChecked)
        }
    }

    override fun onResume() {
        super.onResume()
        inputLayoutAccount.editText?.setText(SpUtils.get(LOGIN_NAME_SP, ""))
        inputLayoutPassWord.editText?.setText(SpUtils.get(PASSWORD_SP, ""))
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val wxResp = intent?.getSerializableExtra(WXAUTHDATA)
        if (wxResp is WXLoginResp) {
            val openId = wxResp.openid
            val accessToken = wxResp.access_token
            if (!this::loginVm.isInitialized) loginVm = getViewModel()
            if (!openId.isNullOrEmpty() && !accessToken.isNullOrEmpty()) {
                loginVm.wxLogin(openId, accessToken).observe(this, Observer {
                    if (it == null) {
                        showToast("信息同步失败,请重试~")
                    } else {
                        //微信登陆 清除可能存在的密码信息，防止判断是否显示更换手机号码错误

                        //如果没有绑定正确的手机号 则跳转绑定界面
                        if (!Pattern.matches(phoneRegex, it.phone ?: "")) {
                            startActivity<WxLoginBindPhoneActivity>()
                            this.finish()
                        } else {
                            this.finish()
                        }
                    }
                })
            } else {
                showToast("微信授权失败,请重试~")
            }
        }


    }

    override fun permissionForResult() {
    }

    private fun goLogin() {
        loginVm = getViewModel()
        val loginName = inputLayoutAccount.editText?.text.toString().trim()
        val password = inputLayoutPassWord.editText?.text.toString().trim()
        if (!SpUtils.get(AGREE_PROTOCOL_LOGIN, false)) {
            showToast("请阅读并勾选协议")
        } else if (!Pattern.matches(phoneRegex, loginName)) {
            showToast("请输入正确的手机号")
        } else if (password.isBlank()) {
            showToast("请输入正确的密码")
        } else {
            loginVm.getLogin(listOf("loginName" to loginName, "password" to password))
                .observe(this, Observer {
                    handlerResponseData(it, {
                        mineVm = getViewModel()
                        mineVm.isLoginFinish.postValue(true)
                        this.finish()
                    }, {

                    })
                })
        }
    }

    override fun setStatusBar() {
        ImmersionBar.with(this)
            .transparentStatusBar()
            .statusBarDarkFont(true)
            .init()
    }

    private fun wakeWxApp() {
        if (!SpUtils.get(AGREE_PROTOCOL_LOGIN, false)) {
            showToast("请勾选同意下方协议")
        } else {
            val req = SendAuth.Req()
            req.scope = "snsapi_userinfo"
            req.state = "wyh_wx_login"
            WeChatRegister.wxApi?.sendReq(req)
        }

    }

    private fun getClickableHtml(html: String?): CharSequence {
        val spannedHtml: Spanned = Html.fromHtml(html)
        val clickableHtmlBuilder = SpannableStringBuilder(spannedHtml)
        val spans = clickableHtmlBuilder.getSpans(0, spannedHtml.length, URLSpan::class.java)
        for (value in spans) {
            setLinkClickable(clickableHtmlBuilder, value)
        }

        return clickableHtmlBuilder
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
                if (url?.contains("softwareService.html") == true) {
                    startActivity<WebViewActivity>(Pair(WEB_VIEW_URL, FWXY))
                } else if (url?.contains("userPrivacy.html") == true) {
                    startActivity<WebViewActivity>(Pair(WEB_VIEW_URL, YSZC))
                }
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
