package com.stxx.wyhvisitorandroid.bean

/**
 * description:
 * Created by liNan on 2020/3/31 12:14

 */

data class UserInfoResp(
    //1是步道已认证
    var education: String,
    var email: String,
    var icon: String,
    var id: Int,
    var lastUpdAcct: String,
    var lastUpdTime: String,
    var name: String?,
    var nationality: String,
    var note: String,
    var onlineStatus: String,
    var phone: String?,
    var pwd: String,
    var salt2: String,
    var sex: String,
    var source: String,
    var status: String,
    var true_name: String?,
    var wxid: String?
)

/**
 * 获取微信的用户信息
 */
data class WxUserInfoResp(
    val city: String,
    val country: String,
    val headimgurl: String,
    val nickname: String,
    val openid: String,
    val privilege: List<String>,
    val province: String,
    val sex: Int,
    val unionid: String
)

/**
 * 上传头像
 */
data class UploadRespSrc(
    val id: Int,
    val src: String,
    val title: String
)

// 人脸识别
data class FaceIdentifyResp(val code: Int, val message: String)