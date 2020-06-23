package com.stxx.wyhvisitorandroid.view.mine

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.gavindon.mvvm_lib.net.SuccessSource
import com.gavindon.mvvm_lib.widgets.showToast
import com.haibin.calendarview.Calendar
import com.haibin.calendarview.CalendarView
import com.orhanobut.logger.Logger
import com.stxx.wyhvisitorandroid.R
import com.stxx.wyhvisitorandroid.adapter.BrowseRecordAdapter
import com.stxx.wyhvisitorandroid.base.ToolbarFragment
import com.stxx.wyhvisitorandroid.bean.BrowseRecordBean
import com.stxx.wyhvisitorandroid.bean.CopyBrowRecordBean
import com.stxx.wyhvisitorandroid.graphics.ImageLoader
import com.stxx.wyhvisitorandroid.mplusvm.BrowseRecordVm
import com.stxx.wyhvisitorandroid.navOption
import kotlinx.android.synthetic.main.fragment_browse_record.*
import kotlinx.android.synthetic.main.toolbar.*
import org.jetbrains.anko.support.v4.toast
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.InputStream
import java.io.InputStreamReader


/**
 * description: 浏览纪录
 * Created by liNan on 2020/3/18 14:36

 */
class BrowseRecordFragment : ToolbarFragment() {

    private lateinit var mViewModel: BrowseRecordVm

    override val toolbarName: Int = R.string.browse_footprint
    override val layoutId: Int = R.layout.fragment_browse_record

    //转换返回足迹数据为自己可用数据 (list嵌套list 转换成一个list,根据showType展示不同布局。)
    private val copyList = mutableListOf<CopyBrowRecordBean>()
    private val mAdapter: BrowseRecordAdapter by lazy { BrowseRecordAdapter(null) }


    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        mViewModel = getViewModel()
        initCalendar()
        mAdapter.setAnimationWithDefault(BaseQuickAdapter.AnimationType.AlphaIn)
        browse_recyclerView.adapter = mAdapter
        tv_menu.text = "全部"
        tv_menu.visibility = View.VISIBLE
        tv_menu.setOnClickListener {
            mAdapter.setList(copyList)
        }
        loadData()
    }

    private fun initCalendar() {
        calendarView.setRange(
            2020, 5, 1,
            calendarView.curYear,
            calendarView.curMonth,
            calendarView.curDay
        )
        calendarView.setOnCalendarSelectListener(object : CalendarView.OnCalendarSelectListener {
            override fun onCalendarSelect(calendar: Calendar, isClick: Boolean) {
                val m = calendar.month
                val d = calendar.day
                val date =
                    """${calendar.year}-${if (m < 10) "0$m" else "$m"}-${if (d < 10) "0$d" else "$d"}"""

                val newData = copyList.filter { it.time == date }
                mAdapter.setList(newData.toMutableList())
            }

            override fun onCalendarOutOfRange(calendar: Calendar?) {
                toast("所选日期超出范围")
            }

        })
    }

    private fun loadData() {
//        val value = mViewModel.getBrowseData().value
        /*   if (value != null && value is SuccessSource) {
               convertData(value.body.data)
           } else {*/
        mViewModel.getBrowseData().observe(this, Observer {
            handlerResponseData(it, { resp ->
                convertData(resp.data)
            }, {})
        })
//        }
    }

    private fun convertData(data: List<BrowseRecordBean>) {
        copyList.clear()
        data.forEachIndexed { _, browseRecordBean ->
            copyList.add(CopyBrowRecordBean(time = browseRecordBean.time, showType = 0))
            for (beanItem in browseRecordBean.data) {
                copyList.add(CopyBrowRecordBean().apply {
                    explain = beanItem.explain
                    id = beanItem.id
                    imgurl = beanItem.imgurl
                    introduction = beanItem.introduction
                    name = beanItem.name
                    position = beanItem.position
                    time = beanItem.time
                    type = beanItem.type
                    x = beanItem.x
                    y = beanItem.y
                })
            }
        }
        mAdapter.setList(copyList)
        mAdapter.setOnItemClickListener { adapter, _, position ->
            val itemData = adapter.getItem(position) as CopyBrowRecordBean
            if (itemData.showType != 0) {
                findNavController().navigate(
                    R.id.fragment_scenic_comment,
                    bundleOf("id" to itemData.id, "type" to itemData.type),
                    navOption
                )
            }
        }

    }



    class BrowseAdapter(private var dataSource: MutableList<CopyBrowRecordBean>?) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        companion object {
            const val TITLE_TYPE = 0
            const val CONTENT_TYPE = 1
        }


        fun updateData(dataSource2: MutableList<CopyBrowRecordBean>) {
            this.dataSource = dataSource2
            notifyDataSetChanged()
        }

        inner class Vh(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val title: TextView = itemView.findViewById(R.id.adaTvRecordName)
            val iv: ImageView = itemView.findViewById(R.id.adaIvRecord)
        }

        inner class TitleVh(itemView: View) : RecyclerView.ViewHolder(itemView)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val normal = LayoutInflater.from(parent.context)
                .inflate(R.layout.adapter_browse, parent, false)

            val title =
                LayoutInflater.from(parent.context)
                    .inflate(android.R.layout.simple_list_item_1, parent, false)

            return if (viewType == TITLE_TYPE) {
                TitleVh(title)
            } else {
                Vh(normal)
            }

        }

        override fun getItemViewType(position: Int): Int {
            return if (dataSource?.get(position)?.showType == 0) {
                TITLE_TYPE
            } else {
                CONTENT_TYPE
            }
        }

        override fun getItemCount(): Int = dataSource?.size ?: 0


        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

            if (holder is TitleVh) {
                (holder.itemView as TextView).text = dataSource?.get(position)?.time
            } else if (holder is Vh) {
                holder.title.text = dataSource?.get(position)?.name
                ImageLoader.with().load(dataSource?.get(position)?.imgurl).into(holder.iv)
                holder.itemView.setOnClickListener {
                    it.findNavController().navigate(R.id.fragment_setting)
                }
            }
        }

        override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
            val layoutManager = (recyclerView.layoutManager as GridLayoutManager)
            layoutManager.spanSizeLookup =
                object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return if (dataSource?.get(position)?.showType == 0) {
                            layoutManager.spanCount
                        } else {
                            1
                        }
                    }

                }
        }
    }
}