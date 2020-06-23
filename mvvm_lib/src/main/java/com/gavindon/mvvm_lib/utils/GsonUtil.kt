package com.gavindon.mvvm_lib.utils

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.stream.MalformedJsonException
import java.lang.reflect.Type

/**
 * description:json解析
 * Created by liNan on 2019/12/24 13:54
 */
class GsonUtil {

    companion object {

        private val gson: Gson by lazy(LazyThreadSafetyMode.PUBLICATION) { Gson() }

        fun <T> str2Obj(str: String, type: Type): T? {
            return try {
                gson.fromJson(str, type)
            } catch (ex: JsonSyntaxException) {
                null
            } catch (ex: MalformedJsonException) {
                null
            }
        }

        fun obj2Str(src: Any): String? {
            return gson.toJson(src)
        }
    }


}