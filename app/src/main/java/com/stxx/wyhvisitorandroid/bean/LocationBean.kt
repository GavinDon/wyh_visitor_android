package com.stxx.wyhvisitorandroid.bean

import java.io.Serializable

/**
 * description:
 * Created by liNan on  2020/8/29 10:18
 */

data class LocationBean(
    val id: String,
    val name: String,
    val x: String = "0",
    val y: String = "0",
    //路线
    val route: String = "",
    //适合游玩对象
    val suitble: String = ""
) : Serializable
