package com.gavindon.mvvm_lib.base

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ViewModel
import com.orhanobut.logger.Logger
import io.reactivex.disposables.CompositeDisposable

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