package com.gavindon.mvvm_lib.base

import android.app.Application
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.Factory

/**
 *
 * <p>activity必须是FragmentActivity的子类</p>
 *
 * description:viewModel提供类
 * Created by liNan on  2019/12/18 09:19
 */
class ViewModelProviders {

    companion object {
        private fun checkApplication(activity: FragmentActivity): Application {
            return activity.application ?: throw  IllegalStateException("activity=null")
        }

        private fun checkActivity(fragment: Fragment): FragmentActivity {
            return fragment.requireActivity()
        }

        fun of(activity: FragmentActivity) = this.of(activity, null)

        fun of(fragment: Fragment) = this.of(fragment, null)

        /**
         * fragment
         */
        fun of(fragment: Fragment, factory: Factory?): ViewModelProvider {
            val act = checkActivity(fragment)
            val app = checkApplication(act)
            val f = factory ?: ViewModelProvider.AndroidViewModelFactory.getInstance(app)
            return ViewModelProvider(act.viewModelStore, f)

        }

        /**
         * activity
         */
        fun of(activity: FragmentActivity, factory: Factory?): ViewModelProvider {
            val application = checkApplication(activity)
            val f = factory ?: ViewModelProvider.AndroidViewModelFactory.getInstance(application)
            return ViewModelProvider(activity.viewModelStore, f)
        }

    }
}