package com.stxx.wyhvisitorandroid.mplusvm

import com.gavindon.mvvm_lib.base.MVVMBaseModel
import com.gavindon.mvvm_lib.base.MVVMBaseViewModel
import com.gavindon.mvvm_lib.net.*
import com.gavindon.mvvm_lib.utils.onFailed
import com.gavindon.mvvm_lib.utils.onSuccessT
import com.gavindon.mvvm_lib.utils.singLiveData
import com.google.gson.reflect.TypeToken
import com.stxx.wyhvisitorandroid.ApiService
import com.stxx.wyhvisitorandroid.bean.BrowseRecordBean
import com.stxx.wyhvisitorandroid.db.SqlHelper
import io.reactivex.disposables.CompositeDisposable

/**
 * description:浏览纪录
 * Created by liNan on  2020/5/6 17:57
 */
class BrowseRecordVm : MVVMBaseViewModel() {

    private val mModel: BrowseRecordRepository by lazy { BrowseRecordRepository(mComDis) }

    private val browseRecordLiveEvent = singLiveData<List<BrowseRecordBean>>()

    fun getBrowseData(): singLiveData<List<BrowseRecordBean>> {

        /*  fun fetch() {
              mModel.fetchBrowseData({
                  browseRecordLiveEvent.value = Resource.create(it)
              }, {
                  browseRecordLiveEvent.value = Resource.create(it)
              })

          }
          if (browseRecordLiveEvent.value == null) {
              fetch()
          } else if (browseRecordLiveEvent.value !is SuccessSource) {
              fetch()
          }*/
        mModel.fetchBrowseData({
            browseRecordLiveEvent.value = Resource.create(it)
        }, {
            browseRecordLiveEvent.value = Resource.create(it)
        })
        return browseRecordLiveEvent
    }

}

class BrowseRecordRepository(private val mComDis: CompositeDisposable) : MVVMBaseModel() {

    fun fetchBrowseData(onSuccessT: onSuccessT<BR<List<BrowseRecordBean>>>, onFailed: onFailed) {
        val type = object : TypeToken<BR<List<BrowseRecordBean>>>() {}.type
        mComDis.add(
            http!!.get(ApiService.BROWSE_LIST)
                .parse2<BR<List<BrowseRecordBean>>>(type, {
                    onSuccessT.invoke(it)
                }, {
                    onFailed.invoke(it)
                })
        )
    }


}