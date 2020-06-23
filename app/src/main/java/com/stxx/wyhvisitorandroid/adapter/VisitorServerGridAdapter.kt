package com.stxx.wyhvisitorandroid.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.stxx.wyhvisitorandroid.R
import com.stxx.wyhvisitorandroid.bean.VisitGridData

/**
 * description:游客服务
 * Created by liNan on 2020/3/20 14:55

 */
class VisitorServerGridAdapter(private var dataSource: List<VisitGridData>?) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TITLE_TYPE = 0
        const val CONTENT_TYPE = 1
    }

    fun setList(data: List<VisitGridData>?) {
        this.dataSource = data
        notifyDataSetChanged()
    }

    fun getData(): List<VisitGridData>? {
        return this.dataSource
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TITLE_TYPE) {
            TitleViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(android.R.layout.simple_list_item_1, parent, false)
            )
        } else {
            ContentViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.adapter_visitor_server_grid, parent, false)
            )
        }

    }

    override fun getItemCount(): Int = dataSource?.size ?: 0

    override fun getItemViewType(position: Int): Int {
        return if (dataSource?.get(position)?.showType == 0) {
            TITLE_TYPE
        } else {
            CONTENT_TYPE
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is TitleViewHolder) {
            (holder.itemView as TextView).text = dataSource?.get(position)?.name
        } else if (holder is ContentViewHolder) {
            val mContext = holder.itemView.context
            holder.name.text = dataSource?.get(position)?.name
            val imageId =
                mContext.resources.getIdentifier(
                    dataSource?.get(position)?.iconRes
                    , "mipmap", mContext.packageName
                )
            holder.icon.setImageResource(imageId)
            holder.itemView.setOnClickListener {
                listener?.invoke(position)
            }
        }


    }

    private var listener: ((position: Int) -> Unit)? = null

    fun addOnItemClickListener(lis: ((position: Int) -> Unit)) {
        this.listener = lis
    }


    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        val layoutManager = (recyclerView.layoutManager as GridLayoutManager)
        layoutManager.spanSizeLookup =
            object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    if (getItemViewType(position) == TITLE_TYPE) {
                        return layoutManager.spanCount
                    }
                    return 1
                }

            }
    }


    inner class TitleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    inner class ContentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.adaTvVisitorName)
        val icon: ImageView = itemView.findViewById(R.id.adaIvVisitorServer)
    }
}