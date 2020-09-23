package com.stxx.wyhvisitorandroid.view.more

import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.text.HtmlCompat
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.gavindon.mvvm_lib.net.SuccessSource
import com.gavindon.mvvm_lib.utils.getStatusBarHeight
import com.gyf.immersionbar.ImmersionBar
import com.stxx.wyhvisitorandroid.*
import com.stxx.wyhvisitorandroid.base.ToolbarFragment
import com.stxx.wyhvisitorandroid.bean.LineRecommendResp
import com.stxx.wyhvisitorandroid.graphics.ImageLoader
import com.stxx.wyhvisitorandroid.mplusvm.HomeVm
import com.stxx.wyhvisitorandroid.transformer.RoundedCornersTransformation
import kotlinx.android.synthetic.main.comment_recyclerview.*
import kotlinx.android.synthetic.main.toolbar.*

/**
 * description:线路推荐更多页面
 * Created by liNan on  2020/4/15 15:14
 */
class LineRecommendFragment : ToolbarFragment() {
    override val toolbarName: Int = R.string.line_recommend
    override val layoutId: Int = R.layout.comment_recyclerview


    //加载第几页
    private var pageNum: Int = 1
    private var dataCount: Int = 0
    private lateinit var mViewModel: HomeVm
    //使adapter数据能够保存下来
    private val mAdapter by lazy { LineRecommendAdapter(R.layout.adapter_push_message, null) }

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        mViewModel = getViewModel()
//        lifecycle.addObserver(mViewModel)
        commentRv.adapter = mAdapter
        mAdapter.setOnItemClickListener { adapter, _, position ->
            findNavController().navigate(
                R.id.fragment_scenic_news_detail,
                bundleOf(BUNDLE_DETAIL to adapter.getItem(position)),
                navOption
            )
        }
        swipeRefresh.setOnRefreshListener {
            loadFirstData()
        }
        loadFirstData()
        requestMoreData()
        swipeRefresh?.setColorSchemeResources(R.color.colorTabSelect)

    }

    private fun loadFirstData() {
        val liveData = mViewModel.lineLiveData.value
        if (!mAdapter.data.isNullOrEmpty()) {
            swipeRefresh.isRefreshing = false
            return
        }
        if (liveData != null && liveData is SuccessSource) {
            mAdapter.setList(liveData.body.data.toMutableList())
            dataCount = liveData.body.count
            pageNum++
            swipeRefresh.isRefreshing = false
        } else {
            swipeRefresh?.showRefreshIcon()
            mViewModel.getLineRecommend().observe(this, Observer {
                handlerResponseData(it, { resp ->
                    dataCount = resp.count
                    mAdapter.setList(resp.data.toMutableList())
                }, {
                    if (mAdapter.data.isNullOrEmpty()) {
                        loadFirstData()
                    }

                })
                swipeRefresh?.isRefreshing = false
            })
        }

    }

    private fun requestMoreData() {
        mAdapter.loadMoreModule.setOnLoadMoreListener {
            //如果页数小于总页数可进行加载更多
            if (pageNum < dataCount.pageTotalNum) {
                swipeRefresh?.isEnabled = false
                mViewModel.getLineRecommendMore(pageNum).observe(this, Observer {
                    handlerResponseData(it, { resp ->
                        pageNum++
                        mAdapter.addData(resp.data)
                        mAdapter.loadMoreModule.loadMoreComplete()
                    }, {
                        mAdapter.loadMoreModule.loadMoreFail()
                    })
                    swipeRefresh?.isEnabled = true
                })
            } else {
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
        titleBar.setBackgroundColor(Color.WHITE)
        titleBar.layoutParams.height = getStatusBarHeight(this.requireContext())
        ImmersionBar.with(this)
            .fitsSystemWindows(false)
            .statusBarDarkFont(true)
            .transparentStatusBar()
            .init()
    }
}

class LineRecommendAdapter(layoutResId: Int, data: MutableList<LineRecommendResp>?) :
    BaseQuickAdapter<LineRecommendResp, BaseViewHolder>(layoutResId, data), LoadMoreModule {
    override fun convert(holder: BaseViewHolder, item: LineRecommendResp) {

        holder.setText(R.id.adaPushMessageTitle, item.title)
            .setText(
                R.id.adaPushMessageContent,
                HtmlCompat.fromHtml(item.content, HtmlCompat.FROM_HTML_MODE_COMPACT, null, null)
            )

        val imageView = holder.getView<ImageView>(R.id.adaIvPushMessage)

        ImageLoader.with().load(item.imgurl)
            .error(R.mipmap.banner)
            .placeHolder(R.mipmap.banner)
            .transForm(RoundedCornersTransformation(20, 0))
            .placeHolder(R.mipmap.banner)
            .into(imageView)
    }
}