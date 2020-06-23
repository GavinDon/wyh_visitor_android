package com.stxx.wyhvisitorandroid.view.login

import android.os.Bundle
import com.gavindon.mvvm_lib.utils.phoneRegex
import com.gavindon.mvvm_lib.widgets.showToast
import com.gyf.immersionbar.ImmersionBar
import com.stxx.wyhvisitorandroid.R
import com.stxx.wyhvisitorandroid.base.BaseActivity
import com.stxx.wyhvisitorandroid.mplusvm.LoginVm
import com.stxx.wyhvisitorandroid.view.ar.ArUnityActivity
import kotlinx.android.synthetic.main.activity_register.*
import org.jetbrains.anko.startActivity
import java.util.regex.Pattern

class RegisterActivity : BaseActivity() {


    override val layoutId: Int = R.layout.activity_register

    private lateinit var loginVm: LoginVm

    override fun onInit(savedInstanceState: Bundle?) {
        loginVm = getViewModel()
        listenerRegister()
        listenerSendCode()
    }

    private fun listenerSendCode() {
        sendSmsView.setOnSmsClickListener() {
            loginVm.getSmsCode(listOf(Pair("phone", it)))
        }
    }

    /**
     * 注册
     */
    private fun listenerRegister() {
        btnRegister.setOnClickListener {
            val strPhone = tvRegisterPhone.text.toString().trim()
            val pwd = tvInputPwd.text.toString().trim()
            val againPwd = tvInputPwdAgain.text.toString().trim()
            val smsCode = sendSmsView.getEditText().text.toString().trim()
            when {
                !Pattern.matches(phoneRegex, strPhone) -> {
                    showToast(getString(R.string.input_right_phone))
                }
                smsCode.isEmpty() -> {
                    showToast(getString(R.string.code_error))
                }
                !((pwd == againPwd) && (pwd.isNotEmpty())) -> {
                    showToast(getString(R.string.pwd_Inconsistent))
                }
                else -> {
                    loginVm.getRegister(strPhone, pwd, smsCode)
                }
            }
        }
    }

    override fun permissionForResult() {
    }

    override fun setStatusBar() {
        ImmersionBar.with(this)
            .transparentStatusBar()
            .statusBarDarkFont(true)
            .init()
    }
}
