package com.stxx.wyhvisitorandroid.bean

import java.io.Serializable

/**
 * description:游客服务请求结果
 * Created by liNan on 2020/3/16 16:55

 */


/*举报结果查询*/
data class ReportResultResp(
    val content: String,
    val createDate: String,
    val createUser: String,
    val ctime: String,
    val enclosure: String?,
    val id: Int,
    val mailbox: String,
    val phone: String,
    val position: String,
    val replyid: Any,
    val replyinfo: String,
    val replytime: String,
    val sources: String,
    val status: String,
    val suggested: String,
    val type: String,
    val updateDate: String,
    val updateUser: String
) : Serializable

/*展示游客服务网格数据*/
data class VisitGridData(val name: String, val iconRes: String, val showType: Int)



