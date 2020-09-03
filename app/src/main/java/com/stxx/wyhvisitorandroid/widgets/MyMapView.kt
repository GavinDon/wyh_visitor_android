package com.stxx.wyhvisitorandroid.widgets

import android.content.Context
import com.baidu.mapapi.map.*
import com.baidu.mapapi.model.LatLng
import com.stxx.wyhvisitorandroid.SCENIC_CENTER_LATLNG
import com.stxx.wyhvisitorandroid.location.BdUtil


/**
 * description:地图的共用设置
 * Created by liNan on  2020/8/28 10:57
 */

fun MapView.init() {
    map?.setMaxAndMinZoomLevel(19f, 15f)
    map?.uiSettings?.isRotateGesturesEnabled=false
    showZoomControls(false)
}

fun MapView.customMap(context: Context) {
    val customFileNameGreen = "custom_bd_map.sty"
    // 默认开启个性化样式
    val customStyleFilePath =
        BdUtil.getCustomStyleFilePath(context, customFileNameGreen)
    setMapCustomStylePath(customStyleFilePath)
    setMapCustomStyleEnable(true)
    //在线加载
    val mapCustomStyleOptions = MapCustomStyleOptions()
    mapCustomStyleOptions.apply {
        customStyleId("a5d30240334ed1721316416e4d49ba05")
        localCustomStylePath(customStyleFilePath)
    }
    setMapCustomStyle(mapCustomStyleOptions, object : CustomMapStyleCallBack {
        override fun onPreLoadLastCustomMapStyle(p0: String?): Boolean {
            //默认返回false，由SDK内部处理加载逻辑；返回true则SDK内部不会做任何处理，由开发者自行完成样式加载。
            return false
        }

        override fun onCustomMapStyleLoadSuccess(p0: Boolean, p1: String?): Boolean {
            return false
        }

        override fun onCustomMapStyleLoadFailed(p0: Int, p1: String?, p2: String?): Boolean {
            return false
        }
    })
}

//地图移动到景区中心
fun MapView.fixedLocation2Center(latLng: LatLng = SCENIC_CENTER_LATLNG) {
    val mapStatus = MapStatus.Builder()
        .target(latLng)
        .build()
    val mapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mapStatus)
    this.map?.setMapStatus(mapStatusUpdate)
}

fun MapView.mapStatusBuild(target: LatLng = SCENIC_CENTER_LATLNG, zoom: Float = 16f) {
    val builder = MapStatus.Builder()
    builder.zoom(zoom)
    builder.target(target)
    val mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(builder.build())
    this.map?.setMapStatus(mMapStatusUpdate)
}

val urlTileProvider = object : UrlTileProvider() {
    override fun getMinDisLevel(): Int = 15
    override fun getMaxDisLevel(): Int = 19
    override fun getTileUrl(): String {
        return "http://223.70.181.106:8082/mapTiles/{z}/tile{x}_{y}.png"
    }
}
val overLayOptions = TileOverlayOptions().apply {
    val northEast = LatLng(40.06048593512643, 116.39524272759365)
    val southEast = LatLng(40.071822098761984, 116.46385409389569)
    tileProvider(urlTileProvider)
    //setPositionFromBounds(LatLngBounds.Builder().include(northEast).include(southEast).build())
}
