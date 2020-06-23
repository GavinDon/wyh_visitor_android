package com.stxx.wyhvisitorandroid.view.login

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.gavindon.mvvm_lib.utils.SpUtils
import com.gavindon.mvvm_lib.utils.phoneRegex
import com.gavindon.mvvm_lib.widgets.showToast
import com.gyf.immersionbar.ImmersionBar
import com.stxx.wyhvisitorandroid.LOGIN_NAME_SP
import com.stxx.wyhvisitorandroid.PASSWORD_SP
import com.stxx.wyhvisitorandroid.R
import com.stxx.wyhvisitorandroid.base.BaseActivity
import com.stxx.wyhvisitorandroid.base.BaseFragment
import com.stxx.wyhvisitorandroid.mplusvm.LoginVm
import com.stxx.wyhvisitorandroid.mplusvm.MineVm
import kotlinx.android.synthetic.main.activity_login.*
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
    }

    override fun onResume() {
        super.onResume()
        inputLayoutAccount.editText?.setText(SpUtils.get(LOGIN_NAME_SP, ""))
        inputLayoutPassWord.editText?.setText(SpUtils.get(PASSWORD_SP, ""))
    }

    override fun permissionForResult() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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
}
