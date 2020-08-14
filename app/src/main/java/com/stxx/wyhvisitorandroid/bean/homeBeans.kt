package com.stxx.wyhvisitorandroid.bean

import java.io.Serializable

/**
 * description:
 * Created by liNan on 2020/3/13 14:58

 */

/*轮播图*/
data class BannerResp(
    val des: String,
    val id: Int,
    val imgurl: String,
    val is_deleted: Int,
    val name: String,
    val status: String,
    //当天天气
    val cloud: String,
    val cond_code: String,
    val cond_txt: String,
    val fl: String,
    val gmt_create: String,
    val hum: String,
    val pcpn: String,
    val pres: String,
    val tmp: String,
    val vis: String,
    val wind_deg: String,
    val wind_dir: String,
    val wind_sc: String,
    val wind_spd: String
)

/*新闻资讯*/
data class ScenicNewsResp(
    var content: String,
    var des: String,
    var id: Int,
    var imgurl: String,
    var key_words: String,
    var gmt_modfy: String,
    var modify_by: Int,
    var title: String,
    var type: String
) : Serializable

/*线路推荐*/
data class LineRecommendResp(
    var content: String,
    var id: Int,
    var imgurl: String,
    var key_words: String,
    var modify_by: Int,
    var gmt_modfy: String,
    var title: String,
    var type: String
) : Serializable

/*热门推荐*/
data class HotRecommendResp(
    var content: String?,
    var des: String,
    var id: Int,
    var gmt_modfy: String,
    var imgurl: String,
    var key_words: String,
    var modify_by: Int,
    var title: String,
    var type: String
) : Serializable

/**
 * 消息
 */
data class PushMessageResp(
    val content: String,
    val create_by: String,
    val des: String,
    val drafter: String,
    val gmt_create: String,
    val gmt_modfy: String,
    val id: Int,
    val imgurl: String,
    val key_words: String,
    val modify_by: String?,
    val push: String,
    val status: String,
    val title: String,
    val type: String
) : Serializable

data class VegetationWikiResp(
    val code: String,
    val content: String,
    val id: Int,
    val img: String,
    val is_deleted: Int,
    val name: String,
    val synopsis: String,
    val video: Any,
    val voice: Any
) : Serializable

/**
 * 当天天气
 */
data class WeatherResp(
    val cloud: String,
    val cond_code: String,
    val cond_txt: String,
    val fl: String,
    val gmt_create: String,
    val hum: String,
    val id: Int,
    val pcpn: String,
    val pres: String,
    val tmp: String,
    val vis: String,
    val wind_deg: String,
    val wind_dir: String,
    val wind_sc: String,
    val wind_spd: String
)

data class WeatherLifestyle(
    val brf: String,
    val gmt_create: String,
    val id: Int,
    val txt: String,
    val type: String
)

data class RealPeopleNum(
    val history_num_total: Int,
    val same_day_num_total: Int = 0,
    val real_time_num_total: Int = 0

)

data class NoticeResp(
    val content: String? = null,
    val createBy: Int? = null,
    val id: Int? = null,
    val isDeleted: Int? = null,
    val is_scroll: String? = null,
    val key_words: String? = null,
    val note: String? = null,
    val publisher: String? = null,
    val title: String? = null,
    val type: String? = null
) : Serializable

//720
data class Ar720Resp(val pid: Int, val imgurl: String, val name: String)

