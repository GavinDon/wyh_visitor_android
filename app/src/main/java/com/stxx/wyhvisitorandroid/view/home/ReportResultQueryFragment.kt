package com.stxx.wyhvisitorandroid.view.home

import android.os.Bundle
import android.view.Gravity
import android.widget.FrameLayout
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.gavindon.mvvm_lib.net.SuccessSource
import com.gavindon.mvvm_lib.utils.SpUtils
import com.gavindon.mvvm_lib.utils.phoneRegex
import com.gavindon.mvvm_lib.widgets.showToast
import com.stxx.wyhvisitorandroid.*
import com.stxx.wyhvisitorandroid.adapter.ReportResultAdapter
import com.stxx.wyhvisitorandroid.base.ToolbarFragment
import com.stxx.wyhvisitorandroid.bean.ReportResultResp
import com.stxx.wyhvisitorandroid.mplusvm.ComplaintVm
import com.tencent.bugly.crashreport.CrashReport
import kotlinx.android.synthetic.main.comment_recyclerview.*
import kotlinx.android.synthetic.main.fragment_report_result_query.*
import kotlinx.android.synthetic.main.fragment_report_result_query.swipeRefresh
import kotlinx.android.synthetic.main.toolbar_title.*
import org.jetbrains.anko.matchParent
import java.util.regex.Pattern

/**
 * description: 举报结果查询
 * Created by liNan on 2020/3/16 16:18

 */
class ReportResultQueryFragment : ToolbarFragment() {
    override val toolbarName: Int = R.string.visitor_server_tsjy
    override val layoutId: Int = R.layout.fragment_report_result_query

    private lateinit var mAdapter: ReportResultAdapter

    private lateinit var complaintVm: ComplaintVm
    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        complaintVm = getViewModel()
//        lifecycle.addObserver(complaintVm)
        mAdapter = ReportResultAdapter(R.layout.adapter_report_result, null)
        rvReportRecord.adapter = mAdapter
        btnReportAdd.setOnClickListener {
            it.findNavController().navigate(R.id.fragment_complaint, null, navOption)
        }
        mAdapter.setOnItemClickListener { adapter, view, position ->
            val itemData = adapter.data[position] as ReportResultResp
            view.findNavController()
                .navigate(R.id.fragment_report_detail, bundleOf("detail" to itemData), navOption)
        }

        swipeRefresh?.setColorSchemeResources(R.color.colorTabSelect)
        swipeRefresh?.setOnRefreshListener {
            query()
        }
        query()
    }

    private fun query() {
        val userPhone = SpUtils.get(LOGIN_NAME_SP, "")
        /*  val liveData = complaintVm.queryLiveData.value
          if (liveData is SuccessSource) {
              mAdapter.setList(liveData.body.data.toMutableList())
              swipeRefresh.isRefreshing = false
          } else {*/
        swipeRefresh?.showRefreshIcon()
        complaintVm.queryResult(userPhone).observe(this, Observer {
            handlerResponseData(it, { resp ->
                mAdapter.setList(resp.data.toMutableList())
            }, {
                query()
            })
            swipeRefresh.isRefreshing = false
        })
//        }
    }

}