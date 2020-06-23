package com.stxx.wyhvisitorandroid.mplusvm

import com.gavindon.mvvm_lib.base.MVVMBaseModel
import com.gavindon.mvvm_lib.base.MVVMBaseViewModel
import com.gavindon.mvvm_lib.net.*
import com.gavindon.mvvm_lib.utils.*
import com.github.kittinunf.fuel.core.Parameters
import com.google.gson.reflect.TypeToken
import com.orhanobut.logger.Logger
import com.stxx.wyhvisitorandroid.ApiService
import com.stxx.wyhvisitorandroid.bean.*
import io.reactivex.disposables.CompositeDisposable
import java.io.File


/**
 * description:游客服务
 * Created by liNan on 2020/3/10 15:31

 */

class ComplaintVm : MVVMBaseViewModel() {

    private val model: ComplaintRepositories = ComplaintRepositories(mComDis)
    val queryLiveData = singLiveData<List<ReportResultResp>>()

    fun complaint(param: Parameters, file: List<String>): singLiveData<String> {
        val liveData = singLiveData<String>()
        model.postComplaint(param, file, {
            liveData.value = Resource.create(it)
        }, {
            liveData.value = Resource.create(it)
        })
        return liveData

    }

    fun queryResult(phone: String): singLiveData<List<ReportResultResp>> {

        model.reportQuery(phone, {
            queryLiveData.value = Resource.create(it)
        }, {
            queryLiveData.value = Resource.create(it)
        })

        return queryLiveData
    }

    fun getBanner(): singLiveData<List<BannerResp>> {
        val singleLiveEvent = singLiveData<List<BannerResp>>()
        model.fetchBanner({
            singleLiveEvent.value = Resource.create(it)
        }, {
            singleLiveEvent.value = Resource.create(it)
        })
        return singleLiveEvent
    }

    fun upComment(param: Parameters, file: List<String>): singLiveData<String> {
        val liveData = singLiveData<String>()
        model.postComment(param, file, {
            liveData.value = Resource.create(it)
        }, {
            liveData.value = Resource.create(it)
        })
        return liveData

    }
}

class ComplaintRepositories(private val mComDis: CompositeDisposable) : MVVMBaseModel() {

    /**
     * 投诉
     */
    fun postComplaint(
        params: Parameters,
        file: List<String>,
        onSuccessBr: onSuccessBr,
        onFailed: onFailed
    ) {
        val param = mutableListOf<Pair<String, Any?>>()
        param.addAll(params)
        //如果上传有文件的话
        val disposable = if (!file.isNullOrEmpty()) {
            val reqFiles = file.map { File(it) }
            val uploadImage = http?.upload(ApiService.UPLOAD_ICON, reqFiles)!!.toObservable()
            uploadImage.compose(RxScheduler.applyScheduler())
                .flatMap {
                    val typeToken = object : TypeToken<BR<List<UploadRespSrc>>>() {}.type
                    val jsonResp = GsonUtil.str2Obj<BR<List<UploadRespSrc>>>(it, typeToken)
                    if (jsonResp?.code == 0 && !jsonResp.data.isNullOrEmpty()) {
                        param.add("enclosure" to listSplit(jsonResp.data))
                        return@flatMap http?.post(
                            ApiService.COMPLAINT_SUGGEST,
                            param
                        )!!.toObservable()
                    } else {
                        return@flatMap null
                    }
                }.parse<BR<String>>(strType, {
                    onSuccessBr.invoke(it)
                }, {
                    onFailed.invoke(it)
                })
        } else {
            //没有文件的提交
            http!!.post(ApiService.COMPLAINT_SUGGEST, params)
                .parse2<BR<String>>(strType, {
                    onSuccessBr.invoke(it)
                }, {
                    onFailed.invoke(it)
                })

        }
        mComDis.add(disposable)
    }

    fun reportQuery(
        phone: String,
        onSuccessT: onSuccessT<BR<List<ReportResultResp>>>,
        onFailed: onFailed
    ) {
        val type = object : TypeToken<BR<List<ReportResultResp>>>() {}.type
        http!!.getWithoutLoading(ApiService.REPORT_QUERY_RESULT, listOf("phone" to phone))
            .parse<BR<List<ReportResultResp>>>(type, {
                onSuccessT(it)
            }, {
                onFailed(it)
            })


    }


    fun fetchBanner(onSuccessT: onSuccessT<BR<List<BannerResp>>>, onFailed: onFailed) {
        val type = object : TypeToken<BR<List<BannerResp>>>() {}.type
        http?.getWithoutLoading(ApiService.BANNER)
            ?.parse<BR<List<BannerResp>>>(type, {
                onSuccessT(it)
            }, {
                onFailed(it)
            })
    }


    /**
     *进行评价
     */
    fun postComment(
        params: Parameters,
        file: List<String>,
        onSuccessBr: onSuccessBr,
        onFailed: onFailed
    ) {
        val paramss = mutableListOf<Pair<String, Any?>>()
        paramss.addAll(params.toMutableList())


        //如果上传有文件的话
        val disposable = if (!file.isNullOrEmpty()) {
            val reqFiles = file.map { File(it) }
            val uploadImage = http?.upload(ApiService.UPLOAD_ICON, reqFiles)!!.toObservable()
            uploadImage.compose(RxScheduler.applyScheduler())
                .flatMap {
                    val typeToken = object : TypeToken<BR<List<UploadRespSrc>>>() {}.type
                    val jsonResp = GsonUtil.str2Obj<BR<List<UploadRespSrc>>>(it, typeToken)
                    paramss.add("url" to listSplit(jsonResp?.data))
                    return@flatMap http?.post(ApiService.ADD_COMMENT_URL, paramss)!!.toObservable()
                }.parse<BR<String>>(strType, {
                    onSuccessBr.invoke(it)
                }, {
                    onFailed.invoke(it)
                })
        } else {
            //没有文件的提交
            http!!.post(ApiService.ADD_COMMENT_URL, params)
                .parse2<BR<String>>(strType, {
                    onSuccessBr.invoke(it)
                }, {
                    onFailed.invoke(it)
                })

        }
        mComDis.add(disposable)
    }

    private fun listSplit(list: List<UploadRespSrc?>?): String {
        val stringBuilder = StringBuilder()
        if (!list.isNullOrEmpty()) {
            stringBuilder.append(list[0]?.src)
            list.forEachIndexed { index, uploadRespSrc ->
                if (index > 0) {
                    stringBuilder.append(",").append(uploadRespSrc?.src)
                }
            }
        }
        return stringBuilder.toString()
    }

}
