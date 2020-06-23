package com.gavindon.mvvm_lib.net

/**
 * description:
 * Created by liNan on  2019/12/19 15:02
 */
class HttpProvider {

    companion object {
        fun createHttp(type: HttpFrame): IHttpRequest {
            return when (type) {
                HttpFrame.FUEL -> FuelHttp.instance
                HttpFrame.RETROFIT -> FuelHttp.instance
            }
        }
    }


}