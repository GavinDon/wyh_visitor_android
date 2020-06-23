package com.stxx.wyhvisitorandroid.base

import android.os.Bundle
import android.view.View
import com.gavindon.mvvm_lib.base.BindContextViewModelFactory
import com.gavindon.mvvm_lib.base.MVVMBaseFragment
import com.gavindon.mvvm_lib.base.MVVMBaseViewModel
import com.gavindon.mvvm_lib.base.ViewModelProviders
import com.gavindon.mvvm_lib.net.*
import com.gavindon.mvvm_lib.utils.onSuccessT
import com.gavindon.mvvm_lib.widgets.showToast
import com.stxx.wyhvisitorandroid.R
import com.stxx.wyhvisitorandroid.view.home.HomeFragment
import com.stxx.wyhvisitorandroid.view.mine.MineFragment
import com.stxx.wyhvisitorandroid.view.scenic.ScenicMapFragment
import com.stxx.wyhvisitorandroid.view.scenic.ScenicTabFragment
import kotlinx.android.synthetic.main.fragment_multi_root.*

/**
 * description:
 * Created by liNan on  2019/12/17 14:09
 */
abstract class BaseFragment : MVVMBaseFragment() {


    override fun onInit(savedInstanceState: Bundle?) {

        /* if (http?.compositeDisposable?.size() ?: 0 > 0) {
             com.orhanobut.logger.Logger.i("oninit clear===")
             http?.compositeDisposable?.dispose()
             Thread.sleep(10)
         }*/
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //防止快速返回时，fragment已经销毁context为null引发异常
        if (this.context != null) setStatusBar()
    }


    /**
     * @T 只代表data 不包含code message
     */
    inline fun <reified T> handlerResponseData(
        resource: Resource<T>,
        onSuccess: onSuccessT<T>,
        crossinline onRetry: () -> Unit
    ) {
        mStatusView?.hideAllView()
        when (resource) {
            //数据正常返回ui显示
            is SuccessSource -> {
                onSuccess.invoke(resource.body)
            }
            //data 中list数据为空
            is EmptySource -> {
                mStatusView?.showEmpty()
            }
            //数据不正常统一会返回errorSource
            is ErrorSource -> {
                val ex = ExceptionHandle.handleException(resource.e)
                this.context?.showToast(ex.errorMsg)
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
                }
            }
            //非0 code
            is NotZeroSource -> {
                StatusException(resource.code, resource.msg)
            }
        }
    }


    override fun onResume() {
        super.onResume()
        toggleShowBottomBarView()
    }

    private fun toggleShowBottomBarView() {
        when (this.javaClass.simpleName) {
            HomeFragment::class.java.simpleName,
            ScenicMapFragment::class.java.simpleName,
            ScenicTabFragment::class.java.simpleName,
            MineFragment::class.java.simpleName -> {
                activity?.bottomBarView?.visibility = View.VISIBLE
            }
            else -> {
                activity?.bottomBarView?.visibility = View.GONE
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

    inline fun <reified V : MVVMBaseViewModel> getContextViewModel(): V? {
        return if (this.context != null) {
            BindContextViewModelFactory.getInstance(this.context!!)!!.create(V::class.java)
        } else {
            null
        }
    }


    /**
     * 使用共用的title_bar可直接使用
     */
    abstract fun setStatusBar()


    override fun permissionForResult() {
    }


}