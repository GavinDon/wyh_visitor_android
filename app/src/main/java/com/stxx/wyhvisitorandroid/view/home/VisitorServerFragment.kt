package com.stxx.wyhvisitorandroid.view.home

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.annotation.StringRes
import androidx.core.os.bundleOf
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gavindon.mvvm_lib.utils.GsonUtil
import com.google.gson.reflect.TypeToken
import com.gyf.immersionbar.ImmersionBar
import com.orhanobut.logger.Logger
import com.stxx.wyhvisitorandroid.*
import com.stxx.wyhvisitorandroid.adapter.VisitorServerGridAdapter
import com.stxx.wyhvisitorandroid.base.BaseFragment
import com.stxx.wyhvisitorandroid.bean.BannerResp
import com.stxx.wyhvisitorandroid.bean.VisitGridData
import com.stxx.wyhvisitorandroid.enums.ComplaintEnum
import com.stxx.wyhvisitorandroid.enums.ScenicMApPointEnum
import com.stxx.wyhvisitorandroid.graphics.ImageLoader
import com.stxx.wyhvisitorandroid.mplusvm.ComplaintVm
import kotlinx.android.synthetic.main.fragment_visitor_server.*
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

/**
 * description: 游客服务
 * Created by liNan on  2020/2/17 10:01
 */
class VisitorServerFragment : BaseFragment() {

    override val layoutId: Int = R.layout.fragment_visitor_server

    private val complaintVm: ComplaintVm by lazy { getViewModel<ComplaintVm>() }

    private val mGridAdapter: VisitorServerGridAdapter by lazy { VisitorServerGridAdapter(null) }

    private var lastOffset = 0

    override fun setStatusBar() {
        ImmersionBar.with(this)
            .transparentStatusBar()
            .statusBarDarkFont(false)
            .init()
    }

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        toolbar_back.setOnClickListener { it.findNavController().navigateUp() }

        if (mGridAdapter.getData().isNullOrEmpty()) {
            initData()
        } else {
            scrollVisitor.scrollY = lastOffset
        }
        resumePosition()
        rvVisitorServerGrid.adapter = mGridAdapter
        complaintVm.getBanner().observe(this, Observer {
            handlerResponseData(it, { resp ->
                bannerVisitServer.setData(resp.data, null)
                bannerVisitServer.setAdapter { _, itemView, model, _ ->
                    ImageLoader.with().load((model as BannerResp).imgurl)
                        .into((itemView as ImageView))
                }
            }, {})
        })
        mGridAdapter.addOnItemClickListener {
            when (it) {
                1 -> {
                    navigate(WebViewUrl.MEDICAL, R.string.visitor_server_yljz)
                }
                2 -> {
                    navigate(WebViewUrl.WZAFW, R.string.visitor_server_wzafu)
                }
                3 -> {
                    val token = judgeLogin()
                    if (token.isEmpty()) {
                        findNavController().navigate(R.id.login_activity, null, navOption)
                    } else {
                        findNavController().navigate(
                            R.id.action_report_result_query,
                            bundleOf("name" to ComplaintEnum.getItem(it)),
                            navOption
                        )
                    }
                }
                4 -> {
                    navigate(WebViewUrl.XRXW, R.string.visitor_server_xrxw)
                }
                5 -> {
                    navigate(WebViewUrl.ZAJB, R.string.visitor_server_zajb)
                }
                6 -> {
                    navigate(WebViewUrl.FWZX, R.string.visitor_server_khzx)
                }
                7 -> {
                    findNavController().navigate(
                        R.id.fragment_webview_primeval, bundleOf(
                            WEB_VIEW_URL to WebViewUrl.KEFU,
                            WEB_VIEW_TITLE to "客服"
                        )
                    )
                }
                //8 热力图
                8 -> {
                    navigate(WebViewUrl.HOT, R.string.visitor_server_klhot)
                }
                9 -> {
                    //景区导览
                    navigateMap(ScenicMApPointEnum.MAIN_SCENIC.ordinal)
                }
                10 -> {
                    //找厕所
                    navigateMap(ScenicMApPointEnum.TOILET.ordinal)
                }
                11 -> {
                    findNavController().navigate(
                        R.id.fragment_vegetation_wiki, null, navOption
                    )
                }
                12 -> {
                    navigate(WebViewUrl.WEATHER, R.string.visitor_server_weather)
                }
                13 -> {
                    findNavController().navigate(
                        R.id.fragment_webview, bundleOf(
                            "url" to WebViewUrl.VR,
                            "title" to R.string.str_ar
                        ), navOption
                    )
                }
                14 -> {
                    //线路推荐
                    findNavController().navigate(
                        R.id.fragment_line_recommend, null, navOption
                    )
                }
                15 -> {
                    //小机器人
                    findNavController().navigate(R.id.fragment_asr)
                }
                16 -> {
                    //步道
                    goAiBudaoPage(this.requireView())
                }
                17 -> {
                    navigate(WebViewUrl.DEVICE_QUERY, R.string.visitor_server_device)
                }
                18 -> {
                    navigate(WebViewUrl.LSCX, R.string.visitor_server_lscx)
                }
                19 -> {
                    navigate(WebViewUrl.GJCX, R.string.visitor_server_gjcx)
                }
                20 -> {
                    navigateMap(ScenicMApPointEnum.PARK.ordinal)
                }
                21 -> {
                    findNavController().navigate(R.id.fragment_one_key_wifi, null, navOption)
                }
            }
        }

    }

    /**
     * 记录view位置
     */
    private fun resumePosition() {
        scrollVisitor.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, _ ->
            lastOffset = scrollY
        })
    }

    private fun navigateMap(index: Int) {
        findNavController().navigate(
            R.id.fragment_scenic,
            bundleOf(BUNDLE_IS_ROBOT to true, BUNDLE_SELECT_TAB to index)
            , navOption
        )
    }

    private fun navigate(u: String, @StringRes title: Int) {
        findNavController().navigate(
            R.id.fragment_webview,
            bundleOf("url" to u, "title" to title),
            navOption
        )
    }

    private fun initData() {
        if (!mGridAdapter.getData().isNullOrEmpty()) {
            return
        }
        var inputStream: InputStream? = null
        var inputStreamReader: InputStreamReader? = null
        var bufferedReader: BufferedReader? = null

        try {
            inputStream = resources.assets.open("json/visit_server.json")
            inputStreamReader = InputStreamReader(inputStream)
            bufferedReader = BufferedReader(inputStreamReader)
            var strJson = bufferedReader.readLine()
            val stringBuilder = StringBuilder()
            while (strJson != null) {
                stringBuilder.append(strJson)
                strJson = bufferedReader.readLine()
            }
            val type = object : TypeToken<List<VisitGridData>>() {}.type
            val obj = GsonUtil.str2Obj<List<VisitGridData>>(stringBuilder.toString(), type)
            mGridAdapter.setList(obj?.toMutableList())
        } catch (ex: Exception) {
        } finally {
            inputStream?.close()
            inputStreamReader?.close()
            bufferedReader?.close()
        }
    }


}