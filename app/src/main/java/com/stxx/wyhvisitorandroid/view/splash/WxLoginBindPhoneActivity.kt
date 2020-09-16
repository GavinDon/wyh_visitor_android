package com.stxx.wyhvisitorandroid.view.splash

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.lifecycle.Observer
import com.gavindon.mvvm_lib.net.SuccessSource
import com.gavindon.mvvm_lib.utils.SpUtils
import com.gavindon.mvvm_lib.utils.getStatusBarHeight
import com.gavindon.mvvm_lib.utils.phoneRegex
import com.gavindon.mvvm_lib.widgets.showToast
import com.gyf.immersionbar.ImmersionBar
import com.stxx.wyhvisitorandroid.BIND_PHONE_RESULT
import com.stxx.wyhvisitorandroid.LOGIN_NAME_SP
import com.stxx.wyhvisitorandroid.R
import com.stxx.wyhvisitorandroid.base.BaseActivity
import com.stxx.wyhvisitorandroid.mplusvm.LoginVm
import com.stxx.wyhvisitorandroid.view.mine.MineView
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.fragment_webview.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.toolbar.frame_layout_title
import kotlinx.android.synthetic.main.toolbar.titleBar
import kotlinx.android.synthetic.main.wx_login_bind_phone.*
import org.jetbrains.anko.backgroundColorResource
import java.lang.Exception
import java.util.regex.Pattern

/**
 * description: 微信登陆完成绑定手机号
 * Created by liNan on  2020/9/12 22:06
 */
class WxLoginBindPhoneActivity : BaseActivity() {
    override val layoutId: Int
        get() = R.layout.wx_login_bind_phone


    private val loginVm: LoginVm by lazy { getViewModel<LoginVm>() }
    override fun onInit(savedInstanceState: Bundle?) {
        bindSendSmsView.setOnSmsClickListener() {
            loginVm.getSmsCode(listOf(Pair("phone", it)))
        }

        btnBindPhone.setOnClickListener {
            val strPhone = tvBindPhone.text.toString().trim()
            val smsCode = bindSendSmsView.getEditText().text.toString().trim()
            when {
                !Pattern.matches(phoneRegex, strPhone) -> {
                    showToast(getString(R.string.input_right_phone))
                }
                smsCode.isEmpty() -> {
                    showToast(getString(R.string.code_error))
                }
                else -> {
                    loginVm.bindPhone(strPhone, smsCode).observe(this, Observer {
                        handlerResponseData(it, {
                            SpUtils.put(LOGIN_NAME_SP, strPhone)
                            showToast("绑定成功")
                            //绑定成功更新个人信息
                            try {
                                val resourceValue = MineView.mineVm.getUserInfo().value
                                if (resourceValue is SuccessSource) {
                                    resourceValue.body.data.phone = strPhone
                                    MineView.mineVm.getUserInfo().postValue(resourceValue)
                                }
                                val i = Intent()
                                i.putExtra("isBind", true)
                                setResult(BIND_PHONE_RESULT, i)
                                this.finish()
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                            }
                        }, {})
                    })
                }
            }
        }

    }

    override fun setStatusBar() {
        titleBar.layoutParams.height = getStatusBarHeight(this)
        titleBar.setBackgroundColor(Color.WHITE)
        frame_layout_title.setBackgroundColor(Color.WHITE)
        ImmersionBar.with(this)
            .transparentStatusBar()
            .statusBarDarkFont(true)
            .init()

    }

    override fun permissionForResult() {
    }


}