package com.stxx.wyhvisitorandroid.bean

/**
 * description: 热力图
 * Created by liNan on  2022-10-11 9:06
 */
data class HotDotResp(
    val adjustment: Int,
    val areaName: String,
    val cameraIndexCode: String,
    val flowInNum: Int,
    var flowOutNum: Int,
    val gmt_modified: String,
    val groupId: String,
    val groupName: String,
    val holdValue: String,
    val id: Int,
    val location: String,
    var region: Int,
    val threshold: Int,
    val update_by: Int,
    var x: String="0",
    var y: String="0"
)