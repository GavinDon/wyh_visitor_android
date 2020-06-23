package com.stxx.wyhvisitorandroid.view.scenic

import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import com.baidu.mapapi.map.*
import com.baidu.mapapi.map.BaiduMap
import com.baidu.mapapi.map.MapStatus
import com.baidu.mapapi.model.LatLng
import com.baidu.mapapi.model.LatLngBounds
import com.gavindon.mvvm_lib.utils.getStatusBarHeight
import com.gyf.immersionbar.ImmersionBar
import com.orhanobut.logger.Logger
import com.stxx.wyhvisitorandroid.R
import com.stxx.wyhvisitorandroid.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_scenic_tab.*
import kotlinx.android.synthetic.main.title_bar.*
import java.io.InputStream
import java.nio.ByteBuffer


/**
 * description: viewpager的fragment
 * Created by liNan on  2020/2/7 14:09
 */
class ScenicTabFragment : BaseFragment(), BaiduMap.OnMapLoadedCallback {

    private var tileOverlay: TileOverlay? = null

    private val map: BaiduMap by lazy { mapView.map }
    private var mapLoaded = false

    override val layoutId: Int = R.layout.fragment_scenic_tab

    companion object {
        fun getInstance(str: String): ScenicTabFragment {
            val that = ScenicTabFragment()
            that.arguments = bundleOf(Pair("str", str))
            return that
        }
    }

    override fun setStatusBar() {
        titleBar?.layoutParams?.height = getStatusBarHeight(requireContext())
        ImmersionBar.with(this)
            .fitsSystemWindows(false)
            .statusBarDarkFont(true)
            .init()
    }

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
//        x5WebView.loadUrl("http://223.221.37.9:8082/app/index.html?name=1")
        map.setOnMapLoadedCallback(this)
        val builder = MapStatus.Builder()
        builder.zoom(17.0f)
        builder.target(LatLng(40.082681, 116.474134))
        val mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(builder.build())
        map.setMapStatus(mMapStatusUpdate)


        val tilePair = object : FileTileProvider() {
            override fun getMinDisLevel(): Int = 3

            override fun getMaxDisLevel(): Int = 21

            override fun getTile(x: Int, y: Int, z: Int): Tile? {
                Logger.i("$x==$y==$z")
                val filedir = "tiles/${z}/tile${x}_${y}.png"
                val bm = getFromAssets(filedir) ?: return null
                val tile = Tile(bm.width, bm.height, toRawData(bm))
                bm.recycle()
                return tile
            }
        }

        val options = TileOverlayOptions().apply {

            val northEast = LatLng(80.toDouble(), 180.toDouble())
            val southEast = LatLng((-80).toDouble(), (-180).toDouble())
            tileProvider(tilePair)
            setPositionFromBounds(LatLngBounds.Builder().include(northEast).include(southEast).build())
        }
        map.addTileLayer(options)

        if (mapLoaded) {
            val builders = LatLngBounds.Builder()
            builders.include(LatLng(40.099933, 116.464498))
                .include(LatLng(40.067047, 116.489129))
            map.setMaxAndMinZoomLevel(17.0f, 16.0f)
            map.setMapStatusLimits(builders.build())
            map.mapType = BaiduMap.MAP_TYPE_NONE
        }


    }


    private fun toRawData(bitmap: Bitmap): ByteArray? {
        val buffer: ByteBuffer = ByteBuffer.allocate(
            bitmap.width
                    * bitmap.height * 4
        )
        bitmap.copyPixelsToBuffer(buffer)
        val data: ByteArray = buffer.array()
        buffer.clear()
        return data
    }

    private fun getFromAssets(fileName: String): Bitmap? {
        val am: AssetManager = this.resources.assets
        val `is`: InputStream?
        val bm: Bitmap
        return try {
            `is` = am.open(fileName)
            bm = BitmapFactory.decodeStream(`is`)
            bm
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onMapLoaded() {
        mapLoaded = true
    }
}