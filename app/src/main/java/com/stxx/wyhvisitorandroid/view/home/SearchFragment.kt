package com.stxx.wyhvisitorandroid.view.home

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.gavindon.mvvm_lib.net.SuccessSource
import com.gavindon.mvvm_lib.utils.SpUtils
import com.gavindon.mvvm_lib.widgets.showToast
import com.github.promeg.pinyinhelper.Pinyin
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.gyf.immersionbar.ImmersionBar
import com.orhanobut.logger.Logger
import com.stxx.wyhvisitorandroid.*
import com.stxx.wyhvisitorandroid.adapter.HistoryAdapter
import com.stxx.wyhvisitorandroid.adapter.SearchAdapter
import com.stxx.wyhvisitorandroid.base.BaseFragment
import com.stxx.wyhvisitorandroid.bean.SearchAllScenicResp
import com.stxx.wyhvisitorandroid.enums.ScenicMApPointEnum
import com.stxx.wyhvisitorandroid.mplusvm.SearchVm
import kotlinx.android.synthetic.main.fragment_search.*
import kotlin.concurrent.thread

/**
 * description:搜索
 * Created by liNan on 2020/3/23 10:44

 */
class SearchFragment : BaseFragment() {


    private lateinit var mViewModel: SearchVm

    //展示搜索历史列表
    private val historyAdapter by lazy { HistoryAdapter(R.layout.adapter_search_history, null) }

    //展示查询到的结果列表
    private val searchResultAdapter by lazy { SearchAdapter(R.layout.adapter_search_result, null) }

    //查询所有景点并且把景点名称转换成拼音集合
    private var searchResultLst: List<SearchAllScenicResp>? = null

    //匹配到结果列表
    private var searchMatchResultLst = mutableListOf<SearchAllScenicResp>()

    //搜索历史纪录
    private var searchHistoryLst: MutableList<Triple<Int, String, String>>? = null

    override fun setStatusBar() {
        ImmersionBar.with(this)
            .transparentStatusBar()
            .fitsSystemWindows(true)
            .statusBarDarkFont(true)
            .init()
    }

    override val layoutId: Int = R.layout.fragment_search

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        //搜索历史纪录布局
        val flexLayoutManager = rvSearchHistory.layoutManager as FlexboxLayoutManager
        flexLayoutManager.let {
            it.justifyContent = JustifyContent.FLEX_START
            it.flexDirection = FlexDirection.ROW
            it.flexWrap = FlexWrap.WRAP
        }
        btnBack.setOnClickListener {
            it.findNavController().navigateUp()
        }
        //搜索结果recyclerView
        rvSearchResult?.let {
            it.adapter = searchResultAdapter
            it.addItemDecoration(DividerItemDecoration(this.context, RecyclerView.VERTICAL))
        }
        //进行搜索动作
        tvSearch.setOnClickListener {
            queryResult(etSearchScenic?.text.toString())
        }
        //监听键盘右下角搜索
        etSearchScenic.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == IME_ACTION_SEARCH) queryResult(v?.text.toString())
            return@setOnEditorActionListener actionId == IME_ACTION_SEARCH
        }
        //监听文本变化
        etSearchScenic.doOnTextChanged { text, _, _, _ ->
            ivSearchClose.visibility = if (text?.length ?: 0 > 0) View.VISIBLE else View.GONE
            queryResult(text?.toString())
        }
        ivSearchClose.setOnClickListener {
            etSearchScenic.text?.clear()
        }
        //搜索结果Adapter
        searchResultAdapter.apply {
            setAnimationWithDefault(BaseQuickAdapter.AnimationType.AlphaIn)
            setOnItemClickListener { adapter, _, position ->
                val data = adapter.getItem(position) as SearchAllScenicResp
                thread {
                    //给已经保存的数据添加新历史纪录
                    if (searchHistoryLst == null) {
                        searchHistoryLst = mutableListOf()
                    }
                    val triple = Triple(data.id, data.type, data.name)
                    if (searchHistoryLst!!.contains(triple)) {
                        searchHistoryLst!!.remove(triple)
                    }
                    //把新的记录数据添加到第一个
                    searchHistoryLst?.add(0, Triple(data.id, data.type, data.name))

                    val str = SpUtils.list2String(searchHistoryLst)
                    SpUtils.put(HISTORY_SEARCH_SP, str)
                }
                Logger.i("type==${data.type}")
                navDetail(data.id, data.type, data.name)
            }
        }
        //删除按钮
        search_delete.setOnClickListener {
            cslHistory.visibility = View.GONE
            SpUtils.clearName(HISTORY_SEARCH_SP)
            //删除之后把数据置为null
            searchHistoryLst = null
        }
        //历史纪录recyclerView
        rvSearchHistory.adapter = historyAdapter
        historyAdapter.setAnimationWithDefault(BaseQuickAdapter.AnimationType.AlphaIn)
        historyAdapter.setOnItemClickListener { adapter, _, position ->
            val data = adapter.data[position] as Triple<Int, String, String>
            navDetail(data.first, data.second, data.third)
            //把点击的item放到第一个位置
            searchHistoryLst?.remove(data)
            searchHistoryLst?.add(0, data)
            //重新保存sp
            val str = SpUtils.list2String(searchHistoryLst)
            SpUtils.put(HISTORY_SEARCH_SP, str)
        }

        //获取可搜索的所有数据
        loadAllSearchData()
        loadHistoryData()

    }

    /**
     * 获取历史记录数据
     */
    private fun loadHistoryData() {
        val strHistory = SpUtils.get(HISTORY_SEARCH_SP, "")
        searchHistoryLst = SpUtils.string2List(strHistory)?.toMutableList()
        //判断是否显示历史纪录
        toggleRv()
        historyAdapter.setList(searchHistoryLst)
    }

    /**
     * 跳转到详情
     */
    private fun navDetail(id: Int, type: String, name: String) {
        when (type) {
            "5" -> {
                //停车场
                findNavController().navigate(
                    R.id.fragment_scenic,
                    bundleOf(BUNDLE_IS_ROBOT to true, BUNDLE_SELECT_TAB to 6, "name" to name)
                )

            }
            "7" -> {
                //厕所
                findNavController().navigate(
                    R.id.fragment_scenic,
                    bundleOf(BUNDLE_IS_ROBOT to true, BUNDLE_SELECT_TAB to 5, "name" to name)
                )
            }
            else -> {
                findNavController().navigate(
                    R.id.fragment_scenic_comment,
                    //@see ScenicCommentFragment
                    bundleOf("id" to id, "type" to type),
                    alphaNavOption
                )
            }
        }
    }


    private fun loadAllSearchData() {
        mViewModel = getViewModel()
        val allSearchValue = mViewModel.allSearchLiveEvent.value
        if (allSearchValue is SuccessSource) {
            convertSourceData(allSearchValue.body.data)
        } else {
            mViewModel.fetchAllScenic().observe(this, Observer {
                handlerResponseData(it, { resp ->
                    convertSourceData(resp.data)
                }, {

                })
            })
        }

    }

    /**
     * 根据输入内容进行匹配查询
     * @param fillStr 输入的字符串
     */
    private fun queryResult(fillStr: String?) {
        if (fillStr != null && fillStr.trim().isNotEmpty()) {
            if (searchResultLst != null) {
                searchMatchResultLst.clear()
                for (i in searchResultLst!!) {
                    val name = i.name
                    val pyName = i.letterName
                    if (name.indexOf(fillStr.trim()) != -1 || pyName?.indexOf(
                            fillStr.trim().toUpperCase()
                        ) != -1
                    ) {
                        searchMatchResultLst.add(i)
                    }
                }
                if (searchMatchResultLst.isNullOrEmpty()) {
                    searchResultAdapter.setList(null)
                    searchResultAdapter.setEmptyView(R.layout.empty_view)
                } else {
                    searchResultAdapter.setList(searchMatchResultLst)
                }
            } else {
                this.context?.showToast("获取景点数据失败")
            }
        } else {
            searchResultAdapter.setList(null)
            historyAdapter.setList(searchHistoryLst)
        }
        toggleRv()
    }

    /**
     * 把原始数据集合转换成带有拼音的集合
     */
    private fun convertSourceData(sourceData: List<SearchAllScenicResp>) {
        for (itemData in sourceData) {
            itemData.letterName = Pinyin.toPinyin(itemData.name, "")
        }
        searchResultLst = sourceData
    }

    /**
     * 切换显示历史列表和搜索结果列表
     */
    private fun toggleRv() {
        //如果没有搜索到关键字 则显示搜索历史
        if (searchHistoryLst.isNullOrEmpty()) {
            cslHistory.visibility = View.GONE
        } else {
            if (searchResultAdapter.data.isNullOrEmpty()) {
                cslHistory.visibility = View.VISIBLE
            } else {
                cslHistory.visibility = View.GONE
            }
        }
    }

}