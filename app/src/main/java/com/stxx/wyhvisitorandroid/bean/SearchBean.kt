package com.stxx.wyhvisitorandroid.bean

/**
 * description:
 * Created by liNan on  2020/5/11 09:43
 */
data class SearchAllScenicResp(
    val id: Int,
    val introduction: String,
    val name: String,
    val position: String,
    val type: String,
    //把name转换成拼音
    var letterName: String? = null
)
