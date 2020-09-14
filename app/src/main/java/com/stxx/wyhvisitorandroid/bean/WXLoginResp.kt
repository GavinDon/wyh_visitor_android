package com.stxx.wyhvisitorandroid.bean

import java.io.Serializable

/**
 * description:
 * Created by liNan on  2020/9/14 14:43
 */
data class WXLoginResp(
    val access_token: String?,
    val expires_in: Int,
    val openid: String?,
    val refresh_token: String,
    val scope: String,
    val unionid: String
) : Serializable

object WxOpenIdInfo {
    var wxLoginResp: WXLoginResp? = null
}