package com.stxx.wyhvisitorandroid.bean

/**
 * description:
 * Created by liNan on  2020/5/6 17:53
 */

data class BrowseRecordBean(
    val `data`: List<DataX>,
    val time: String
)

data class DataX(
    val create_by: Int? = null,
    val explain: String? = null,
    val gmt_create: String? = null,
    val gmt_modified: String? = null,
    val id: Int? = null,
    val imgurl: String? = null,
    val introduction: String? = null,
    val is_deleted: String? = null,
    val name: String? = null,
    val position: String? = null,
    val time: String? = null,
    val type: String? = null,
    val update_by: Int? = null,
    val x: String? = null,
    val y: String? = null,
    var showType: Int = 1
)


data class CopyBrowRecordBean(
    var create_by: Int? = null,
    var explain: String? = null,
    var gmt_create: String? = null,
    var gmt_modified: String? = null,
    var id: Int? = null,
    var imgurl: String? = null,
    var introduction: String? = null,
    var is_deleted: String? = null,
    var name: String? = null,
    var position: String? = null,
    var time: String? = null,
    var type: String? = null,
    var update_by: Int? = null,
    var x: String? = null,
    var y: String? = null,
    //0表示时间，1表示内容
    var showType: Int = 1
)