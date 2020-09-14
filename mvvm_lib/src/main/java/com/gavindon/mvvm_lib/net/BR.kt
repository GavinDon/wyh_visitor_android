package com.gavindon.mvvm_lib.net

/**
 * description:
 * Created by liNan on  2019/12/19 10:14
 */
data class BR<T>(val code: Int, val msg: String, var data: T, val count: Int)

data class StrBR(val data: String)


