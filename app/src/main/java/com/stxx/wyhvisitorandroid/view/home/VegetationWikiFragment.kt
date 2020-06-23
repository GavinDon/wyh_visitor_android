package com.stxx.wyhvisitorandroid.view.home

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.gavindon.mvvm_lib.utils.getStatusBarHeight
import com.gyf.immersionbar.ImmersionBar
import com.stxx.wyhvisitorandroid.R
import com.stxx.wyhvisitorandroid.base.ToolbarFragment
import com.stxx.wyhvisitorandroid.bean.VegetationWikiResp
import com.stxx.wyhvisitorandroid.graphics.ImageLoader
import com.stxx.wyhvisitorandroid.mplusvm.VegetationWikiVm
import com.stxx.wyhvisitorandroid.navOption
import com.stxx.wyhvisitorandroid.transformer.RoundedCornersTransformation
import kotlinx.android.synthetic.main.fragment_vegetaion_wiki.*
import kotlinx.android.synthetic.main.toolbar.*

/**
 * description:景区百科
 * Created by liNan on 2020/3/9 10:01

 */
class VegetationWikiFragment : ToolbarFragment() {
    override val toolbarName: Int = R.string.grid_plant_wiki
    override val layoutId: Int = R.layout.fragment_vegetaion_wiki
    override val mStatusViewId: Int = R.id.vegetationStatus

    private lateinit var mAdapter: VegetationWikiAdapter
    private lateinit var vegetationWikiVm: VegetationWikiVm

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        vegetationWikiVm = getViewModel()
        lifecycle.addObserver(vegetationWikiVm)
        mAdapter = VegetationWikiAdapter(R.layout.adapter_vegetation_wiki, null)
        rvVegetationWiki.adapter = mAdapter
        loadData()
        frame_layout_title.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.white
            )
        )
        refreshVegetationWiki.setOnRefreshListener {
            loadData()
        }

        mAdapter.setOnItemClickListener { adapter, _, position ->
            if (adapter.getItem(position) == null) {
                return@setOnItemClickListener
            }
            findNavController()
                .navigate(
                    R.id.fragment_scenic_news_detail,
                    bundleOf("detail" to adapter.getItem(position)),
                    navOption
                )
        }
    }

    override fun setStatusBar() {
        titleBar.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
        titleBar.layoutParams.height = getStatusBarHeight(requireContext())
        ImmersionBar.with(this)
            .fitsSystemWindows(false)
            .statusBarDarkFont(true)
            .transparentStatusBar()
            .init()
    }

    private fun loadData() {
        mStatusView?.hideAllView()
        vegetationWikiVm.getWiki().observe(this, Observer {
            handlerResponseData(it, { resp ->
                mAdapter.setNewInstance(resp.data.toMutableList())
                refreshVegetationWiki.isRefreshing = false
            }, {
                if (mAdapter.data.isNullOrEmpty()) {
                    mStatusView?.setOnClickListener {
                        loadData()
                    }
                }
                refreshVegetationWiki.isRefreshing = false

            })
        })
    }

}

class VegetationWikiAdapter(layoutResId: Int, data: MutableList<VegetationWikiResp>?) :
    BaseQuickAdapter<VegetationWikiResp, BaseViewHolder>(layoutResId, data) {
    override fun convert(holder: BaseViewHolder, item: VegetationWikiResp) {
        holder.setText(R.id.adaTvVegetationTitle, item.name)
            .setText(R.id.adaTvVegetationOpenTime, item.content)
            .setText(R.id.adaTvVegetationTip, item.synopsis)
//            .setText(R.id.adaTvScenicLevel, item.name)
        ImageLoader.with().load(item.img).transForm(RoundedCornersTransformation(20, 0))
            .into(holder.getView(R.id.adaIvVegetationWiki))
    }
}