package com.gavindon.mvvm_lib.net

/**
 * description:
 * Created by liNan on  2019/12/19 10:38
 */
enum class Method(val value: String) {
    GET("GET"),
    HEAD("HEAD"),
    POST("POST"),
    PUT("PUT"),
    DELETE("DELETE"),
    OPTIONS("OPTIONS"),
    TRACE("TRACE"),
    PATCH("PATCH"),
}