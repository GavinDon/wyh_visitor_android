package com.gavindon.mvvm_lib.net

import com.gavindon.mvvm_lib.utils.Parameters
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import java.io.File

/**
 * description: http请求接口
 * 可以使用不同的框架来实现get、post等请求
 * 实现此接口来进行切换
 * Created by liNan on  2019/12/19 10:19
 */

interface IHttpRequest {
    val compositeDisposable: CompositeDisposable

    /**
     * get请求
     * @param url 请求地址
     * @param param 请求参数
     */
    fun get(url: String, param: Parameters? = null): Single<String>

    fun post(url: String, param: Parameters? = null): Single<String>
    fun postWithoutLoading(url: String, param: Parameters? = null): Single<String>


    fun upload(url: String, files: List<File>, param: Parameters? = null): Single<String>

    /*不带加载框*/
    fun getWithoutLoading(url: String, param: Parameters? = null): Single<String>

    /*多个请求同时进行*/
    fun <T> getMerge(vararg args: Observable<T>)

    fun getSingle(
        url: String,
        param: Parameters? = null,
        method: Method = Method.GET
    ): Single<String>


}

interface IFuelHttp : IHttpRequest {
}





