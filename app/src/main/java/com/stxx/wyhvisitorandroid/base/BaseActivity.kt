package com.stxx.wyhvisitorandroid.base

import android.content.Context
import android.os.Bundle
import com.gavindon.mvvm_lib.base.BindContextViewModelFactory
import com.gavindon.mvvm_lib.base.MVVMBaseActivity
import com.gavindon.mvvm_lib.base.MVVMBaseViewModel
import com.gavindon.mvvm_lib.base.ViewModelProviders
import com.gavindon.mvvm_lib.net.*
import com.gavindon.mvvm_lib.utils.onSuccess
import com.gavindon.mvvm_lib.utils.onSuccessT
import com.gavindon.mvvm_lib.widgets.showToast
import com.gyf.immersionbar.ImmersionBar
import com.stxx.wyhvisitorandroid.R
import org.jetbrains.anko.toast

/**
 * description:
 * Created by liNan on  2019/12/17 14:09
 */
abstract class BaseActivity : MVVMBaseActivity() {


    /**
     * @T 只代表data 不包含code message
     */
    inline fun <reified T> handlerResponseData(
        resource: Resource<T>,
        onSuccess: onSuccessT<T>,
        crossinline onRetry: (() -> Unit?)
    ) {
        when (resource) {
            //数据正常返回ui显示
            is SuccessSource -> {
                onSuccess.invoke(resource.body)
            }
            //数据不正常统一会返回errorSource
            is ErrorSource -> {
                val ex = ExceptionHandle.handleException(resource.e)
                when (ex.errCode) {
                    ERROR.NETWORK_ERROR.getKey() -> {
                        mStatusView?.showRetryView {
                            onRetry.invoke()
                        }
                    }
                    ERROR.TIMEOUT_ERROR.getKey() -> {
                        mStatusView?.showRetryView {
                            onRetry.invoke()
                        }
                    }
                    else -> {
                        showToast(ex.errorMsg)
                    }
                }
            }
            is NotZeroSource -> {
                StatusException(resource.code, resource.msg)
                onRetry.invoke()
            }
        }
    }

    override val mStatusViewId: Int = R.id.statusView

    /**
     * 在java用泛型时不能够直接地使用类型。
     * 而在kotlin中使用inline配合reified可以使用泛型类型
     */
    inline fun <reified V : MVVMBaseViewModel> getViewModel(): V {
        return ViewModelProviders.of(this).get(V::class.java)
    }

    /**
     * 尽量使ViewModel不要引用activity/fragment
     * 带有context的viewModel
     */
    inline fun <reified V : MVVMBaseViewModel> getViewModel(context: Context): V {
        val factory = BindContextViewModelFactory.getInstance(context)
        return ViewModelProviders.of(this, factory).get(V::class.java)
    }


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setStatusBar()
    }

    open fun setStatusBar() {

    }

}