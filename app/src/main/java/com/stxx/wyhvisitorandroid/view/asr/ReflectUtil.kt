package com.stxx.wyhvisitorandroid.view.asr

import androidx.core.os.bundleOf
import androidx.navigation.NavController
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
//        navController.navigate(
//            R.id.fragment_scenic,
//            bundleOf(
//                BUNDLE_SELECT_TAB to ScenicMApPointEnum.PARK.ordinal,
//                BUNDLE_IS_ROBOT to true,
//                BUNDLE_NAV_NAME to funcName
//            ),
//            navOptions { popUpTo(R.id.fragment_scenic) { inclusive = true } }
//        )
        navigation(navController,ScenicMApPointEnum.PARK.ordinal,funcName)
    }

    fun navigation(navController: NavController,tab:Int,funcName: String){
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