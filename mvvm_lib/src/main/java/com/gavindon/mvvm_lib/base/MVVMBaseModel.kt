package com.gavindon.mvvm_lib.base

import com.gavindon.mvvm_lib.base.my_interface.IModel
import com.gavindon.mvvm_lib.net.BR
import com.gavindon.mvvm_lib.utils.GsonUtil
import com.gavindon.mvvm_lib.widgets.showToast
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import io.reactivex.disposables.CompositeDisposable
import org.json.JSONException
import java.lang.reflect.Type

/**
 * description:
 * Created by liNan on  2019/12/17 17:10
 */
abstract class MVVMBaseModel : IModel {


    /*反参data为String的gson type*/
    val strType: Type = object : TypeToken<BR<String>>() {}.type

    fun <T> tipNoZero(br: BR<T>): Boolean {
        return if (br.code == 0) {
            true
        } else {
            MVVMBaseApplication.appContext.showToast(br.msg)
            false
        }
    }


    fun <T> deserialize(strJson: String, type: Type): T? {
        return try {
            GsonUtil.str2Obj<T>(strJson, type)
        } catch (ex: JsonSyntaxException) {
            null
        } catch (ex: JSONException) {
            null
        }
    }

}