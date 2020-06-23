package com.stxx.wyhvisitorandroid.view.mine

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.gavindon.mvvm_lib.net.SuccessSource
import com.stxx.wyhvisitorandroid.R
import com.stxx.wyhvisitorandroid.adapter.PushMessageAdapter
import com.stxx.wyhvisitorandroid.base.ToolbarFragment
import com.stxx.wyhvisitorandroid.mplusvm.NewsVm
import com.stxx.wyhvisitorandroid.navOption
import com.stxx.wyhvisitorandroid.pageTotalNum
import com.stxx.wyhvisitorandroid.showRefreshIcon
import kotlinx.android.synthetic.main.comment_recyclerview.*
import kotlinx.android.synthetic.main.fragment_push_message.*


/**
 * description:消息推送新闻
 * Created by liNan on 2020/4/9 9:02

 */
class PushMessageFragment : ToolbarFragment() {
    override val toolbarName: Int = R.string.push_message
    override val layoutId: Int = R.layout.fragment_push_message


    private lateinit var mViewModel: NewsVm
    //加载第几页
    private var pageNum: Int = 1
    private var dataCount: Int = 0

    private val mAdapter: PushMessageAdapter by lazy {
        PushMessageAdapter(
            R.layout.adapter_push_message,
            null
        )
    }

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        mViewModel = getViewModel()

        rvPushMessage?.adapter = mAdapter
        srlPushMessage?.setColorSchemeResources(R.color.colorTabSelect)

        srlPushMessage?.setOnRefreshListener {
            loadFirstData()
        }
        mAdapter.setOnItemClickListener { adapter, _, position ->
            findNavController().navigate(
                R.id.fragment_scenic_news_detail, bundleOf("detail" to adapter.getItem(position)),
                navOption
            )
        }
        loadFirstData()
        requestMoreData()
    }


    private fun loadFirstData() {
        val pushLiveValue = mViewModel.pushMessageData.value
        if (!mAdapter.data.isNullOrEmpty()) {
            srlPushMessage.isRefreshing = false
            return
        }

        if (pushLiveValue is SuccessSource) {
            mAdapter.setList(pushLiveValue.body.data)
            dataCount = pushLiveValue.body.count
            srlPushMessage.isRefreshing = false
        } else {
            srlPushMessage?.showRefreshIcon()
            mViewModel.getPushMessage().observe(this, Observer {
                handlerResponseData(it, { resp ->
                    dataCount = resp.count
                    mAdapter.setList(resp.data.toMutableList())
                }, {
                    if (mAdapter.data.isNullOrEmpty()) {
                        loadFirstData()
                    }
                })
                srlPushMessage.isRefreshing = false
            })
        }

    }

    private fun requestMoreData() {
        mAdapter.loadMoreModule.setOnLoadMoreListener {
            //如果页数小于总页数可进行加载更多
            if (pageNum < dataCount.pageTotalNum) {
                srlPushMessage?.isEnabled = false
                mViewModel.getPushMessageMore(pageNum).observe(this, Observer {
                    handlerResponseData(it, { resp ->
                        pageNum++
                        mAdapter.addData(resp.data)
                        mAdapter.loadMoreModule.loadMoreComplete()
                    }, {
                        mAdapter.loadMoreModule.loadMoreFail()
                    })
                    srlPushMessage?.isEnabled = true
                })
            } else {
                mAdapter.loadMoreModule.loadMoreEnd()
            }
        }
    }
}