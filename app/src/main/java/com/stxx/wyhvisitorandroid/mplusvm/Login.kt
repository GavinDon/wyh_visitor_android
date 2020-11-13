package com.stxx.wyhvisitorandroid.mplusvm

import com.gavindon.mvvm_lib.base.MVVMBaseApplication
import com.gavindon.mvvm_lib.base.MVVMBaseModel
import com.gavindon.mvvm_lib.base.MVVMBaseViewModel
import com.gavindon.mvvm_lib.net.*
import com.gavindon.mvvm_lib.net.BR
import com.gavindon.mvvm_lib.net.ExceptionHandle.handleException
import com.gavindon.mvvm_lib.utils.*
import com.gavindon.mvvm_lib.utils.SpUtils.put
import com.gavindon.mvvm_lib.widgets.showToast
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.gson.gsonDeserializer
import com.github.kittinunf.fuel.rx.rxResponseObject
import com.google.gson.reflect.TypeToken
import com.orhanobut.logger.Logger
import com.stxx.wyhvisitorandroid.*
import com.stxx.wyhvisitorandroid.bean.UserInfoResp
import com.stxx.wyhvisitorandroid.bean.WxUserInfoResp
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.regex.Pattern

/**
 * description: 注册 登陆 找回密码 发送验证码
 * Created by liNan on  2020/2/26 08:42
 */

class LoginVm : MVVMBaseViewModel() {

    companion object {
        //注册来源 app
        const val SOURCE_APP = 1
    }


    private val loginModel = LoginModel(mComDis)

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

    fun bindPhone(
        strPhone: String,
        code: String,
        url: String = ApiService.BIND_PHONE
    ): singLiveData<String> {
        val reqParam = listOf("phone" to strPhone, "vcode" to code)
        val singleLiveEvent = singLiveData<String>()
        loginModel.bindPhone(reqParam, url, {
            singleLiveEvent.value = Resource.create(it)
        }, {
            singleLiveEvent.value = Resource.create(it)
        })
        return singleLiveEvent
    }

    fun getForgetPwd(phone: String, newPassword: String, code: String) {
        val reqParam = listOf("phone" to phone, "newpassword" to newPassword, "vcode" to code)
        loginModel.forgetPwd(reqParam)
    }

    /**
     * 返回true代表处理成功
     */
    fun wxLogin(openId: String, accessToken: String): SingleLiveEvent<UserInfoResp?> {
        val singleLiveEvent = SingleLiveEvent<UserInfoResp?>()
        loginModel.getWxLogin(openId, accessToken, {
            singleLiveEvent.value = it
        }, {
            //传null 过去代表http异常
            singleLiveEvent.value = null
        })
        return singleLiveEvent
    }

}

class LoginModel(private val mComDis: CompositeDisposable) : MVVMBaseModel() {
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

    fun bindPhone(param: Parameters, url: String, onSuccess: onSuccessBr, onFailed: onFailed) {
        http?.get(url, param)?.parse<BR<String>>(strType, {
            onSuccess(it)
        }, {
            onFailed(it)
        })
    }


    fun forgetPwd(param: Parameters) {
        http?.get(ApiService.FORGET_PASSWORD, param)?.parse<BR<String>>(strType, {
            finish(param[0].second.toString(), it)
        }, {
            handleException(it)
        })

    }

    fun getWxLogin(
        openId: String,
        accessToken: String,
        onSuccess: onSuccessT<UserInfoResp>,
        onFailed: onFailed
    ) {
        //使用微信token换自己服务器上的token
        val getToken = Fuel.get(ApiService.WX_LOGIN, listOf("openId" to openId))
            .rxResponseObject(gsonDeserializer<BR<String>>())
            .toObservable()
        //微信用户信息
        val getWxUserInfo = Fuel.get(
            ApiService.WX_USER_INFO,
            listOf("openId" to openId, "access_token" to accessToken)
        )
            .rxResponseObject(gsonDeserializer<WxUserInfoResp>())
            .toObservable()
        val user = Observable.concat(getToken, getWxUserInfo)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .compose(RxScheduler.applyLoading())
            .subscribe({
                if (it is BR<*>) {
                    if (it.code == 0 && it.data != null) {
                        val token = it.data.toString()
                        HttpManager.instance.addHeader("token" to token)
                        put(TOKEN, "$token-${getCurrentDateMillSeconds()}")
                    } else {
                        onFailed(Throwable(TokenException()))
                    }
                }
                if (it is WxUserInfoResp) {
                    val mineModel = MineModel(mComDis)
                    mineModel.userInfo({ userInfo ->
                        val data = userInfo.data
                        //判断是否存在合法的phone
                        if (!Pattern.matches(phoneRegex, data.phone ?: "")) {
                            //把微信的数据同步过去
                            mineModel.updateNickName(it.nickname) {}
                            http?.get(ApiService.UPDATE_ICON, listOf("imgurl" to it.headimgurl))
                                ?.parse<BR<String>>(strType, { }, { })
                        }
                        //使用微信登陆如果已经绑定手机号 防止卸载之后没有手机信息
                        put(LOGIN_NAME_SP, data.phone ?: "")
                        onSuccess(userInfo.data)
                    }, { error ->
                        onFailed(error)
                    })

                }
            }, { error ->
                onFailed(error)
            })

    }

    /**
     * 更新微信信息到自己服务器中
     */
    private fun updateWxIcon(imgUrl: String, trueName: String) {
        http?.getWithoutLoading(ApiService.UPDATE_ICON, listOf("imgurl" to imgUrl))
        http?.getWithoutLoading(ApiService.UPDATE_NAME, listOf("truename" to trueName))
    }


    /**
     * 忘记密码、注册 完成之后保存信息
     */
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