package com.stxx.wyhvisitorandroid.mplusvm

import com.gavindon.mvvm_lib.base.MVVMBaseViewModel
import com.gavindon.mvvm_lib.net.Resource
import com.gavindon.mvvm_lib.utils.singLiveData
import com.stxx.wyhvisitorandroid.bean.ScenicCommentResp
import com.stxx.wyhvisitorandroid.bean.ServerPointResp
import com.stxx.wyhvisitorandroid.pageSize

/**
 * description:
 * Created by liNan on  2020/5/10 14:21
 */
class CommentViewModel : MVVMBaseViewModel() {
    private val model = ScenicRepositories(mComDis)
    private val pointLiveDataById = singLiveData<ServerPointResp>()

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