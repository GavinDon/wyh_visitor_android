package com.stxx.wyhvisitorandroid.mplusvm

import com.gavindon.mvvm_lib.base.MVVMBaseApplication
import com.gavindon.mvvm_lib.base.MVVMBaseModel
import com.gavindon.mvvm_lib.base.MVVMBaseViewModel
import com.gavindon.mvvm_lib.net.*
import com.gavindon.mvvm_lib.net.ExceptionHandle.handleException
import com.gavindon.mvvm_lib.utils.*
import com.gavindon.mvvm_lib.utils.SpUtils.put
import com.gavindon.mvvm_lib.widgets.showToast
import com.stxx.wyhvisitorandroid.ApiService
import com.stxx.wyhvisitorandroid.LOGIN_NAME_SP
import com.stxx.wyhvisitorandroid.PASSWORD_SP
import com.stxx.wyhvisitorandroid.TOKEN

/**
 * description: 注册 登陆 找回密码 发送验证码
 * Created by liNan on  2020/2/26 08:42
 */

class LoginVm : MVVMBaseViewModel() {

    companion object {
        //注册来源 app
        const val SOURCE_APP = 1
    }


    private val loginModel = LoginModel()

    private var smsCode = ""

    /**
     * 登陆
     */
    fun getLogin(reqParam: Parameters): singLiveData<String> {
        val sLiveData = singLiveData<String>()
        loginModel.login(reqParam, {
            sLiveData.value = Resource.create(it)
        }, {
            sLiveData.value = Resource.create(it)
        }
        )
        return sLiveData
    }

    /**
     * 获取验证码
     */
    fun getSmsCode(reqParam: Parameters) {
        loginModel.sendSmsCode(reqParam, {
            smsCode = it.data
//            MVVMBaseApplication.appContext.showToast(it.data)
        }, {
            val ex = handleException(it)
            MVVMBaseApplication.appContext.showToast(ex.errorMsg)
        })
    }

    /**
     * 注册
     */
    fun getRegister(strPhone: String, pwd: String, code: String) {

        val reqParam =
            listOf("phone" to strPhone, "password" to pwd, "vcode" to code, "source" to SOURCE_APP)

        loginModel.register(reqParam)
    }

    fun getForgetPwd(phone: String, newPassword: String, code: String) {
        val reqParam = listOf("phone" to phone, "newpassword" to newPassword, "vcode" to code)
        loginModel.forgetPwd(reqParam)
    }

}

class LoginModel : MVVMBaseModel() {
    fun login(
        param: Parameters,
        onSuccess: onSuccessBr,
        onFailed: onFailed
    ) {
        http?.get(ApiService.LOGIN, param)
            ?.parse<BR<String>>(strType, {
                val r = Resource.create(it)
                if (r is SuccessSource) {
                    val token = it.data
                    if (token.isNotEmpty()) {
                        HttpManager.instance.addHeader("token" to token)
                        SpUtils.let {
                            put(LOGIN_NAME_SP, param[0].second)
                            put(PASSWORD_SP, param[1].second)
                            //保存时间与token
                            put(TOKEN, "$token-${getCurrentDateMillSeconds()}")
                        }
                        onSuccess(it)
                    } else {
                        onFailed(Throwable(TokenException()))
                    }
                } else {
                    onSuccess(it)
                }
            }, {
                onFailed(it)
            })
    }

    /**
     * 发送验证码
     */
    fun sendSmsCode(
        param: Parameters, onSuccess: onSuccessBr,
        onFailed: onFailed
    ) {
        http?.get(ApiService.SEND_SMS_CODE, param)?.parse<BR<String>>(strType, {
            onSuccess(it)
        }, {
            onFailed(it)
        })
    }

    fun register(param: Parameters) {
        http?.get(ApiService.REGISTER, param)?.parse<BR<String>>(strType, {
            finish(param[0].second.toString(), it)
        }, {
            handleException(it)
        })
    }

    fun forgetPwd(param: Parameters) {
        http?.get(ApiService.FORGET_PASSWORD, param)?.parse<BR<String>>(strType, {
            finish(param[0].second.toString(), it)
        }, {
            handleException(it)
        })

    }

    private fun finish(phone: String, br: BR<String>) {
        if (br.code == 0) {
            put(LOGIN_NAME_SP, phone)
            SpUtils.clearName(PASSWORD_SP)
            MVVMBaseApplication.getCurActivity()?.finish()
        } else {
            MVVMBaseApplication.getCurActivity()?.showToast(br.msg)
        }
    }


}