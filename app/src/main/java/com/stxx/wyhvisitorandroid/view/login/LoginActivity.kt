package com.stxx.wyhvisitorandroid.view.login

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import com.gavindon.mvvm_lib.utils.SpUtils
import com.gavindon.mvvm_lib.utils.phoneRegex
import com.gavindon.mvvm_lib.widgets.showToast
import com.gyf.immersionbar.ImmersionBar
import com.stxx.wyhvisitorandroid.LOGIN_NAME_SP
import com.stxx.wyhvisitorandroid.PASSWORD_SP
import com.stxx.wyhvisitorandroid.R
import com.stxx.wyhvisitorandroid.base.BaseActivity
import com.stxx.wyhvisitorandroid.bean.WXLoginResp
import com.stxx.wyhvisitorandroid.mplusvm.LoginVm
import com.stxx.wyhvisitorandroid.mplusvm.MineVm
import com.stxx.wyhvisitorandroid.view.helpers.WeChatRegister
import com.stxx.wyhvisitorandroid.view.splash.WxLoginBindPhoneActivity
import com.stxx.wyhvisitorandroid.wxapi.WXEntryActivity.WXAUTHDATA
import com.tencent.mm.opensdk.modelmsg.SendAuth
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.startActivity
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
        if (!Pattern.matches(phoneRegex, loginName)) {
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
        val req = SendAuth.Req()
        req.scope = "snsapi_userinfo"
        req.state = "wyh_wx_login"
        WeChatRegister.wxApi?.sendReq(req)
    }
}
