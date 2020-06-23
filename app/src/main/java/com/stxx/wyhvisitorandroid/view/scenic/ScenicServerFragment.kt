package com.stxx.wyhvisitorandroid.view.scenic

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.stxx.wyhvisitorandroid.R
import com.stxx.wyhvisitorandroid.base.ToolbarFragment
import com.stxx.wyhvisitorandroid.navOption
import kotlinx.android.synthetic.main.fragment_scenic_server.*

/**
 * description:景区服务列表
 * Created by liNan on 2020/3/30 11:24
 * 在fragment中不能使用xml:onclick

 */
class ScenicServerFragment : ToolbarFragment() {
    override val toolbarName: Int = R.string.str_scenic_server
    override val layoutId: Int = R.layout.fragment_scenic_server

    /**
     * 1游客须知2.购票须知3.来园交通4.紧急求援
     */
    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        csVisitorNotice?.setOnClickListener {
            findNavController().navigate(
                R.id.fragment_scenic_server_second,
                bundleOf("scenicServer" to 1, "scenicServerName" to R.string.visitor_know),
                navOption
            )
        }
        csVisitorBuyTicket?.setOnClickListener {
            findNavController().navigate(
                R.id.fragment_scenic_server_second,
                bundleOf("scenicServer" to 2, "scenicServerName" to R.string.visitor_buy_ticket),
                navOption
            )
        }
        csVisitorTraffic?.setOnClickListener {
            findNavController().navigate(
                R.id.fragment_scenic_server_second,
                bundleOf("scenicServer" to 3, "scenicServerName" to R.string.visitor_traffic),
                navOption
            )
        }

        csVisitorHelp?.setOnClickListener {
            findNavController().navigate(
                R.id.fragment_scenic_server_second,
                bundleOf("scenicServer" to 4, "scenicServerName" to R.string.visitor_help),
                navOption
            )
        }


    }

}