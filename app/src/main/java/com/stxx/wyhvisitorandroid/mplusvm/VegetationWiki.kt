package com.stxx.wyhvisitorandroid.mplusvm

import com.gavindon.mvvm_lib.base.MVVMBaseModel
import com.gavindon.mvvm_lib.base.MVVMBaseViewModel
import com.gavindon.mvvm_lib.net.*
import com.gavindon.mvvm_lib.utils.onFailed
import com.gavindon.mvvm_lib.utils.onSuccessT
import com.gavindon.mvvm_lib.utils.singLiveData
import com.google.gson.reflect.TypeToken
import com.stxx.wyhvisitorandroid.ApiService
import com.stxx.wyhvisitorandroid.bean.PlantWikiResp
import com.stxx.wyhvisitorandroid.bean.VegetationWikiResp
import com.stxx.wyhvisitorandroid.enums.ScenicMApPointEnum
import io.reactivex.disposables.CompositeDisposable

/**
 * description:植物百科
 * Created by liNan on 2020/3/9 10:28

 */
class VegetationWikiVm : MVVMBaseViewModel() {

    private val vegetationWikiRepositories = VegetationWikiRepositories(mComDis)

    //景区百科
    private val liveData = singLiveData<List<VegetationWikiResp>>()

    //植物百科
    private val plantWIkiLiveData = singLiveData<List<PlantWikiResp>>()

    fun getWiki(pageNumber: Int = 1, pageSize: Int = 100): singLiveData<List<VegetationWikiResp>> {

        vegetationWikiRepositories.fetchVegetationWiki(pageNumber, pageSize, {
            liveData.value = Resource.create(it)
        }, {
            liveData.value = Resource.create(it)
        })

        return liveData
    }

    /**
     * 获取植物百科
     */
    fun getPlantWiki(): singLiveData<List<PlantWikiResp>> {
        vegetationWikiRepositories.fetchPlantWiki({
            plantWIkiLiveData.value = Resource.create(it)
        }, {
            plantWIkiLiveData.value = Resource.create(it)
        })
        return plantWIkiLiveData
    }


}

class VegetationWikiRepositories(private val mComDis: CompositeDisposable) : MVVMBaseModel() {

    /**
     * 获取植物百科
     */
    fun fetchVegetationWiki(
        pageNumber: Int = 1,
        pageSize: Int = 100,
        onSuccessT: onSuccessT<BR<List<VegetationWikiResp>>>,
        onFailed: onFailed
    ) {
        val type = object : TypeToken<BR<List<VegetationWikiResp>>>() {}.type
        val reqParam = listOf("pageNumber" to pageNumber, "pageSize" to pageSize)
        mComDis.add(
            http!!.get(ApiService.SCENIC_WIKI, reqParam)
                .parse2<BR<List<VegetationWikiResp>>>(type, {
                    onSuccessT(it)
                }, {
                    onFailed(it)
                })
        )
    }

    fun fetchPlantWiki(
        onSuccessT: onSuccessT<BR<List<PlantWikiResp>>>,
        onFailed: onFailed
    ) {
        val type = object : TypeToken<BR<List<PlantWikiResp>>>() {}.type
        mComDis.add(
            http!!.get(
                ApiService.SCENIC_MAP_POINT,
                listOf("type" to ScenicMApPointEnum.SCENIC_PLANT.ordinal + 1)
            )
                .parse2<BR<List<PlantWikiResp>>>(type, {
                    onSuccessT(it)
                }, {
                    onFailed(it)
                })
        )
    }

}

