package com.stxx.wyhvisitorandroid.mplusvm

import androidx.lifecycle.MutableLiveData
import com.gavindon.mvvm_lib.base.MVVMBaseModel
import com.gavindon.mvvm_lib.base.MVVMBaseViewModel
import com.gavindon.mvvm_lib.net.*
import com.gavindon.mvvm_lib.net.Resource.Companion.create
import com.gavindon.mvvm_lib.utils.Parameters
import com.gavindon.mvvm_lib.utils.onFailed
import com.gavindon.mvvm_lib.utils.onSuccessT
import com.gavindon.mvvm_lib.utils.singLiveData
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.gson.gsonDeserializer
import com.github.kittinunf.fuel.rx.rxResponseObject
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import com.stxx.wyhvisitorandroid.ApiService
import com.stxx.wyhvisitorandroid.bean.*
import com.stxx.wyhvisitorandroid.judgeLogin
import com.stxx.wyhvisitorandroid.pageSize
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable

/**
 * description:
 * Created by liNan on 2020/3/13 11:06

 */
class HomeVm : MVVMBaseViewModel() {


    private val model: HomeRepositories = HomeRepositories(mComDis)
    private val homeLiveData = SingleLiveEvent<Resource<BR<List<*>>>>()

    //保存首页所有的数据,在切换页面时不再请求数据（pair（int,any）Int保存的是adapter的view ID）
    internal val homeData = mutableListOf<Pair<Int, Any>>()

    private val mCompositeDisposable: CompositeDisposable by lazy { CompositeDisposable() }


    fun getHomes(
        isRefresh: Boolean
    ): SingleLiveEvent<Resource<BR<List<*>>>> {
        if (homeData.isNotEmpty() && !isRefresh) {
            return homeLiveData
        }
        val token = judgeLogin()
        if (token.isNotEmpty())
            HttpManager.instance.addHeader("token" to token)
        val news = Fuel.get(ApiService.SCENIC_NEWS, listOf("pageNumber" to 1, "pageSize" to 3))
            .rxResponseObject(gsonDeserializer<BR<List<ScenicNewsResp>>>()).toObservable()
        /*  val banner =
              Fuel.get(ApiService.BANNER)
                  .rxResponseObject(gsonDeserializer<BR<List<BannerResp>>>()).toObservable()*/
        val horRecommend =
            Fuel.get(ApiService.HOT_RECOMMEND, listOf("pageNumber" to 1, "pageSize" to 5))
                .rxResponseObject(gsonDeserializer<BR<List<HotRecommendResp>>>()).toObservable()

        val line = Fuel.get(ApiService.VISIT_PARK_LINE, listOf("pageNumber" to 1, "pageSize" to 3))
            .rxResponseObject(gsonDeserializer<BR<List<LineRecommendResp>>>()).toObservable()


        val notice = Fuel.get(ApiService.NOTICE_URL)
            .rxResponseObject(gsonDeserializer<BR<List<NoticeResp>>>()).toObservable()

        val ar720 = Fuel.get(ApiService.AR720_URL, listOf("pageSize" to 3))
            .rxResponseObject(gsonDeserializer<BR<List<Ar720Resp>>>()).toObservable()




        mCompositeDisposable.add(
            //使用mergeDelayError处理合并请求当有错误发生时保留到list中
            // 直到所有数据发射完成再走onError
            Observable.mergeArrayDelayError(news, horRecommend, line, notice, ar720)
                .compose(RxScheduler.applyScheduler())
                .subscribe({
                    if (null != it) {
                        homeLiveData.value = create(it)
                    } else {
                        homeLiveData.value = create(JsonSyntaxException(""))
                    }
                }, {
                    homeLiveData.value = create(it)
                })
        )
        return homeLiveData
    }

    val liveDataBanner = MutableLiveData<List<Resource<BR<*>>>>()
    fun getTopBannerData(): MutableLiveData<List<Resource<BR<*>>>> {
        val weatherNow =
            Fuel.get(ApiService.WEATHER_NOW).rxResponseObject(gsonDeserializer<BR<WeatherResp>>())
                .toObservable()
        val lifestyle = Fuel.get(ApiService.WEATHER_LIFESTYLE)
            .rxResponseObject(gsonDeserializer<BR<List<WeatherLifestyle>>>())
            .toObservable()

//        val nowPe = Fuel.get(ApiService.REAL_TIME_NUM_TOTAL)
//            .rxResponseObject(gsonDeserializer<BR<RealPeopleNum>>()).toObservable()
        val pm25 = Fuel.get(ApiService.PM25).rxResponseObject(gsonDeserializer<BR<PM25Resp>>())
            .toObservable()
        val banner =
            Fuel.get(ApiService.BANNER)
                .rxResponseObject(gsonDeserializer<BR<List<BannerResp>>>()).toObservable()


        val source = mutableListOf<Resource<BR<*>>>()
        mCompositeDisposable.add(
            Observable.mergeDelayError(
                banner,
                weatherNow,
                lifestyle,
                pm25
            ).compose(RxScheduler.applyScheduler())
                .subscribe({
                    source.add(create(it))
                    if (source.size == 4) {
                        liveDataBanner.value = source
                    }
                }, {
                })
        )
        return liveDataBanner

    }

    val lineLiveData = singLiveData<List<LineRecommendResp>>()
    fun getLineRecommend(): singLiveData<List<LineRecommendResp>> {
        model.fetchLineRecommend(listOf("pageNumber" to 1, "pageSize" to pageSize), {
            lineLiveData.value = create(it)
        }, {
            lineLiveData.value = create(it)
        })
        return lineLiveData
    }

    fun getLineRecommendMore(pageNumber: Int): singLiveData<List<LineRecommendResp>> {
        val lineLiveDataMore = singLiveData<List<LineRecommendResp>>()
        model.fetchLineRecommend(listOf("pageNumber" to pageNumber, "pageSize" to pageSize), {
            lineLiveDataMore.value = create(it)
        }, {
            lineLiveDataMore.value = create(it)
        })
        return lineLiveDataMore
    }

    val newsLiveData = singLiveData<List<ScenicNewsResp>>()
    fun getNews(): singLiveData<List<ScenicNewsResp>> {
        model.fetchNews(listOf("pageNumber" to 1, "pageSize" to pageSize), {
            newsLiveData.value = create(it)
        }, {
            newsLiveData.value = create(it)
        })
        return newsLiveData
    }

    fun getNewsMore(pageNumber: Int): singLiveData<List<ScenicNewsResp>> {
        val newsMoreLiveData = singLiveData<List<ScenicNewsResp>>()
        model.fetchNews(listOf("pageNumber" to pageNumber, "pageSize" to pageSize), {
            newsMoreLiveData.value = create(it)
        }, {
            newsMoreLiveData.value = create(it)
        })
        return newsMoreLiveData
    }

    val hotLiveData = singLiveData<List<HotRecommendResp>>()
    fun getHotRecommend(pageNumber: Int = 1): singLiveData<List<HotRecommendResp>> {
        model.fetchHotRecommend(listOf("pageNumber" to pageNumber, "pageSize" to pageSize), {
            hotLiveData.value = create(it)
        }, {
            hotLiveData.value = create(it)
        })

        return hotLiveData
    }

    fun getHotRecommendMore(pageNumber: Int = 1): singLiveData<List<HotRecommendResp>> {
        val hotLiveDataMore = singLiveData<List<HotRecommendResp>>()
        model.fetchHotRecommend(listOf("pageNumber" to pageNumber, "pageSize" to pageSize), {
            hotLiveDataMore.value = create(it)
        }, {
            hotLiveDataMore.value = create(it)
        })

        return hotLiveDataMore
    }

    val arMoreLiveData = singLiveData<List<Ar720Resp>>()
    fun getArMOre(): singLiveData<List<Ar720Resp>> {
        model.fetchArMore({
            arMoreLiveData.value = create(it)
        }, {
            arMoreLiveData.value = create(it)
        })
        return arMoreLiveData
    }

    fun getLinePointById(id: Int): singLiveData<LineRecorderPointResp> {
        val livedata = singLiveData<LineRecorderPointResp>()
        model.fetchLinePoint(id, {
            livedata.value = create(it)
        }, {
            livedata.value = create(it)
        })
        return livedata
    }
}


class HomeRepositories(private val mComDis: CompositeDisposable) : MVVMBaseModel() {

    fun fetchLineRecommend(
        newsParam: Parameters,
        onSuccess: onSuccessT<BR<List<LineRecommendResp>>>,
        onFailed: onFailed
    ) {
        val typeToken = object : TypeToken<BR<List<LineRecommendResp>>>() {}.type
        mComDis.add(
            http!!.getWithoutLoading(ApiService.VISIT_PARK_LINE, newsParam)
                .parse2<BR<List<LineRecommendResp>>>(typeToken, {
                    onSuccess.invoke(it)
                }, {
                    onFailed(it)
                })
        )
    }

    fun fetchNews(
        newsParam: Parameters,
        onSuccess: onSuccessT<BR<List<ScenicNewsResp>>>,
        onFailed: onFailed
    ) {
        val typeToken = object : TypeToken<BR<List<ScenicNewsResp>>>() {}.type
        mComDis.add(
            http!!.getWithoutLoading(ApiService.SCENIC_NEWS, newsParam)
                .parse2<BR<List<ScenicNewsResp>>>(typeToken, {
                    onSuccess.invoke(it)
                }, {
                    onFailed(it)
                })
        )
    }

    fun fetchHotRecommend(
        param: Parameters,
        onSuccess: onSuccessT<BR<List<HotRecommendResp>>>,
        onFailed: onFailed
    ) {
        val typeToken = object : TypeToken<BR<List<HotRecommendResp>>>() {}.type
        mComDis.add(
            http!!.getWithoutLoading(ApiService.HOT_RECOMMEND, param)
                .parse2<BR<List<HotRecommendResp>>>(typeToken, {
                    onSuccess.invoke(it)
                }, {
                    onFailed(it)
                })
        )
    }

    /**
     * 获取Ar720资源
     */
    fun fetchArMore(onSuccess: onSuccessT<BR<List<Ar720Resp>>>, onFailed: onFailed) {
        val typeToken = object : TypeToken<BR<List<Ar720Resp>>>() {}.type
        mComDis.add(
            http!!.getWithoutLoading(ApiService.AR720_URL, listOf("pageSize" to 999))
                .parse2<BR<List<Ar720Resp>>>(typeToken, {
                    onSuccess(it)
                }, {
                    onFailed(it)
                })
        )

    }

    /**
     * 获取线路推荐经纬度
     */
    fun fetchLinePoint(
        id: Int,
        onSuccessT: onSuccessT<BR<LineRecorderPointResp>>,
        onFailed: onFailed

    ) {
        val type = object : TypeToken<BR<LineRecorderPointResp>>() {}.type
        mComDis.add(
            http!!.get(ApiService.LINE_POINT, listOf(Pair("id", id)))
                .parse2<BR<LineRecorderPointResp>>(type, {
                    onSuccessT.invoke(it)
                }, {
                    onFailed(it)
                })
        )
    }


}