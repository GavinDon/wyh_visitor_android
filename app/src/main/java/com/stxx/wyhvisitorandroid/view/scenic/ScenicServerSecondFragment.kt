package com.stxx.wyhvisitorandroid.view.scenic

import android.os.Bundle
import android.os.Handler
import android.text.Spanned
import android.text.method.LinkMovementMethod
import androidx.lifecycle.Observer
import com.gavindon.mvvm_lib.net.*
import com.gavindon.mvvm_lib.widgets.showToast
import com.google.gson.reflect.TypeToken
import com.stxx.wyhvisitorandroid.ApiService
import com.stxx.wyhvisitorandroid.R
import com.stxx.wyhvisitorandroid.base.ToolbarFragment
import com.stxx.wyhvisitorandroid.bean.ScenicServerResp
import com.stxx.wyhvisitorandroid.mplusvm.MineVm
import com.stxx.wyhvisitorandroid.widgets.HtmlUtil
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_scenic_server_second.*

/**
 * description:景区服务详情
 * Created by liNan on 2020/4/8 10:21
 */
class ScenicServerSecondFragment : ToolbarFragment() {
    override val toolbarName: Int by lazy { arguments?.getInt("scenicServerName") ?: 1 }
    override val layoutId: Int = R.layout.fragment_scenic_server_second
    private val scenicServerIndex by lazy { arguments?.getInt("scenicServer") ?: 1 }

    private val mViewModel: MineVm by lazy { getViewModel<MineVm>() }
    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        loadData()
    }

    private fun loadData() {
        mViewModel.getServiceNotice(listOf("type" to scenicServerIndex))
            .observe(this, Observer {
                handlerResponseData(it, { resp ->
                    if (!resp.data.isNullOrEmpty()) {
                        HtmlUtil().show(this.context, resp.data[0].content, handler)
                        tvScenicServerDetail.movementMethod =
                            LinkMovementMethod.getInstance()
                    }
                }, { loadData() })
            })

    }

    private val handler = Handler() {
        tvScenicServerDetail?.text = it.obj as Spanned
        return@Handler false
    }
}