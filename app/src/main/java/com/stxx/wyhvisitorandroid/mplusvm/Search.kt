package com.stxx.wyhvisitorandroid.mplusvm

import com.gavindon.mvvm_lib.base.MVVMBaseViewModel
import com.gavindon.mvvm_lib.net.*
import com.gavindon.mvvm_lib.utils.singLiveData
import com.google.gson.reflect.TypeToken
import com.stxx.wyhvisitorandroid.ApiService
import com.stxx.wyhvisitorandroid.bean.SearchAllScenicResp

/**
 * description:
 * Created by liNan on  2020/5/9 16:46
 */
class SearchVm : MVVMBaseViewModel() {
    val allSearchLiveEvent = singLiveData<List<SearchAllScenicResp>>()

    fun fetchAllScenic(): singLiveData<List<SearchAllScenicResp>> {
        if (allSearchLiveEvent.value is SuccessSource) {
            return allSearchLiveEvent
        } else {
            val type = object : TypeToken<BR<List<SearchAllScenicResp>>>() {}.type
            http?.getWithoutLoading(ApiService.SEARCH_ALL)
                ?.parse<BR<List<SearchAllScenicResp>>>(type, {
                    allSearchLiveEvent.value = Resource.create(it)
                }, {
                    allSearchLiveEvent.value = Resource.create(it)
                })
        }
        return allSearchLiveEvent
    }


}

