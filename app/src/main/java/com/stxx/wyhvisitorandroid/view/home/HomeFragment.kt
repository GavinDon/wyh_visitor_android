package com.stxx.wyhvisitorandroid.view.home

import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.vlayout.DelegateAdapter
import com.alibaba.android.vlayout.VirtualLayoutManager
import com.alibaba.android.vlayout.layout.GridLayoutHelper
import com.alibaba.android.vlayout.layout.LinearLayoutHelper
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.gavindon.mvvm_lib.base.MVVMBaseApplication
import com.gavindon.mvvm_lib.net.ErrorSource
import com.gavindon.mvvm_lib.net.ExceptionHandle
import com.gavindon.mvvm_lib.net.SuccessSource
import com.gavindon.mvvm_lib.widgets.showToast
import com.gyf.immersionbar.ImmersionBar
import com.gyf.immersionbar.ImmersionBar.getStatusBarHeight
import com.huawei.hms.hmsscankit.ScanUtil
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions
import com.stxx.wyhvisitorandroid.*
import com.stxx.wyhvisitorandroid.adapter.*
import com.stxx.wyhvisitorandroid.base.BaseDelegateVH
import com.stxx.wyhvisitorandroid.base.BaseFragment
import com.stxx.wyhvisitorandroid.base.OnlyShowDelegateAdapter
import com.stxx.wyhvisitorandroid.bean.*
import com.stxx.wyhvisitorandroid.mplusvm.HomeVm
import com.stxx.wyhvisitorandroid.view.PushReceiveActivity
import io.reactivex.exceptions.CompositeException
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.title_bar.*


/**
 * description:
 * Created by liNan on 2020/1/14 8:49

 */
class HomeFragment : BaseFragment() {

    private lateinit var delegateAdapter: DelegateAdapter
    override val layoutId: Int = R.layout.fragment_home

    private val homeVm: HomeVm by lazy { getViewModel<HomeVm>() }

    private lateinit var bannerAdapter: BannerAdapter

    private lateinit var newsAdapter: NewsAdapters
    private lateinit var lineRecommendAdapter: LineRecommendAdapter


    private lateinit var hotRecommendAdapter: HotRecommendAdapter
    private lateinit var noticeAdapter: NoticeAdapter
    private lateinit var ar720Adapter: Ar720Adapter


    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        //建立委托LayoutManger
        val virtualLayoutManager =
            VirtualLayoutManager(MVVMBaseApplication.appContext)
        homeHoleRv.layoutManager = virtualLayoutManager
        val pool = RecyclerView.RecycledViewPool()
        homeHoleRv.setRecycledViewPool(RecyclerView.RecycledViewPool())
//        homeHoleRv.onFlingListener = null
        //防止来回滚动重建view
        pool.setMaxRecycledViews(R.layout.card_home_news, 10)
        pool.setMaxRecycledViews(R.layout.view_hot_record, 10)
        pool.setMaxRecycledViews(R.layout.view_home_banner, 10)
        pool.setMaxRecycledViews(R.layout.view_ar_visit, 10)
        /*delegateAdapter持有自定义virtualLayoutManage 代理recycleView adapter*/
        /*hasConsistItemType 子adapter中是否itemType一致*/
        delegateAdapter = DelegateAdapter(virtualLayoutManager, true)
        virtualLayoutManager.setRecycleOffset(10)
        virtualLayoutManager.initialPrefetchItemCount = 10
        homeHoleRv.adapter = delegateAdapter
        /*banner*/
        bannerAdapter =
            BannerAdapter(R.layout.view_home_banner)
        delegateAdapter.addAdapter(bannerAdapter)

        noticeAdapter = NoticeAdapter(R.layout.home_notice)
        delegateAdapter.addAdapter(noticeAdapter)

        /*grid*/
        val gridLayoutHelper = GridLayoutHelper(4)
        val gridAdapter = GridAdapter(R.layout.view_home_grid, gridLayoutHelper)
        gridLayoutHelper.setAutoExpand(true)
        gridAdapter.addItems(loadGridData())
        delegateAdapter.addAdapter(gridAdapter)

        /*hotRecommends*/
        hotRecommendAdapter = HotRecommendAdapter(R.layout.view_hot_record)
        delegateAdapter.addAdapter(hotRecommendAdapter)

        /*news*/
        val titleAdapter = TitleAdapter(R.string.str_news, R.string.str_news_sub)
        delegateAdapter.addAdapter(titleAdapter)

        val newsHelper = LinearLayoutHelper(12.dpToPx)
//        newsHelper.marginTop = 10.dpToPx
        newsAdapter = NewsAdapters(R.layout.card_home_news, newsHelper)
        delegateAdapter.addAdapter(newsAdapter)

        /*线路推荐*/
        val lineTitleAdapter =
            TitleAdapter(R.string.str_line_recommend, R.string.str_line_recommend_tip, true)
        delegateAdapter.addAdapter(lineTitleAdapter)

        val lineRecommendHelper = LinearLayoutHelper(12.dpToPx)
        lineRecommendAdapter = LineRecommendAdapter(R.layout.card_home_news, lineRecommendHelper)
        delegateAdapter.addAdapter(lineRecommendAdapter)


        /*AR游园*/
        ar720Adapter = Ar720Adapter(R.layout.view_ar_visit)
        delegateAdapter.addAdapter(ar720Adapter)

        //搜索
        llSearchDestination.setOnClickListener {
            findNavController().navigate(R.id.fragment_search, null, navOptions {
                anim {
                    enter = R.anim.alpha_enter
                    exit = R.anim.alpha_exit
                }
                launchSingleTop = true
                popUpTo(R.id.fragment_search) { inclusive = true }
            })
        }
        ibScanQr.setOnClickListener {
            ScanUtil.startScan(this.activity, SCAN_CODE, HmsScanAnalyzerOptions.Creator().create())
        }

        loadData()
        homeRefresh.run {
            setColorSchemeResources(R.color.colorTabSelect)
            setOnRefreshListener { loadData(true) }
        }

        ibMessage.setOnClickListener {
            findNavController().navigate(
                R.id.fragment_push_message, null,
                navOption
            )
        }
    }

    private fun loadGridData(): List<GridBean> {
        //AI步道
        //ar科普
        //植物百科
        //智慧停车
        //全景游园
        //虚拟游园
        //景区百科
        //ar科普 植物百科 智慧停车 全景游园 虚拟游园 景区百科  AI步道 游客服务
        return listOf(
            GridBean(R.string.str_enter_book, R.mipmap.grid_enter_book),
            GridBean(R.string.grid_smart_car, R.mipmap.grid_smart_cart),
            GridBean(R.string.grid_plant_wiki, R.mipmap.grid_plant_wiki),
            GridBean(R.string.full_ar, R.mipmap.grid_vr),
            GridBean(R.string.grid_ar_science, R.mipmap.grid_ar),
            GridBean(R.string.grid_scenic_wiki, R.mipmap.grid_scenic_wiki),
            GridBean(R.string.visitor_ai_budao, R.mipmap.grid_ar_budao),
            GridBean(R.string.grid_visit, R.mipmap.grid_visit_server)
        )
    }

    override fun setStatusBar() {
        titleBar?.layoutParams?.height = getStatusBarHeight(this)
        ImmersionBar.with(this)
            .fitsSystemWindows(false)
            .statusBarDarkFont(true)
            .init()
    }

    /**
     * @param isRefresh 是否是下拉刷新数据
     */
    private fun loadData(isRefresh: Boolean = false) {
        loadTopData(isRefresh)
        homeVm.getHomes(isRefresh).observe(this, Observer {
            if (it is SuccessSource) {
                val data = it.body.data
                when (data.first()) {
                    is ScenicNewsResp -> {
                        newsAdapter.addItems(data as List<ScenicNewsResp>, true)
                        homeVm.homeData.add(Pair(R.layout.view_home_news, data))
                    }
                    is BannerResp -> {
                        bannerAdapter.setSingerLayoutData(listOf(data as BannerResp))
                        homeVm.homeData.add(Pair(R.layout.view_home_banner, data))
                    }
                    is HotRecommendResp -> {
                        hotRecommendAdapter.setSingerLayoutData(data as List<HotRecommendResp>)
                        homeVm.homeData.add(Pair(R.layout.view_hot_record, data))
                    }
                    is LineRecommendResp -> {
                        lineRecommendAdapter.addItems(data as List<LineRecommendResp>, true)
                        homeVm.homeData.add(Pair(R.layout.view_home_grid, data))
                    }
                    is NoticeResp -> {
                        noticeAdapter.setSingerLayoutData(data as List<NoticeResp>)
                        homeVm.homeData.add(Pair(R.layout.home_notice, data))
                    }
                    is Ar720Resp -> {
                        ar720Adapter.setSingerLayoutData(data as List<Ar720Resp>)
                        homeVm.homeData.add(Pair(R.layout.view_ar_visit, data))
                    }
                }
            } else if (it is ErrorSource) {
                val e = it.e
                if (e is CompositeException) {
                    val ae = ExceptionHandle.handleException(e.exceptions[0])
                    this.context?.showToast(ae.errorMsg)
                }
            }
        })
        loadHasData()
    }

    private fun loadTopData(isRefresh: Boolean) {
        val bannerValue = homeVm.liveDataBanner.value
        if (isRefresh) {
            homeVm.getTopBannerData().observe(this, Observer {
                bannerAdapter.setSingerLayoutData(it)
            })
        } else {
            if (bannerValue?.size == 4) {
                bannerAdapter.setSingerLayoutData(bannerValue)
            } else {
                homeVm.getTopBannerData().observe(this, Observer {
                    bannerAdapter.setSingerLayoutData(it)
                })
            }
        }


    }

    /**
     * 加载ViewModel中已经请求过的数据
     * 不再重复请求
     */
    private fun loadHasData() {
        homeRefresh.isRefreshing = false
        val homeData = homeVm.homeData
        if (homeData.isNotEmpty()) {
            homeData.forEach {
                when (it.first) {
                    R.layout.view_home_news -> {
                        newsAdapter.addItems(it.second as List<ScenicNewsResp>, true)
                    }
                    R.layout.view_home_banner -> {
                        bannerAdapter.setSingerLayoutData(it.second as List<Any>)
                    }
                    R.layout.view_hot_record -> {
                        hotRecommendAdapter.setSingerLayoutData(it.second as List<HotRecommendResp>)
                    }
                    R.layout.view_home_grid -> {
                        lineRecommendAdapter.addItems(it.second as List<LineRecommendResp>, true)
                    }
                    R.layout.home_notice -> {
                        noticeAdapter.setSingerLayoutData(it.second as List<NoticeResp>)
                    }
                    R.layout.view_ar_visit -> {
                        ar720Adapter.setSingerLayoutData(it.second as List<Ar720Resp>)
                    }


                }
            }
        }
    }

    private val (Int).dpToPx: Int
        get() = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this.toFloat(),
            resources.displayMetrics
        ).toInt()

}