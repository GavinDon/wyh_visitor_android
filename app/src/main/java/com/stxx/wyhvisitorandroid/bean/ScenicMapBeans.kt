package com.stxx.wyhvisitorandroid.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * description:
 * Created by liNan on 2020/4/1 17:10

 */


/*服务点*/
data class ServerPointResp(
    val create_by: Int,
    val explain: String?,
    val gmt_create: String,
    val gmt_modified: String,
    val id: Int,
    @SerializedName(value = "imgUrl", alternate = ["imgurl"])
    val imgurl: String,
    val introduction: String,
    val is_deleted: String,
    val position: String,
    var type: String?,
    val update_by: Int,
    val lngLat: List<String>,
    //共用
    val x: String? = "0",
    val y: String? = "0",
    val name: String,
    val location: String,
    //停车场
    val manager: String,
    val alarm_threshold: String,
    val corporation: String,
    val sum: String?,
    val residue: String?,
    val number: String,
    val details: String,
    val parkingFee: String?,
    val state: String,
    //厕所
    val thirdInfo: ToiletNumBean?,
    val manInfo: ToiletNumBean?,
    val woMenInfo: ToiletNumBean?,
    val occupation: String
) : Serializable

data class ToiletNumBean(
    val H2SThreshold: String,
    val NH3Threshold: String,
    val occupation: Int = 0,
    val status: String,
    val stayThreshold: String,
    val sum: Int = 0,
    val temperatureThreshold: String
)

/**
 * 停车场
 */
data class ParkPointResp(
    val alarm_threshold: Int,
    val camer_ids: String,
    val corporation: Any,
    val create_by: Any,
    val details: Any,
    val gmt_create: Any,
    val gmt_modified: String,
    val id: Int,
    val imgUrl: String,
    val is_deleted: Int,
    val location: String,
    val manager: Any,
    val name: String,
    val number: String,
    val region: Any,
    val residue: Int,
    val state: String,
    val sum: Int,
    val tel: Any,
    val type: String,
    val update_by: Int,
    val x: String,
    val y: String
)

/**
 * 景区服务详情
 */
data class ScenicServerResp(
    val content: String,
    val id: Int,
    val is_deleted: Int,
    val sort: String,
    val type: String
)


/**
 * 评论
 */
data class ScenicCommentResp(
    val create_date: String?,
    //评论内容
    val evaluate: String,
    val icon: String,
    val id: Int,
    val name: String,
    val score: String,
    val true_name: String?,
    val url: String?
)

