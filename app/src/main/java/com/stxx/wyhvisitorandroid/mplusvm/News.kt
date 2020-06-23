package com.stxx.wyhvisitorandroid.mplusvm

import com.gavindon.mvvm_lib.base.MVVMBaseModel
import com.gavindon.mvvm_lib.base.MVVMBaseViewModel
import com.gavindon.mvvm_lib.net.*
import com.gavindon.mvvm_lib.utils.onFailed
import com.gavindon.mvvm_lib.utils.onSuccessT
import com.gavindon.mvvm_lib.utils.singLiveData
import com.google.gson.reflect.TypeToken
import com.stxx.wyhvisitorandroid.ApiService
import com.stxx.wyhvisitorandroid.bean.PushMessageResp
import com.stxx.wyhvisitorandroid.pageSize
import io.reactivex.disposables.CompositeDisposable

/**
 * description:新闻资讯
 * Created by liNan on  2020/3/5 10:51
 */
class NewsVm : MVVMBaseViewModel() {

    private val repositories = NewsRepositories(mComDis)
    private val pushMessageMoreData = singLiveData<List<PushMessageResp>>()
    val pushMessageData = singLiveData<List<PushMessageResp>>()

    /**
     * 消息
     */
    fun getPushMessage(
        pageNumber: Int = 1
    ): singLiveData<List<PushMessageResp>> {
        repositories.fetchMessage(pageNumber, pageSize, {
            pushMessageData.value = Resource.create(it)
        }, {
            pushMessageData.value = Resource.create(it)
        })
        return pushMessageData
    }

    fun getPushMessageMore(pageNumber: Int): singLiveData<List<PushMessageResp>> {
        repositories.fetchMessage(pageNumber, pageSize, {
            pushMessageMoreData.value = Resource.create(it)
        }, {
            pushMessageMoreData.value = Resource.create(it)
        })
        return pushMessageMoreData
    }

}

class NewsRepositories(private val mComDis: CompositeDisposable) : MVVMBaseModel() {


    fun fetchMessage(
        pageNumber: Int,
        pageSize: Int,
        onSuccessT: onSuccessT<BR<List<PushMessageResp>>>,
        onFailed: onFailed
    ) {
        val param = listOf("pageNumber" to pageNumber, "pageSize" to pageSize)
        val type = object : TypeToken<BR<List<PushMessageResp>>>() {}.type

        mComDis.add(
            http!!.getWithoutLoading(ApiService.PUSH_MESSAGE, param)
                .parse2<BR<List<PushMessageResp>>>(type, {
                    onSuccessT(it)
                }, {
                    onFailed(it)
                })
        )
    }

}

