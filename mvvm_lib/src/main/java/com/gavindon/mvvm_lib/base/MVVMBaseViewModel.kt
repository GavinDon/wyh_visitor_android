package com.gavindon.mvvm_lib.base

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ViewModel
import com.gavindon.mvvm_lib.net.IHttpRequest
import com.gavindon.mvvm_lib.net.http
import com.gavindon.mvvm_lib.utils.GsonUtil
import com.gavindon.mvvm_lib.utils.onFailed
import com.gavindon.mvvm_lib.utils.onSuccessT
import com.google.gson.JsonSyntaxException
import com.orhanobut.logger.Logger
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import java.lang.reflect.Type

/**
 * description:
 * Created by liNan on  2019/12/17 14:24
 */
abstract class MVVMBaseViewModel : ViewModel(), LifecycleObserver {

    protected val mComDis: CompositeDisposable = CompositeDisposable()


    override fun onCleared() {
        super.onCleared()
        //主activity onDestroy时回调
        if (!mComDis.isDisposed) {
            Logger.i(mComDis.toString())
            mComDis.clear()
        }
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onViewModelClear() {
        onCleared()
    }


}