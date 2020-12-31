package com.stxx.wyhvisitorandroid.view.asr

import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.navOptions
import com.stxx.wyhvisitorandroid.*
import com.stxx.wyhvisitorandroid.enums.ScenicMApPointEnum

/**
 * description:根据字符串反射调用方法
 * Created by liNan on  2020/4/23 16:55
 */

class ReflectUtil {


    fun listener(navController: NavController, keyWord: String) {
        when (keyWord) {
            "厕所", "卫生间" -> {
                navController.navigate(R.id.fragment_scenic)
            }
        }
    }

    /**
     * 主要景点
     */
    fun navMainViewPoint(navController: NavController, keyWord: String) {
        navController.navigate(
            R.id.fragment_scenic,
            bundleOf(
                BUNDLE_SELECT_TAB to ScenicMApPointEnum.MAIN_SCENIC.ordinal,
                BUNDLE_IS_ROBOT to true
            )
        )
    }

    /**
     * 主要景点
     */
    fun navMainScenicItem(navController: NavController, keyWord: String) {
        navController.navigate(
            R.id.fragment_scenic,
            bundleOf(
                BUNDLE_SELECT_TAB to ScenicMApPointEnum.MAIN_SCENIC.ordinal,
                BUNDLE_IS_ROBOT to true,
                BUNDLE_IS_SUB_SCENIC to true
            )
        )
    }

    /**
     * 导航到特色展区
     */
    fun navFeaturesArea(navController: NavController, keyWord: String) {
        navController.navigate(
            R.id.fragment_scenic,
            bundleOf(
                BUNDLE_SELECT_TAB to ScenicMApPointEnum.FEATURES_AREA.ordinal,
                BUNDLE_IS_ROBOT to true
            )
        )
    }

    /**
     * 导航景区植物
     */
    fun navScenicPlant(navController: NavController, keyWord: String) {

        navController.navigate(
            R.id.fragment_scenic,
            bundleOf(
                BUNDLE_SELECT_TAB to ScenicMApPointEnum.SCENIC_PLANT.ordinal,
                BUNDLE_IS_ROBOT to true
            )
        )
    }

    /**
     * 导航到商铺
     */
    fun navShop(navController: NavController, keyWord: String) {
        navController.navigate(
            R.id.fragment_scenic,
            bundleOf(
                BUNDLE_SELECT_TAB to ScenicMApPointEnum.SHOP.ordinal,
                BUNDLE_IS_ROBOT to true
            )
        )
    }

    /**
     * 服务区
     */
    fun navService(navController: NavController, keyWord: String) {
        navController.navigate(
            R.id.fragment_scenic,
            bundleOf(
                BUNDLE_SELECT_TAB to ScenicMApPointEnum.SERVICE_AREA.ordinal,
                BUNDLE_IS_ROBOT to true
            )
        )
    }

    /**
     * 导航到厕所
     */
    fun navToilet(navController: NavController, keyWord: String) {
        navController.navigate(
            R.id.fragment_scenic,
            bundleOf(
                BUNDLE_SELECT_TAB to ScenicMApPointEnum.TOILET.ordinal,
                BUNDLE_IS_ROBOT to true
            ),
            navOptions { popUpTo(R.id.fragment_scenic) { inclusive = true } }
        )
    }

    /**
     * 导航到停车场
     */
    fun navP1Park(navController: NavController, funcName: String) {
        navigation(navController, ScenicMApPointEnum.PARK.ordinal, funcName)
    }

    //玉湖
    fun navLake(navController: NavController, funcName: String) {
        navigation(navController, ScenicMApPointEnum.MAIN_SCENIC.ordinal, funcName)
    }

    //遐观悠悠
    fun navAlongview(navController: NavController, funcName: String) {
        navigation(navController, ScenicMApPointEnum.MAIN_SCENIC.ordinal, funcName)
    }

    //花溪锦田
    fun navHuaxiJintian(navController: NavController, funcName: String) {
        navigation(navController, ScenicMApPointEnum.MAIN_SCENIC.ordinal, funcName)
    }

    //礼弦桥
    fun navLixianbridge(navController: NavController, funcName: String) {
        navigation(navController, ScenicMApPointEnum.MAIN_SCENIC.ordinal, funcName)
    }

    //五柳观湖
    fun navWuLiuGuanHu(navController: NavController, funcName: String) {
        navigation(navController, ScenicMApPointEnum.MAIN_SCENIC.ordinal, funcName)
    }

    //东囿云稼
    fun navYunjiaintheEast(navController: NavController, funcName: String) {
        navigation(navController, ScenicMApPointEnum.MAIN_SCENIC.ordinal, funcName)
    }

    //柳岸长堤
    fun navWillowbanklevee(navController: NavController, funcName: String) {
        navigation(navController, ScenicMApPointEnum.MAIN_SCENIC.ordinal, funcName)
    }

    //草甸芳华
    fun navBeautifulmeadow(navController: NavController, funcName: String) {
        navigation(navController, ScenicMApPointEnum.MAIN_SCENIC.ordinal, funcName)
    }

    //曲河
    fun navMeanderingriver(navController: NavController, funcName: String) {
        navigation(navController, ScenicMApPointEnum.MAIN_SCENIC.ordinal, funcName)
    }

    //凭栏流水
    fun navRunningwaterbyrailing(navController: NavController, funcName: String) {
        navigation(navController, ScenicMApPointEnum.MAIN_SCENIC.ordinal, funcName)
    }

    //秋意广场
    fun navQiuyisquare(navController: NavController, funcName: String) {
        navigation(navController, ScenicMApPointEnum.MAIN_SCENIC.ordinal, funcName)
    }

    //望山阁
    fun navWangshanPavilion(navController: NavController, funcName: String) {
        navigation(navController, ScenicMApPointEnum.MAIN_SCENIC.ordinal, funcName)
    }

    //凤鸣台
    fun navFengmingplatfo(navController: NavController, funcName: String) {
        navigation(navController, ScenicMApPointEnum.MAIN_SCENIC.ordinal, funcName)
    }

    //朗昆自在书屋
    fun navLangkunziBookstore(navController: NavController, funcName: String) {
        navigation(navController, ScenicMApPointEnum.FEATURES_AREA.ordinal, funcName)
    }

    //阳光沙滩
    fun navSunnyBeach(navController: NavController, funcName: String) {
        navigation(navController, ScenicMApPointEnum.MAIN_SCENIC.ordinal, funcName)
    }

    //飞凤谷
    fun navFeifenggu(navController: NavController, funcName: String) {
        navigation(navController, ScenicMApPointEnum.MAIN_SCENIC.ordinal, funcName)
    }

    //松云华盖
    fun navSongyunhuagai(navController: NavController, funcName: String) {
        navigation(navController, ScenicMApPointEnum.MAIN_SCENIC.ordinal, funcName)
    }

    //飞瀑叠翠
    fun navCascadingnana(navController: NavController, funcName: String) {
        navigation(navController, ScenicMApPointEnum.MAIN_SCENIC.ordinal, funcName)
    }

    //芸上梯田
    fun navTerrace(navController: NavController, funcName: String) {
        navigation(navController, ScenicMApPointEnum.MAIN_SCENIC.ordinal, funcName)
    }

    //蒹葭照水
    fun navJianjia(navController: NavController, funcName: String) {
        navigation(navController, ScenicMApPointEnum.MAIN_SCENIC.ordinal, funcName)
    }

    //玲珑望月
    fun navLooking(navController: NavController, funcName: String) {
        navigation(navController, ScenicMApPointEnum.MAIN_SCENIC.ordinal, funcName)
    }

    //林廊听鸟
    fun navForestcorridor(navController: NavController, funcName: String) {
        navigation(navController, ScenicMApPointEnum.MAIN_SCENIC.ordinal, funcName)
    }

    //芦苇码头
    fun navReedWharf(navController: NavController, funcName: String) {
        navigation(navController, ScenicMApPointEnum.MAIN_SCENIC.ordinal, funcName)
    }

    //沁湖
    fun navQinhuLake(navController: NavController, funcName: String) {
        navigation(navController, ScenicMApPointEnum.MAIN_SCENIC.ordinal, funcName)
    }

    //望芸台
    fun navWangyuntai(navController: NavController, funcName: String) {
        navigation(navController, ScenicMApPointEnum.MAIN_SCENIC.ordinal, funcName)
    }

    //雁鸣岛
    fun navYanmingIslandna(navController: NavController, funcName: String) {
        navigation(navController, ScenicMApPointEnum.MAIN_SCENIC.ordinal, funcName)
    }

    //鸢尾园
    fun navIrisGarden(navController: NavController, funcName: String) {
        navigation(navController, ScenicMApPointEnum.MAIN_SCENIC.ordinal, funcName)
    }

    //月影泉
    fun navMoonshadowna(navController: NavController, funcName: String) {
        navigation(navController, ScenicMApPointEnum.MAIN_SCENIC.ordinal, funcName)
    }

    //茑屋
    fun navThehouse(navController: NavController, funcName: String) {
        navigation(navController, ScenicMApPointEnum.MAIN_SCENIC.ordinal, funcName)
    }

    //游客服务中心
    fun navTouristServiceCenter(navController: NavController, funcName: String) {
        navigation(navController, ScenicMApPointEnum.SERVICE_AREA.ordinal, funcName)
    }

    //望山咖啡
    fun navWangshancoffee(navController: NavController, funcName: String) {
        navigation(navController, ScenicMApPointEnum.SERVICE_AREA.ordinal, funcName)
    }

    /**
     * 游客服务
     */
    fun navTouristservices(navController: NavController, funcName: String) {
        navController.navigate(R.id.fragment_visitor_server, null, navOption)
    }

    /**
     * 游园须知
     */
    fun navNotice(navController: NavController, funcName: String) {
        navController.navigate(
            R.id.fragment_scenic_server_second,
            bundleOf("scenicServer" to 1, "scenicServerName" to R.string.visitor_know),
            navOption
        )
    }

    /**
     * 新闻资讯
     */
    fun navNews(navController: NavController, funcName: String) {
        navController.navigate(R.id.fragment_news_all, null, navOption)
    }


    fun navigation(navController: NavController, tab: Int, funcName: String) {
        navController.navigate(
            R.id.fragment_scenic,
            bundleOf(
                BUNDLE_SELECT_TAB to tab,
                BUNDLE_IS_ROBOT to true,
                BUNDLE_NAV_NAME to funcName,
                BUNDLE_IS_SUB_SCENIC to true
            ),
            navOptions { popUpTo(R.id.fragment_scenic) { inclusive = true } }
        )
    }


}