package com.stxx.wyhvisitorandroid.adapter

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseDelegateMultiAdapter
import com.chad.library.adapter.base.delegate.BaseMultiTypeDelegate
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.stxx.wyhvisitorandroid.R
import com.stxx.wyhvisitorandroid.bean.CopyBrowRecordBean
import com.stxx.wyhvisitorandroid.graphics.ImageLoader

/**
 * description:
 * Created by liNan on  2020/5/7 14:43
 */
class BrowseRecordAdapter(data: MutableList<CopyBrowRecordBean>?) :
    BaseDelegateMultiAdapter<CopyBrowRecordBean, BaseViewHolder>(data) {

    companion object {
        const val TITLE_TYPE = 0
        const val CONTENT_TYPE = 1
    }

    init {
        setMultiTypeDelegate(object : BaseMultiTypeDelegate<CopyBrowRecordBean>() {

            override fun getItemType(data: List<CopyBrowRecordBean>, position: Int): Int {
                return if (data[position].showType == 0) {
                    TITLE_TYPE
                } else {
                    CONTENT_TYPE
                }
            }
        })
        getMultiTypeDelegate()
            ?.addItemType(TITLE_TYPE, android.R.layout.simple_list_item_1)
            ?.addItemType(CONTENT_TYPE, R.layout.adapter_browse)
    }

    override fun convert(holder: BaseViewHolder, item: CopyBrowRecordBean) {
        if (holder.itemViewType == TITLE_TYPE) {
            holder.setText(android.R.id.text1, item.time)
        } else {
            holder.setText(R.id.adaTvRecordName, item.name)
            ImageLoader.with().load(item.imgurl).into(holder.getView(R.id.adaIvRecord))
        }

    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        val layoutManager = (recyclerView.layoutManager as GridLayoutManager)
        layoutManager.spanSizeLookup =
            object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return if (this@BrowseRecordAdapter.data[position].showType == 0) {
                        layoutManager.spanCount
                    } else {
                        1
                    }
                }

            }
    }

}