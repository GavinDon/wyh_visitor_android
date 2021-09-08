package com.stxx.wyhvisitorandroid.mplusvm

import com.gavindon.mvvm_lib.base.MVVMBaseModel
import com.gavindon.mvvm_lib.base.MVVMBaseViewModel
import com.gavindon.mvvm_lib.net.BR
import com.gavindon.mvvm_lib.net.Resource
import com.gavindon.mvvm_lib.net.http
import com.gavindon.mvvm_lib.net.parse2
import com.gavindon.mvvm_lib.utils.Parameters
import com.gavindon.mvvm_lib.utils.onFailed
import com.gavindon.mvvm_lib.utils.onSuccessT
import com.gavindon.mvvm_lib.utils.singLiveData
import com.google.gson.reflect.TypeToken
import com.stxx.wyhvisitorandroid.ApiService
import com.stxx.wyhvisitorandroid.bean.ScenicCommentResp
import com.stxx.wyhvisitorandroid.bean.ServerPointResp
import com.stxx.wyhvisitorandroid.pageSize
import io.reactivex.disposables.CompositeDisposable

/**
 * description:电子地图
 * Created by liNan on 2020/4/1 16:08

 */

class ScenicVm : MVVMBaseViewModel() {

    private val model = ScenicRepositories(mComDis)

    val pointLiveData = singLiveData<List<ServerPointResp>>()
    private val pointLiveDataById = singLiveData<ServerPointResp>()


    fun getServicePoint(type: Int, url: String): singLiveData<List<ServerPointResp>> {
        model.fetchServicePoint(type, url, {
            pointLiveData.value = Resource.create(it)
        }, {
            pointLiveData.value = Resource.create(it)
        })
        return pointLiveData
    }

    fun getServicePointById(id: Int, url: String): singLiveData<ServerPointResp> {
        model.fetchServicePointById(id, url, {
            pointLiveDataById.value = Resource.create(it)
        }, {
            pointLiveDataById.value = Resource.create(it)
        })
        return pointLiveDataById
    }



    fun getComment(
        pageNumber: Int = 1,
        shopsId: Int
    ): singLiveData<List<ScenicCommentResp>> {
        val commentLiveData = singLiveData<List<ScenicCommentResp>>()
        val param =
            listOf("pageNumber" to pageNumber, "pageSize" to pageSize, "shops_id" to shopsId)
        model.fetchComment(param, {
            commentLiveData.value = Resource.create(it)
        }, {
            commentLiveData.value = Resource.create(it)

        })
        return commentLiveData

    }


}

class ScenicRepositories(private val mComDis: CompositeDisposable) : MVVMBaseModel() {

    fun fetchServicePoint(
        type: Int,
        url: String,
        onSuccessT: onSuccessT<BR<List<ServerPointResp>>>,
        onFailed: onFailed
    ) {
        val t = object : TypeToken<BR<List<ServerPointResp>>>() {}.type
        var param: List<Pair<String, Int>>? = null
        if (type >= 0) {
            param = listOf(Pair("type", type))
        }
        mComDis.add(
            http!!.get(url, param)
                .parse2<BR<List<ServerPointResp>>>(t, {
                    onSuccessT.invoke(it)
                }, {
                    onFailed.invoke(it)
                })
        )

    }

    fun fetchServicePointById(
        id: Int,
        url: String,
        onSuccessT: onSuccessT<BR<ServerPointResp>>,
        onFailed: onFailed
    ) {
        val t = object : TypeToken<BR<ServerPointResp>>() {}.type
        val param = listOf(Pair("id", id))
        mComDis.add(
            http!!.get(url, param)
                .parse2<BR<ServerPointResp>>(t, {
                    onSuccessT.invoke(it)
                }, {
                    onFailed.invoke(it)
                })
        )
    }


    /**
     * 获取评论
     */
    fun fetchComment(
        parameters: Parameters,
        onSuccessT: onSuccessT<BR<List<ScenicCommentResp>>>,
        onFailed: onFailed
    ) {
        val t = object : TypeToken<BR<List<ScenicCommentResp>>>() {}.type
        mComDis.add(
            http!!.get(ApiService.COMMENT_URL, parameters)
                .parse2<BR<List<ScenicCommentResp>>>(t, {
                    onSuccessT.invoke(it)
                }, {
                    onFailed.invoke(it)
                })
        )
    }
}
