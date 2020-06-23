package com.stxx.wyhvisitorandroid.mplusvm

import android.text.method.LinkMovementMethod
import com.gavindon.mvvm_lib.base.MVVMBaseModel
import com.gavindon.mvvm_lib.base.MVVMBaseViewModel
import com.gavindon.mvvm_lib.net.*
import com.gavindon.mvvm_lib.utils.*
import com.gavindon.mvvm_lib.widgets.showToast
import com.google.gson.reflect.TypeToken
import com.stxx.wyhvisitorandroid.ApiService
import com.stxx.wyhvisitorandroid.TOKEN
import com.stxx.wyhvisitorandroid.bean.ScenicServerResp
import com.stxx.wyhvisitorandroid.bean.UploadResp
import com.stxx.wyhvisitorandroid.bean.UploadRespSrc
import com.stxx.wyhvisitorandroid.bean.UserInfoResp
import com.stxx.wyhvisitorandroid.widgets.HtmlUtil
import kotlinx.android.synthetic.main.fragment_scenic_server_second.*
import java.io.File

/**
 * description:
 * Created by liNan on  2020/2/26 10:50
 */

class MineVm : MVVMBaseViewModel() {
    private val model = MineModel()
    //用户信息
    private lateinit var userInfoLiveEvent: singLiveData<UserInfoResp>
    var isLoginFinish = SingleLiveEvent<Boolean>()

    fun getUserInfo(): singLiveData<UserInfoResp> {
        if (!this::userInfoLiveEvent.isInitialized) {
            userInfoLiveEvent = singLiveData()
            fetchUserInfo()
        } else if (isLoginFinish.value == true) {
            fetchUserInfo()
        }
        return userInfoLiveEvent
    }

    fun fetchUserInfo(): singLiveData<UserInfoResp> {
        userInfoLiveEvent = singLiveData()
        model.userInfo({
            userInfoLiveEvent.value = Resource.create(it)
        }, {
            userInfoLiveEvent.value = Resource.create(it)
        })
        return userInfoLiveEvent
    }


    fun getLogout(): SingleLiveEvent<Boolean> {
        val logout = SingleLiveEvent<Boolean>()
        model.logout({
            logout.value = true
        }, { logout.value = false })
        return logout
    }

    fun uploadIcon(file: File): singLiveData<String> {
        val singLiveData = singLiveData<String>()
        model.updateIcon(file, {
            singLiveData.value = Resource.create(it)
        }, {
            singLiveData.value = Resource.create(it)
        })
        return singLiveData
    }

    fun updateNickName(nn: String): singLiveData<String> {
        val singLiveData = singLiveData<String>()
        model.updateNickName(nn) {
            singLiveData.value = Resource.create(it)
        }
        return singLiveData
    }

    private val serviceNoticeLiveEvent = singLiveData<List<ScenicServerResp>>()
    private var lastServerIndex = -1

    fun getServiceNotice(params: Parameters): singLiveData<List<ScenicServerResp>> {
        val cIndex = params[0].second as Int
        fun innerRequest() {
            model.fetchServerNotice(params, {
                serviceNoticeLiveEvent.value = Resource.create(it)
            }, {
                serviceNoticeLiveEvent.value = Resource.create(it)

            })
        }
        if (lastServerIndex == cIndex) {
            if (serviceNoticeLiveEvent.value is SuccessSource) {
                return serviceNoticeLiveEvent
            } else {
                innerRequest()
            }
        } else {
            innerRequest()
        }
//        lastServerIndex = cIndex
        return serviceNoticeLiveEvent
    }

}

class MineModel : MVVMBaseModel() {

    fun userInfo(onSuccessT: onSuccessT<BR<UserInfoResp>>, onFailed: onFailed) {
        val type = object : TypeToken<BR<UserInfoResp>>() {}.type
        http?.getWithoutLoading(ApiService.USER_INFO)
            ?.parse<BR<UserInfoResp>>(type, {
                onSuccessT(it)
            }, {
                onFailed(it)
            })
    }

    fun logout(onSuccessBr: onSuccessBr, onFailed: onFailed) {
        http?.get(ApiService.LOGINOUT)?.parse<BR<String>>(strType, {
            onSuccessBr(it)
        }, {
            onFailed(it)
        })
    }

    /**
     * 修改用户头像
     */
    fun updateIcon(file: File, onSuccessBr: onSuccessBr, onFailed: onFailed) {
        val typeToken = object : TypeToken<BR<List<UploadRespSrc>>>() {}.type

        http?.upload(ApiService.UPLOAD_ICON, listOf(file))
            ?.parse<BR<List<UploadRespSrc>>>(typeToken, {
                if (it.code == 0 && it.data.isNotEmpty()) {
                    http!!.get(ApiService.UPDATE_ICON, listOf("imgurl" to it.data[0].src))
                        .parse<BR<String>>(strType, { resp ->
                            onSuccessBr(resp)
                        }, { t ->
                            onFailed(t)
                        })
                } else {
                    onFailed(CodeException())
                }

            }, {
                onFailed(it)
            })
    }

    /**
     * 修改昵称
     */
    fun updateNickName(trueName: String, onSuccessBr: onSuccessBr) {
        http?.get(ApiService.UPDATE_NAME, listOf("truename" to trueName))
            ?.parse<BR<String>>(strType, {
                onSuccessBr(it)
            }, {

            })
    }

    /**
     * 获取游客须知等信息
     */
    fun fetchServerNotice(
        parameters: Parameters,
        onSuccessT: onSuccessT<BR<List<ScenicServerResp>>>,
        onFailed: onFailed
    ) {
        val type = object : TypeToken<BR<List<ScenicServerResp>>>() {}.type
        http?.get(ApiService.SCENIC_SERVER, parameters)
            ?.parse<BR<List<ScenicServerResp>>>(type, {
                onSuccessT(it)
            }, {
                onFailed(it)
            })
    }
}
