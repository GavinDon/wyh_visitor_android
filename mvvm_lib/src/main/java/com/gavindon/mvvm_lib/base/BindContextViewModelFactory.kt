package com.gavindon.mvvm_lib.base

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.reflect.InvocationTargetException

/**
 * description:带有context的viewModel工厂
 * Created by liNan on 2019/12/30 9:23

 */
class BindContextViewModelFactory(private val context: Context) :
    ViewModelProvider.NewInstanceFactory() {


    companion object {
        private var sInstance: BindContextViewModelFactory? = null

        fun getInstance(context: Context): BindContextViewModelFactory? {
            if (sInstance == null) {
                sInstance = BindContextViewModelFactory(context)
            }
            return sInstance
        }

    }


    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        try {
            return modelClass.getConstructor(Context::class.java).newInstance(context)
        } catch (e: NoSuchMethodException) {
            throw  RuntimeException("不能创建 $modelClass 实例", e)
        } catch (e: IllegalAccessException) {
            throw  RuntimeException("不能创建 $modelClass 实例 ", e)
        } catch (e: InstantiationException) {
            throw  RuntimeException("不能创建 $modelClass 实例 ", e)
        } catch (e: InvocationTargetException) {
            throw  RuntimeException("不能创建 $modelClass 实例 $modelClass", e)
        }
    }
}