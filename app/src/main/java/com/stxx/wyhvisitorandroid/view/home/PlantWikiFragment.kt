package com.stxx.wyhvisitorandroid.view.home

import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView
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
import com.stxx.wyhvisitorandroid.bean.PlantWikiResp
import com.stxx.wyhvisitorandroid.graphics.ImageLoader
import com.stxx.wyhvisitorandroid.mplusvm.VegetationWikiVm
import com.stxx.wyhvisitorandroid.navOption
import com.stxx.wyhvisitorandroid.transformer.RoundedCornersTransformation
import kotlinx.android.synthetic.main.fragment_vegetaion_wiki.*
import kotlinx.android.synthetic.main.toolbar.*

/**
 * description:植物百科
 * Created by liNan on  2020/8/17 14:48
 */

class PlantWikiFragment : ToolbarFragment() {

    override val toolbarName: Int = R.string.grid_plant_wiki
    override val layoutId: Int = R.layout.fragment_vegetaion_wiki


    private val mAdapter: PlantWikiAdapter = PlantWikiAdapter(R.layout.adpater_plant_wiki, null)

    private lateinit var mViewModel: VegetationWikiVm

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        mViewModel = getViewModel()
        lifecycle.addObserver(mViewModel)
        rvVegetationWiki.adapter = mAdapter
        loadData()
        frame_layout_title.setBackgroundColor(Color.WHITE)
        refreshVegetationWiki.setColorSchemeResources(R.color.colorTabSelect)
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
        if (!mAdapter.data.isNullOrEmpty()) {
            return
        }
        mViewModel.getPlantWiki().observe(this, Observer {
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

class PlantWikiAdapter(layoutResId: Int, data: MutableList<PlantWikiResp>?) :
    BaseQuickAdapter<PlantWikiResp, BaseViewHolder>(layoutResId, data) {
    override fun convert(holder: BaseViewHolder, item: PlantWikiResp) {
        holder.setText(R.id.adaTvPlantWikiTitle, item.name)
            .setText(R.id.adaTvPlantWikiContent, item.introduction)

        val iv = holder.getView<ImageView>(R.id.adaIvPlantWiki)
        ImageLoader.with().load(item.imgurl).placeHolder(R.mipmap.banner)
            .transForm(
                RoundedCornersTransformation(
                    20,
                    0,
                    RoundedCornersTransformation.CornerType.TOP
                )
            )
            .into(iv)
    }

}