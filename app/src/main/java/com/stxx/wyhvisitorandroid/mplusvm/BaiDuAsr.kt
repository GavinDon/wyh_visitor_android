package com.stxx.wyhvisitorandroid.mplusvm

import com.gavindon.mvvm_lib.base.MVVMBaseViewModel
import com.gavindon.mvvm_lib.net.*
import com.gavindon.mvvm_lib.utils.GsonUtil
import com.gavindon.mvvm_lib.utils.singLiveData
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.rx.rxResponseString
import com.google.gson.reflect.TypeToken
import com.orhanobut.logger.Logger
import com.stxx.wyhvisitorandroid.ApiService
import com.stxx.wyhvisitorandroid.bean.BDasrResp
import com.stxx.wyhvisitorandroid.view.asr.HttpUtil
import java.lang.Exception

/**
 * description:
 * Created by liNan on  2020/4/22 10:22
 */
class BaiDuAsr : MVVMBaseViewModel() {

    val sLiveData = SingleLiveEvent<BDasrResp>()
    fun getAuth(): SingleLiveEvent<BDasrResp> {
        val param = listOf(
            "grant_type" to "client_credentials",
            "client_id" to "yg57pRXqVKhQSCUFoS9ft08D",
            "client_secret" to "nUUGVMpCGlvHNTrvoV5yy8rjNHWuTONv"
        )
        val type = object : TypeToken<BDasrResp>() {}.type
        http?.postWithoutLoading(ApiService.BAIDU_TOKEN, param)
            ?.parse<BDasrResp>(type, {
                if (it.access_token.isNotEmpty()) {
                    sLiveData.value = it
                }
            }, {

            })

        return sLiveData
    }

    fun botChat(param: String, token: String) {
        try {
            val result = HttpUtil.post(ApiService.BAIDU_BOT, token, param)
            Logger.i(result)
        } catch (e: Exception) {

        }


        http?.compositeDisposable?.add(
            Fuel.post("${ApiService.BAIDU_BOT}?access_token=${token}")
                .jsonBody(param)
                .rxResponseString()
                .compose(RxScheduler.applySingleScheduler())
                .subscribe({
                    Logger.i(it)
                }, {
                    Logger.i(it.localizedMessage)
                })
        )


    }
}