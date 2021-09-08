package com.stxx.wyhvisitorandroid.bean

import java.io.Serializable

/**
 * description:
 * Created by liNan on  2020/8/29 10:18
 */

data class LocationBean(
    val id: String,
    val detailId: Int,
    val name: String,
    val x: String = "0",
    val y: String = "0",
    //路线
    val route: String = "",
    //适合游玩对象
    val suitble: String = ""
) : Serializable


//线路推荐
data class LineRecorderPointResp(
    val points: List<Point>,
    val tsvRouteRecommendation: TsvRouteRecommendation
)

data class Point(
    val create_by: Int,
    val explain: String,
    val gmt_create: String,
    val gmt_modified: String,
    val id: Int,
    val imgurl: String,
    val introduction: String,
    val is_deleted: String,
    val name: String,
    val position: String,
    val type: String,
    val update_by: Int,
    val x: String,
    val y: String
)

data class TsvRouteRecommendation(
    val approver: Any,
    val content: String,
    val create_by: Int,
    val des: String,
    val drafter: Any,
    val gmt_create: String,
    val gmt_modfy: String,
    val id: Int,
    val imgurl: String,
    val is_deleted: Int,
    val key_words: Any,
    val modify_by: Int,
    val route_id: String,
    val status: Any,
    val title: String,
    val type: Any
)
