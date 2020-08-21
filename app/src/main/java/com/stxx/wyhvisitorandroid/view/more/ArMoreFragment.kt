package com.stxx.wyhvisitorandroid.view.more

import android.os.Bundle
import android.widget.ImageView
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.gavindon.mvvm_lib.net.SuccessSource
import com.orhanobut.logger.Logger
import com.stxx.wyhvisitorandroid.*
import com.stxx.wyhvisitorandroid.WebViewUrl.AR_720
import com.stxx.wyhvisitorandroid.base.ToolbarFragment
import com.stxx.wyhvisitorandroid.bean.Ar720Resp
import com.stxx.wyhvisitorandroid.graphics.ImageLoader
import com.stxx.wyhvisitorandroid.mplusvm.HomeVm
import kotlinx.android.synthetic.main.fragment_ar_more.*
import org.jetbrains.anko.support.v4.dip
import org.jetbrains.anko.support.v4.toast

/**
 * description:
 * Created by liNan on  2020/7/30 09:19
 */
class ArMoreFragment : ToolbarFragment() {
    override val toolbarName = R.string.full_ar
    override val layoutId = R.layout.fragment_ar_more

    private lateinit var mViewModel: HomeVm
    private val moreAdapter: ArMoreAdapter by lazy {
        ArMoreAdapter(R.layout.adapter_ar_more, null)
    }

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        mViewModel = getViewModel()

        //如果适配器有数据则不再去加载数据

        swipeRefresh?.setOnRefreshListener {
            loadData()
        }
        arMoreRv.adapter = moreAdapter
        moreAdapter.setOnItemClickListener { adapter, view, position ->
            val data = adapter.getItem(position) as Ar720Resp
            Logger.i("${AR_720}?id=${data.pid}")
            view.findNavController()
                .navigate(
                    R.id.fragment_webview, bundleOf(
                        WEB_VIEW_TITLE to data.name,
                        WEB_VIEW_URL to "${AR_720}${data.pid}"
                    ), navOption
                )
        }
        loadData()
    }

    private fun loadData() {
        if (!moreAdapter.data.isNullOrEmpty()) {
            swipeRefresh.isRefreshing = false
            return
        }
        val moreValue = mViewModel.arMoreLiveData.value
        if (moreValue is SuccessSource) {
            moreAdapter.setNewInstance((moreValue.body.data.toMutableList()))
            swipeRefresh.isRefreshing = false
            return
        }
        swipeRefresh?.showRefreshIcon()
        mViewModel.getArMOre().observe(this, Observer {
            handlerResponseData(it, { resp ->
                moreAdapter.setNewInstance(resp.data.toMutableList())
            }, {
                loadData()
            })
            swipeRefresh.isRefreshing = false
        })
    }

    inner class ArMoreAdapter(layoutResId: Int, data: MutableList<Ar720Resp>?) :
        BaseQuickAdapter<Ar720Resp, BaseViewHolder>(layoutResId, data) {
        override fun convert(holder: BaseViewHolder, item: Ar720Resp) {
            holder.setText(R.id.adaTvArName, item.name)
            val imageView = holder.getView<ImageView>(R.id.adaIvAr)
//            ImageLoader.with().load(item.imgurl).placeHolder(R.mipmap.banner).into(imageView)
            Glide.with(context).load(item.imgurl).placeholder(R.mipmap.banner).into(imageView)
        }
    }
}