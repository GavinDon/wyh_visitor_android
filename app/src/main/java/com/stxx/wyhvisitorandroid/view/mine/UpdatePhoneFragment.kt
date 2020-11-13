package com.stxx.wyhvisitorandroid.view.mine

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gavindon.mvvm_lib.base.ViewModelProviders
import com.gavindon.mvvm_lib.net.BR
import com.gavindon.mvvm_lib.net.ExceptionHandle
import com.gavindon.mvvm_lib.net.http
import com.gavindon.mvvm_lib.net.parse2
import com.gavindon.mvvm_lib.utils.SpUtils
import com.gavindon.mvvm_lib.utils.phoneRegex
import com.gavindon.mvvm_lib.widgets.showToast
import com.google.gson.reflect.TypeToken
import com.gyf.immersionbar.ImmersionBar
import com.stxx.wyhvisitorandroid.ApiService
import com.stxx.wyhvisitorandroid.LOGIN_NAME_SP
import com.stxx.wyhvisitorandroid.R
import com.stxx.wyhvisitorandroid.mplusvm.LoginVm
import com.stxx.wyhvisitorandroid.view.dialog.BaseDialogFragment
import com.stxx.wyhvisitorandroid.view.login.LoginActivity
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.dialog_update_userinfo.*
import kotlinx.android.synthetic.main.fragment_update_phone.*
import org.jetbrains.anko.support.v4.startActivity
import java.util.regex.Pattern

/**
 * description: 修改用户手机号
 * Created by liNan on  2020/11/9 15:04
 */
class UpdatePhoneFragment : BaseDialogFragment() {


    private lateinit var loginVm: LoginVm

    private val compositeDisposable: CompositeDisposable by lazy { CompositeDisposable() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_update_phone, null, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar_back.setOnClickListener {
            dismissAllowingStateLoss()
        }
        loginVm = ViewModelProviders.of(this).get(LoginVm::class.java)
        loadSmsCode()
        btnUpdatePhone.setOnClickListener {
            val phone = etUpdatePhone.text.toString().trim()
            val currentLoginPhone = SpUtils.get(LOGIN_NAME_SP, "")
            val vcode = sendSmsView.getEditText().text.toString().trim()
            if (currentLoginPhone.isEmpty()) {
                //如果获取手机号错误跳转到登陆页面
                startActivity<LoginActivity>()
            } else {
                //判断是否替换的是当前登陆的帐号
                when {
                    currentLoginPhone == phone -> {
                        this.context?.showToast("请输入要更换的新手机号")
                    }
                    !Pattern.matches(phoneRegex, phone) -> {
                        this.context?.showToast("请输入正确的手机号")
                    }
                    vcode.length < 6 -> {
                        this.context?.showToast("请输入正确的验证码")
                    }
                    else -> {
                        loadData(phone, vcode)
                    }
                }
            }


        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        app_tv_Title?.text = "更换帐户手机号"
        ImmersionBar.with(this)
            .fitsSystemWindows(true)
            .statusBarColor(R.color.colorPrimaryYellow)
            .statusBarDarkFont(true)
            .init()
    }

    private fun loadData(phone: String, vcode: String) {
        val typeToken = object : TypeToken<BR<String>>() {}.type
        compositeDisposable.add(
            http?.get(
                ApiService.UPDATE_PHONE,
                listOf(Pair("phone", phone), Pair("vcode", vcode))
            )!!.parse2<BR<String>>(typeToken, {
                com.orhanobut.logger.Logger.i(it.data)
            }, {
                ExceptionHandle.handleException(it)
            })

        )
    }

    private fun loadSmsCode() {
        sendSmsView.setOnSmsClickListener {
            loginVm.getSmsCode(listOf(Pair("phone", it)))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.clear()
    }
}