package com.stxx.wyhvisitorandroid.bean

/**
 * description:一键wifi
 * Created by liNan on  2020/9/16 09:28
 */
data class OneKeyWifiResp(
    val name: String,
    val password: String
)

data class PushExtraData(val data: ExtralData)
data class ExtralData(val id: String,val information:String)