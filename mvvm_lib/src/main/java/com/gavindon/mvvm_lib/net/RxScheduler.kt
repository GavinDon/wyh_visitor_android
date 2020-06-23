package com.gavindon.mvvm_lib.net

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.gavindon.mvvm_lib.R
import com.gavindon.mvvm_lib.base.MVVMBaseApplication
import io.reactivex.ObservableTransformer
import io.reactivex.SingleTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.matchParent

/**
 * description:
 * Created by liNan on  2019/12/19 11:26
 */

class RxScheduler {

    companion object {
        fun <T> applySingleScheduler(): SingleTransformer<T, T> {
            return SingleTransformer {
                it.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
            }
        }

        fun <T> applyScheduler(): ObservableTransformer<T, T> {
            return ObservableTransformer {
                it.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
            }
        }

        fun <T> applySingleLoading(): SingleTransformer<T, T> {
            val act = MVVMBaseApplication.getCurActivity()
            val layoutInflater =
                act?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val loadView = layoutInflater.inflate(R.layout.loading_dialog_view, null, false)
            act.window?.addContentView(
                loadView,
                FrameLayout.LayoutParams(matchParent, matchParent)
            )
            loadView.visibility = View.VISIBLE
            fun clearDialog() {
                loadView.visibility = View.GONE
                loadView.isClickable = false
                loadView.clearFocus()
            }
            return SingleTransformer {
                it.doOnError { clearDialog() }
                    .doOnDispose { clearDialog() }
                    .doOnSuccess { clearDialog() }
            }
        }

        fun <T> applyLoading(): ObservableTransformer<T, T> {
            val act = MVVMBaseApplication.getCurActivity()
            val layoutInflater =
                act?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val loadView = layoutInflater.inflate(R.layout.loading_dialog_view, null, false)
            act.window?.addContentView(
                loadView,
                FrameLayout.LayoutParams(matchParent, matchParent)
            )
            loadView.visibility = View.VISIBLE
            fun clearDialog() {
                loadView.visibility = View.GONE
                loadView.isClickable = false
                loadView.clearFocus()
            }
            return ObservableTransformer {
                it.doOnError { clearDialog() }
                    .doOnDispose { clearDialog() }
                    .doOnSubscribe { clearDialog() }
                    .doOnComplete { clearDialog() }
            }
        }

    }
}