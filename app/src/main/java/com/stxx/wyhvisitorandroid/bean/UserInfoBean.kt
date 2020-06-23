package com.stxx.wyhvisitorandroid.bean

/**
 * description:
 * Created by liNan on 2020/3/31 12:14

 */

data class UserInfoResp(
    var education: String,
    var email: String,
    var icon: String,
    var id: Int,
    var lastUpdAcct: String,
    var lastUpdTime: String,
    var name: String,
    var nationality: String,
    var note: String,
    var onlineStatus: String,
    var phone: String,
    var pwd: String,
    var salt2: String,
    var sex: String,
    var source: String,
    var status: String,
    var true_name: String?,
    var wxid: String
)

/**
 * 上传头像
 */
data class UploadResp(
    val id: Int,
    val src: String?,
    val title: String
)


data class UploadRespSrc(
    val id: Int,
    val src: String,
    val title: String
)