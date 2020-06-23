package com.stxx.wyhvisitorandroid.view.login

import android.os.Bundle
import com.gavindon.mvvm_lib.widgets.showToast
import com.gyf.immersionbar.ImmersionBar
import com.stxx.wyhvisitorandroid.R
import com.stxx.wyhvisitorandroid.base.BaseActivity
import com.stxx.wyhvisitorandroid.mplusvm.LoginVm
import kotlinx.android.synthetic.main.activity_forget_password.*

class ForgetPasswordActivity : BaseActivity() {

    private lateinit var loginVm: LoginVm

    override val layoutId: Int = R.layout.activity_forget_password

    override fun onInit(savedInstanceState: Bundle?) {
        listenerSendCode()
        listenerForget()
        ivLeftArrow.setOnClickListener { this.finish() }
    }

    private fun listenerSendCode() {
        resetSendSmsView.setOnSmsClickListener() {
            loginVm = getViewModel()
            loginVm.getSmsCode(listOf(Pair("phone", it)))
        }
    }

    private fun listenerForget() {
        btnUpdate.setOnClickListener {
            val phone = etResetPhone.text.toString().trim()
            val smsCode = resetSendSmsView.getEditText().text.toString().trim()
            val password = etResetPwd.text.toString().trim()
            val againPwd = etResetPwdAgain.text.toString().trim()
            when {
                phone.isEmpty() -> {
                    showToast(getString(R.string.input_right_phone))
                }
                smsCode.isEmpty() -> {
                    showToast(getString(R.string.code_error))
                }
                !((password.isNotEmpty()) && (password == againPwd)) -> {
                    showToast(getString(R.string.pwd_Inconsistent))
                }
                else -> {
                    loginVm = getViewModel()
                    loginVm.getForgetPwd(phone, password, smsCode)
                }
            }
        }
    }

    override fun permissionForResult() {
    }

    override fun setStatusBar() {
        ImmersionBar.with(this)
            .fitsSystemWindows(true)
            .statusBarColor(R.color.colorRegisterBg)
            .statusBarDarkFont(true)
            .init()
    }
}
