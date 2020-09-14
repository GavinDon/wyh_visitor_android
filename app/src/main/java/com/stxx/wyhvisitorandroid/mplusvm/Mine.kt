package com.stxx.wyhvisitorandroid.mplusvm

import android.widget.Toast
import com.gavindon.mvvm_lib.base.MVVMBaseApplication
import com.gavindon.mvvm_lib.base.MVVMBaseModel
import com.gavindon.mvvm_lib.base.MVVMBaseViewModel
import com.gavindon.mvvm_lib.net.*
import com.gavindon.mvvm_lib.net.ExceptionHandle.handleException
import com.gavindon.mvvm_lib.utils.*
import com.gavindon.mvvm_lib.widgets.showToast
import com.github.kittinunf.fuel.core.FileDataPart
import com.github.kittinunf.fuel.gson.gsonDeserializer
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpUpload
import com.github.kittinunf.fuel.rx.rxResponseObject
import com.google.gson.reflect.TypeToken
import com.orhanobut.logger.Logger
import com.stxx.wyhvisitorandroid.ApiService
import com.stxx.wyhvisitorandroid.LOGIN_NAME_SP
import com.stxx.wyhvisitorandroid.bean.FaceIdentifyResp
import com.stxx.wyhvisitorandroid.bean.ScenicServerResp
import com.stxx.wyhvisitorandroid.bean.UploadRespSrc
import com.stxx.wyhvisitorandroid.bean.UserInfoResp
import com.stxx.wyhvisitorandroid.showLoadingDialog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.toast
import java.io.File

/**
 * description:
 * Created by liNan on  2020/2/26 10:50
 */

class MineVm : MVVMBaseViewModel() {
    private val model = MineModel(mComDis)

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

    fun getFaceIdentify(file: File): SingleLiveEvent<Resource<FaceIdentifyResp>> {
        val singLiveData = SingleLiveEvent<Resource<FaceIdentifyResp>>()
        model.faceIdentify(file, {
            singLiveData.value = Resource.create(it)
        }, {
            singLiveData.value = Resource.create(it)
        })
        return singLiveData
    }

    fun postFaceInfo(): SingleLiveEvent<Resource<Boolean>> {
        val singLiveData = SingleLiveEvent<Resource<Boolean>>()
        model.updateFaceInfo2UserInfo({
            //如果更新成功则更新本地userInfo信息
            singLiveData.value = Resource.create(it.code == 0)
        }, {
            singLiveData.value = Resource.create(it)
        })
        return singLiveData
    }

    fun wxBindOrUnBind(openId: String?): singLiveData<String> {
        val singLiveData = singLiveData<String>()
        model.fetchBindWx(openId, {
            singLiveData.value = Resource.create(it)
        }, {
            singLiveData.value = Resource.create(it)

        })
        return singLiveData
    }

}

class MineModel(private val mComDis: CompositeDisposable) : MVVMBaseModel() {

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
     * 人脸认证
     */
    fun faceIdentify(file: File, onSuccessT: onSuccessT<FaceIdentifyResp>, onFailed: onFailed) {
        val typeToken = object : TypeToken<BR<List<UploadRespSrc>>>() {}.type
        val authFaceTypeToken = object : TypeToken<FaceIdentifyResp>() {}.type
        //人脸识别结果
        val phone = SpUtils.get(LOGIN_NAME_SP, "")
        if (phone.isEmpty()) return

        http?.upload(ApiService.UPLOAD_ICON, listOf(file))
            ?.parse2<BR<List<UploadRespSrc>>>(typeToken, { uploadRespSrc ->
                if (!uploadRespSrc.data.isNullOrEmpty()) {
                    val faceUrl = uploadRespSrc.data[0].src
                    http?.getWithoutLoading(
                        ApiService.FACE_REGISTER_URL, listOf(
                            "faceUrl" to faceUrl,
                            "phone" to phone
                        )
                    )?.parse2<FaceIdentifyResp>(authFaceTypeToken, {
                        onSuccessT.invoke(it)
                    }, {
                        onFailed.invoke(it)
                    })
                }
            }, { onFailed.invoke(it) })

        //人脸识别返回时取消对话框
        /*       mComDis.add(
                   ApiService.UPLOAD_ICON.httpUpload().plus(FileDataPart(file))
                       .rxResponseObject(gsonDeserializer<BR<List<UploadRespSrc>>>())
                       .subscribeOn(Schedulers.io())
                       .observeOn(Schedulers.io())
                       .compose(RxScheduler.applySingleScheduler())
                       .compose(RxScheduler.applySingleLoading())
                       .flatMap { t: BR<List<UploadRespSrc>> ->
                           val faceUrl = t.data[0].src
                           ApiService.FACE_REGISTER_URL.httpGet(
                               listOf(
                                   "faceUrl" to faceUrl,
                                   "phone" to phone
                               )
                           )
                               .rxResponseObject(gsonDeserializer<FaceIdentifyResp>())
                       }
                       .observeOn(AndroidSchedulers.mainThread())
                       .compose(RxScheduler.applySingleScheduler())
                       .subscribe({
                           onSuccessT.invoke(it)
                       }, {
                           onFailed.invoke(it)
                       }
                       )
               )*/

    }

    /**
     * 人脸识别成功更新人个信息education字段
     */
    fun updateFaceInfo2UserInfo(onSuccessBr: onSuccessBr, onFailed: onFailed) {
        http?.getWithoutLoading(ApiService.FACE_AUTH, listOf("education" to "1"))
            ?.parse<BR<String>>(strType, {
                onSuccessBr.invoke(it)
            }, {
                onFailed.invoke(it)
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

    /**
     * 绑定微信
     */
    fun fetchBindWx(openId: String?, onSuccessBr: onSuccessBr, onFailed: onFailed) {
        http?.get(ApiService.BIND_WX, listOf("openId" to openId))
            ?.parse<BR<String>>(strType, {
                onSuccessBr(it)
            }, {
                onFailed(it)
            })
    }
}
