package com.stxx.wyhvisitorandroid

/**
 * description:
 * Created by liNan on  2019/12/23 16:59
 */

object ApiService {

    const val LOGIN = "tsvserviceapp/user/postLogin"
    const val LOGINOUT = "tsvserviceapp/user/logout"
    const val USER_INFO = "tsvserviceapp/user/profile"

    //发送验证码
    const val SEND_SMS_CODE = "tsvserviceapp/user/getSMSByPhone"

    //注册
    const val REGISTER = "tsvserviceapp/user/registered"

    //修改密码
    const val UPDATE_PASSWORD = "tsvserviceapp/user/updatepassword"

    //忘记密码
    const val FORGET_PASSWORD = "tsvserviceapp/user/forgetpassword"
    const val WRITE_OFF = "tsvserviceapp/user/cancellationUser"

    //上传头像 (先upload再update)
    const val UPLOAD_ICON = "system/savefile/uploadFiles"
    const val UPDATE_ICON = "tsvserviceapp/user/updatehead"

    //修改用户名
    const val UPDATE_NAME = "tsvserviceapp/user/updateProfile"

    //景区资讯
    const val SCENIC_NEWS = "complex/information/tableData"
    const val SCENIC_NEWS_ID = "complex/information/findByid"

    //消息
    const val PUSH_MESSAGE = "visitor/historicalpush/tableData"

    //景区百科
    const val SCENIC_WIKI = "scenic/findEncyclopedia"
    const val SCENIC_WIKI_ID = "scenic/findEncyclopediaById"

    //景区服务
    const val SCENIC_SERVER = "scenic/findScenicPoint"

    //投诉建议
    const val COMPLAINT_SUGGEST = "service/complaint/postAddApp"

    //举报结果查询
    const val REPORT_QUERY_RESULT = "service/complaint/findUserInfo"

    //首页轮播图
    const val BANNER = "complex/startuppage/tableData"

    //热门推荐
    const val HOT_RECOMMEND = "complex/popular/tableData"

    //根据id获取热门推荐详情
    const val HOT_RECOMMEND_ID = "complex/popular/findByid"

    //推荐路线
    const val VISIT_PARK_LINE = "tsv/routerecommendation/tableData"
    const val VISIT_PARK_LINE_ID = "tsv/routerecommendation/findByid"

    //电子地图服务点
    const val SCENIC_MAP_POINT = "complex/findSrvicePoint"
    const val SCENIC_MAP_POINT_ID = "complex/findSrvicePointById"

    //查询停车场 不传查全部0.通用停车场1.一类停车场2.二类停车场)
    const val PARK_LST_URL = "complex/findParkingInfo"
    const val PARK_LST_URL_ID = "complex/findParkingInfoById"

    //查询厕所
    const val TOILET_LST_URL = "complex/findToiletInfo"
    const val TOILET_LST_URL_ID = "complex/findToiletInfoById"

    //百度token获取
    const val BAIDU_TOKEN = "https://aip.baidubce.com/oauth/2.0/token"

    //使用百度unit 对话
    const val BAIDU_BOT = "https://aip.baidubce.com/rpc/2.0/unit/service/chat"

    //浏览记录
    const val BROWSE_LIST = "footprint/tableData"

    //服务点评论查询
    const val COMMENT_URL = "complex/evaluation/tableData"

    //添加评论
    const val ADD_COMMENT_URL = "complex/evaluation/postAdd"

    //删除评论
    const val DEL_COMMENT_URL = "complex/evaluation/delete"

    //修改评论
    const val UPDATE_COMMENT_URL = "complex/evaluation/postUpdate"

    //所有景点 提供查询景点使用
    const val SEARCH_ALL = "complex/search "

    //查询当天天气
    const val WEATHER_NOW = "complex/weather/findNew"
    const val WEATHER_FORECAST = "complex/weather/findWeatherForecast"
    const val REAL_TIME_NUM_TOTAL = "passengerFlow/findNumberOfVisitors"
    const val PM25 = "complex/weather/findPM25"
    const val WEATHER_LIFESTYLE = "complex/weather/findNewLifestyle"

    //通知公告
    const val NOTICE_URL = "publicInfo/publicInfoManage/noticeInfo"

    //获取720列表
    const val AR720_URL = "complex/panorama/tableData"
    const val FACE_REGISTER_URL =
        "https://aiot.ucanuup.cn/gw-front/externel/bj_wyh/face_register"

    //?education = xxx 认证成功之后更新服务器信息
    const val FACE_AUTH = "tsvserviceapp/user/updateEducation"

    //换取wx token
    const val WX_LOGIN = "tsvserviceapp/user/wxlogin"
    const val WX_USER_INFO = "https://api.weixin.qq.com/sns/userinfo"

    //绑定微信openId=xxx
    const val BIND_WX = "/tsvserviceapp/user/bindWxOpenIdApp"

    //phone=xxx   &  vcode=xx
    const val BIND_PHONE = "tsvserviceapp/user/bindPhone"

    //查询一键wifi 用户名和密码
    const val QUERY_WIFI = "tsvserviceapp/user/getDataList?type=WIFI"

    const val LINE_POINT = "tsv/routerecommendation/findByid"

    const val REAL_PASSAGER = "passengerFlow/realPassager"

    //phone =xx &vcode=xx
    const val UPDATE_PHONE = "tsvserviceapp/user/updatePhone"

}

object WebViewUrl {
    const val WEB_BASE = "https://tourist.wenyuriverpark.com/h5/#/"
//    const val WEB_BASE = "http://223.221.37.181:8082/h5/#/"
//    const val WEB_BASE = "https://tourist.wenyuriverpark.com/app"

    const val SHARE_URL = "${WEB_BASE}pages/road/details/details?id="

    //医疗求助
    const val MEDICAL = "pages/service/tourist/hospital?device=1"

    //无障碍服务
    const val WZAFW = "pages/service/tourist/barrierfree?device=1"

    //寻人寻物
    const val XRXW = "pages/service/results/rummage?device=1"

    //治安举报
    const val ZAJB = "pages/service/tourist/police?device=1"

    //服务咨询
    const val FWZX = "pages/service/tourist/advisory"

    //    const val KEFU =
//        "http://robot.easemob.com/webim/im.html?tenantId=54807&robotId=4d0c23d9-9187-4b62-8956-bdbb92a1b5a7"
    const val KEFU =
        "https://www.v5kf.com/public/chat/chat?sid=189667&entry=5&ref=link&accountid=2e4e303014657&theme=#F7D76B&delay=1-2"

    //景区天气
    const val WEATHER = "pages/service/weather/weather"

    //热力图
    const val HOT = "pages/service/hotMap/hotMap"

    //设备查询
    const val DEVICE_QUERY = "pages/device/device"

    //绿色出行
    const val LSCX = "pages/service/traffic/greentravel?device=1"

    //公交出行
    const val GJCX = "pages/service/traffic/bus?device=1"

    //停车场预约
    const val SMART_PARK = "pages/trip/home"

    //预约
    const val BOOK = "pages/appointment/appointment?device=1"

    //车辆信息
    const val CAR_INFO = "pages/user/trip/triptwo"


    /*三方*/
    const val AI_BUDAO = "https://aiot.ucanuup.cn/mapp/aioth5/?parkCode=WenYuHePark&phone="

    //全景游园
//    const val AR_720 = "http://manage.wenyuriverpark.com:8082/web/index720.html"
    const val AR_720 = "http://map.wenyuriverpark.com:8084/vr/index.html#node_"

    //虚拟游园
    const val VR = "http://map.wenyuriverpark.com:8084/3da"

    //植物百科
    const val PLANT_WIKI_URL = "https://qrcode.wenyuriverpark.com/qrcode/list.html"

    //入园预约
    const val ENTER_BOOK_URL = "pages/authentication/authentication"
    const val LINE_WEB = "http://manage.wenyuriverpark.com:8082/iosMap/map/record.html?id="

    //极光推送跳转h5 新闻详情页面
    const val NEWS_DETAIL = "pages/news/details/details?type=0&id="
}