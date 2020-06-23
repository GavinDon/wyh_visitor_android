package com.gavindon.mvvm_lib.net

import android.system.ErrnoException
import com.gavindon.mvvm_lib.base.MVVMBaseApplication
import com.gavindon.mvvm_lib.widgets.showToast
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.HttpException
import com.google.gson.JsonSyntaxException
import java.net.ConnectException
import java.net.NoRouteToHostException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLException


/**
 * description:异常处理
 * Created by liNan on 2020/1/3 16:03

 */
object ExceptionHandle {

    fun handleException(t: Throwable): AppException {
        val ex: AppException
        //如果是使用了fuelError则需要再获取一次cause
        var throwable = t.cause
        throwable = if (throwable is FuelError) {
            throwable.cause ?: throwable
        } else {
            throwable
        }

        when (throwable) {
            is HttpException -> {
                ex = AppException(ERROR.NETWORK_ERROR)
            }
            is SocketTimeoutException -> {
                //服务器响应的超时
                ex = AppException(ERROR.TIMEOUT_ERROR)
            }
            is ConnectException -> {
                ////连接服务器超时
                ex = AppException(ERROR.NETWORK_ERROR)
            }
            is UnknownHostException -> {
                //未知主机异常
                ex = AppException(ERROR.TIMEOUT_ERROR)
            }
            is NoRouteToHostException -> {
                //无法达到给定ip
                ex = AppException(ERROR.IP_ERROR)
            }
            is SSLException -> {
                //https证书出错
                ex = AppException(ERROR.SSL_ERROR)
            }
            is ErrnoException -> {
                //android.system 系统错误
                ex = AppException(ERROR.NETWORK_ERROR)
            }
            is TokenException -> {
                ex = AppException(ERROR.TOKEN_ERROR)
            }
            is JsonSyntaxException -> {
                ex = AppException(ERROR.JSON_ERROR)
            }
            else -> {
                ex = AppException(ERROR.UNKNOWN)
            }
        }
        return ex

    }
}

class AppException : Exception {
    var errorMsg: String
    var errCode: Int = 0

    constructor(errCodeInput: Int, error: String?) : super(error) {
        errorMsg = error ?: "请求失败，请稍后再试"
        errCode = errCodeInput
    }

    constructor(error: ERROR) {
        errCode = error.getKey()
        errorMsg = error.getValue()
    }
}

/**
 * 状态错误 非0状态
 */
class StatusException(status: Int, msg: String?) {

    init {
        when (status) {
            3 -> {
                MVVMBaseApplication.appContext.showToast(msg.toString())
            }
            1 -> {
                MVVMBaseApplication.appContext.showToast(msg.toString())
            }
        }
    }

}

enum class StatusError(private val code: Int) {
    UN_LOGIN(3),
}

enum class ERROR(private val code: Int, private val err: String) {

    /**
     * 未知错误
     */
    UNKNOWN(1000, "未知错误"),
    /**
     * 解析错误
     */
    PARSE_ERROR(1001, "解析错误"),
    /**
     * 网络错误
     */
    NETWORK_ERROR(1002, "网络错误,请检查网络是否正常"),

    /**
     * 协议出错
     */
    HTTP_ERROR(1003, "协议出错"),

    /**
     * 证书出错
     */
    SSL_ERROR(1004, "证书出错"),

    /**
     * IP错误不能到达服务器
     */
    IP_ERROR(1005, "IP错误不能到达服务器"),

    /**
     * 连接超时
     */
    TIMEOUT_ERROR(1006, "连接超时,请检查网络是否正常"),

    TOKEN_ERROR(1007, "token异常"),

    JSON_ERROR(1008, "数据异常");


    fun getValue(): String {
        return err
    }

    fun getKey(): Int {
        return code
    }

}