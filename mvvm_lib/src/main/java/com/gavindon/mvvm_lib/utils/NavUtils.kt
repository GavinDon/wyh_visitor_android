package com.gavindon.mvvm_lib.utils

import android.content.Context
import android.graphics.drawable.Drawable

/**
 * description:判断是否安装导航
 * Created by liNan on  2020/4/14 19:16
 */
class NavUtils {

    companion object {
        const val bd_package = "com.baidu.BaiduMap"
        const val gd_package = "com.autonavi.minimap"
        const val tx_package = "com.tencent.map"

        private val packageList = listOf(bd_package, gd_package, tx_package)
        /**
         * @return 返回系统中存在的导航app名称集合
         */
        fun checkInstallPackage(context: Context): MutableList<Pair<String, Drawable>> {
            val packageManager = context.packageManager
            val packageInfos = packageManager.getInstalledPackages(0)
            val newPackageName = mutableListOf<Pair<String, Drawable>>()
            for (item in packageList) {
                for (i in 0 until packageInfos.size) {
                    val packageName = packageInfos[i].packageName
                    if (packageName.contains(item)) {
                        val drawable = packageInfos[i].applicationInfo.loadIcon(packageManager)
                        newPackageName.add(Pair(packageName, drawable))
                        break
                    }
                }
            }
            //转换成
            val finalInfo = mutableListOf<String>()
            for (i in newPackageName) {
                when (i.first) {
                    bd_package -> {
                        finalInfo.add("百度地图")
                    }
                    gd_package -> {
                        finalInfo.add("高德地图")
                    }
                    tx_package -> {
                        finalInfo.add("腾讯地图")
                    }
                }
            }

            return newPackageName

        }
    }

}