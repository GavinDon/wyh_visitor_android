package com.stxx.wyhvisitorandroid.enums

/**
 * description:电子地图服务点请求type
 * Created by liNan on 2020/4/1 16:23

 */

enum class ScenicMApPointEnum {
    //主要景点
    MAIN_SCENIC,
    //特色展区
    FEATURES_AREA,
    //景区植物
    SCENIC_PLANT,
    //商铺
    SHOP,
    //服务区
    SERVICE_AREA,
    //厕所
    TOILET,
    //停车场
    PARK

}

/**
 * 卫生间
 * （0.男1.女2.第三卫生间3.无性别厕所）
 */
enum class ToiletTypeEnum {
    MAN,
    WOMAN,
    BARRIER_FREE_LAVATORY,
    PUBLIC_RESTROOM
}