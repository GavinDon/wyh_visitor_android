package com.stxx.wyhvisitorandroid.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.stxx.wyhvisitorandroid.R
import com.stxx.wyhvisitorandroid.bean.SearchAllScenicResp

/**
 * description:
 * Created by liNan on  2020/5/11 10:53
 */

class HistoryAdapter(layoutResId: Int, data: MutableList<Triple<Int, String, String>>?) :
    BaseQuickAdapter<Triple<Int, String, String>, BaseViewHolder>(layoutResId, data) {
    /**
     * triple first为景点id second为type third为name
     */
    override fun convert(holder: BaseViewHolder, item: Triple<Int, String, String>) {
        holder.setText(R.id.adaTvSearchHistory, item.third)
    }

}

class SearchAdapter(layoutResId: Int, data: MutableList<SearchAllScenicResp>?) :
    BaseQuickAdapter<SearchAllScenicResp, BaseViewHolder>(layoutResId, data) {
    override fun convert(holder: BaseViewHolder, item: SearchAllScenicResp) {
        holder.setText(R.id.adaTvResultName, item.name)
            .setText(R.id.adaTvResultPosition, item.position ?: "")
    }
}