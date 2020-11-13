package com.gavindon.mvvm_lib.net

import android.annotation.SuppressLint
import com.gavindon.mvvm_lib.utils.GsonUtil
import com.gavindon.mvvm_lib.utils.Parameters
import com.gavindon.mvvm_lib.utils.onFailed
import com.gavindon.mvvm_lib.utils.onSuccessT
import com.github.kittinunf.fuel.core.FileDataPart
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.fuel.httpUpload
import com.github.kittinunf.fuel.rx.rxResponseString
import com.google.gson.JsonSyntaxException
import com.orhanobut.logger.Logger
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.lang.reflect.Type


/**
 * description:
 * Created by liNan on  2019/12/19 10:27
 */
class FuelHttp private constructor() : IFuelHttp {


    private val mCompositeDisposable: CompositeDisposable by lazy { CompositeDisposable() }


    companion object {
        val instance: FuelHttp by lazy(LazyThreadSafetyMode.PUBLICATION) {
            FuelHttp()
        }
    }

    override val compositeDisposable: CompositeDisposable = mCompositeDisposable


    override fun get(url: String, param: Parameters?): Single<String> {
        return url.httpGet(param)
            .rxResponseString()
            .compose(RxScheduler.applySingleScheduler())
            .compose(RxScheduler.applySingleLoading())
    }


    override fun post(url: String, param: Parameters?): Single<String> {
        return url.httpPost(param)
            .rxResponseString()
            .compose(RxScheduler.applySingleScheduler())
            .compose(RxScheduler.applySingleLoading())
    }

    override fun postWithoutLoading(url: String, param: Parameters?): Single<String> {
        return url.httpPost(param)
            .rxResponseString()
            .compose(RxScheduler.applySingleScheduler())
    }

    override fun upload(url: String, files: List<File>, param: Parameters?): Single<String> {
        val dataParts = files.map { FileDataPart(it) }

        /*url.httpUpload().add(dataParts.toTypedArray().iterator().next())
            .rxResponseString()
            .compose(RxScheduler.applySingleScheduler())
            .compose(RxScheduler.applySingleLoading())*/


        return url.httpUpload().plus(dataParts).rxResponseString()
            .compose(RxScheduler.applySingleScheduler())
            .compose(RxScheduler.applySingleLoading())
    }


    override fun getWithoutLoading(url: String, param: Parameters?): Single<String> {
        return url.httpGet(param)
            .rxResponseString()
            .compose(RxScheduler.applySingleScheduler())
    }

    override fun <T> getMerge(vararg args: Observable<T>) {
        val observables = args.toList()
        Observable.merge(observables).observeOn(Schedulers.io())
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribe()
    }

    override
    fun getSingle(url: String, param: Parameters?, method: Method): Single<String> {
        return if (method.value == Method.GET.value) {
            url.httpGet(param).rxResponseString()
        } else {
            url.httpPost(param).rxResponseString()
        }

    }
}

@SuppressLint("CheckResult")
inline fun <reified T> Single<String>.parse(
    type: Type,
    crossinline onSuccess: onSuccessT<T>,
    crossinline onFailed: onFailed
) {
    this.subscribe({
        val p = GsonUtil.str2Obj<T>(it, type)
        if (null != p) {
            //反参解析正常
            onSuccess(p)
        } else {
            //如果返回解析数据为null则代表json解析异常
            onFailed(JsonSyntaxException("json异常"))
        }
        Logger.json(it)

    }, {
        Logger.e(it, "http异常")
        onFailed(it)
    })
}

inline fun <reified T> Single<String>.parse2(
    type: Type,
    crossinline onSuccess: onSuccessT<T>,
    crossinline onFailed: onFailed
): Disposable {
    return subscribe({
        val p = GsonUtil.str2Obj<T>(it, type)
        //反参解析正常
        if (p != null) {
            onSuccess.invoke(p)

        } else {
            //如果返回解析数据为null则代表json解析异常
            onFailed(JsonSyntaxException("json异常"))
        }
        Logger.json(it)

    }, {
        Logger.e(it, "http异常")
        onFailed(it)
    })
}

/**
 * 投诉建议 使用
 */
inline fun <reified T> Observable<String>.parse(
    type: Type,
    crossinline onSuccess: onSuccessT<T>,
    crossinline onFailed: onFailed
): Disposable {
    return this.subscribe({
        val p = GsonUtil.str2Obj<T>(it, type)
        if (null != p) {
            //反参解析正常
            onSuccess(p)
        } else {
            //如果返回解析数据为null则代表json解析异常
            onFailed(JsonSyntaxException("json异常"))
        }
        Logger.json(it)

    }, {
        Logger.e(it, "http返回异常")
        onFailed(it)
    })

}



