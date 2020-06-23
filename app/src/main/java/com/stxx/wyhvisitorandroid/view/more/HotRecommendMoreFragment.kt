package com.stxx.wyhvisitorandroid.view.more

import android.os.Bundle
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.gavindon.mvvm_lib.net.SuccessSource
import com.gavindon.mvvm_lib.utils.getStatusBarHeight
import com.gavindon.mvvm_lib.utils.singLiveData
import com.gyf.immersionbar.ImmersionBar
import com.squareup.picasso.Picasso
import com.stxx.wyhvisitorandroid.*
import com.stxx.wyhvisitorandroid.base.ToolbarFragment
import com.stxx.wyhvisitorandroid.bean.HotRecommendResp
import com.stxx.wyhvisitorandroid.mplusvm.HomeVm
import com.stxx.wyhvisitorandroid.transformer.RoundedCornersTransformation
import kotlinx.android.synthetic.main.comment_recyclerview.*
import kotlinx.android.synthetic.main.toolbar.*

/**
 * description:热门推荐列表
 * Created by liNan on  2020/4/15 16:01
 */
class HotRecommendMoreFragment : ToolbarFragment() {
    override val toolbarName: Int = R.string.str_hot_record
    override val layoutId: Int = R.layout.comment_recyclerview

    private val mAdapter by lazy {  HotRecommendMoreAdapter(R.layout.card_home_news, null) }

    private lateinit var mViewModel: HomeVm

    //加载第几页
    private var pageNum: Int = 1
    private var dataCount: Int = 0
    private lateinit var liveData: singLiveData<List<HotRecommendResp>>

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        mViewModel = getViewModel()
        lifecycle.addObserver(mViewModel)
        commentRv?.adapter = mAdapter
        swipeRefresh.setOnRefreshListener {
            loadFirstPageData()
        }
        loadFirstPageData()
        mAdapter.setOnItemClickListener { adapter, _, position ->
            findNavController()
                .navigate(
                    R.id.fragment_scenic_news_detail,
                    bundleOf(BUNDLE_DETAIL to adapter.getItem(position)),
                    navOption
                )
        }
        swipeRefresh?.setColorSchemeResources(R.color.colorTabSelect)
        requestMoreData()
    }

    private fun loadFirstPageData() {
        liveData = mViewModel.hotLiveData
        val liveData = liveData.value

        if (!mAdapter.data.isNullOrEmpty()) {
            swipeRefresh.isRefreshing = false
            return
        }
        if (liveData != null && liveData is SuccessSource) {
            mAdapter.setList(liveData.body.data.toMutableList())
            dataCount = liveData.body.count
            swipeRefresh.isRefreshing = false
        } else {
            swipeRefresh?.showRefreshIcon()
            mViewModel.getHotRecommend().observe(this, Observer {
                handlerResponseData(it, { resp ->
                    dataCount = resp.count
                    pageNum++
                    mAdapter.setList(resp.data.toMutableList())
                }, {
                    if (mAdapter.data.isNullOrEmpty()) {
                        loadFirstPageData()
                    }
                })
                swipeRefresh.isRefreshing = false
            })
        }
    }

    /**
     * 加载更多
     */
    private fun requestMoreData() {
        mAdapter.loadMoreModule.setOnLoadMoreListener {
            if (pageNum < dataCount.pageTotalNum) {
                swipeRefresh.isEnabled = false
                mViewModel.getHotRecommendMore(pageNum).observe(this, Observer {
                    handlerResponseData(it, { resp ->
                        pageNum++
                        mAdapter.addData(resp.data)
                        mAdapter.loadMoreModule.loadMoreComplete()
                        swipeRefresh.isEnabled = true
                    }, {
                        mAdapter.loadMoreModule.loadMoreFail()
                        swipeRefresh.isEnabled = true
                    })
                })

            } else {
                //所有数据加载完成
                mAdapter.loadMoreModule.loadMoreEnd()
            }
        }
    }

    override fun setStatusBar() {
        frame_layout_title.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.white
            )
        )
        titleBar.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
        titleBar.layoutParams.height = getStatusBarHeight(this.requireContext())
        ImmersionBar.with(this)
            .fitsSystemWindows(false)
            .statusBarDarkFont(true)
            .transparentStatusBar()
            .init()
    }

}


class HotRecommendMoreAdapter(layoutResId: Int, data: MutableList<HotRecommendResp>?) :
    BaseQuickAdapter<HotRecommendResp, BaseViewHolder>(layoutResId, data), LoadMoreModule {
    override fun convert(holder: BaseViewHolder, t: HotRecommendResp) {

        holder.setText(R.id.tvNewsTitle, t.title)
            .setText(R.id.tvNewsContent, t.des)

        val iv = holder.getView<ImageView>(R.id.ivNewsPic)

        Picasso.get().load(t.imgurl).resizeDimen(R.dimen.dp_92, R.dimen.dp_82)
            .transform(RoundedCornersTransformation(20, 0)).into(iv)
    }
}