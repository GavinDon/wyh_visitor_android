package com.stxx.wyhvisitorandroid.view.dialog

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigator
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.gavindon.mvvm_lib.base.ViewModelProviders
import com.gavindon.mvvm_lib.net.SuccessSource
import com.gavindon.mvvm_lib.utils.phoneWidth
import com.stxx.wyhvisitorandroid.*
import com.stxx.wyhvisitorandroid.bean.LocationBean
import com.stxx.wyhvisitorandroid.mplusvm.ScenicVm
import kotlinx.android.synthetic.main.dialog_smart_tip.*
import org.jetbrains.anko.support.v4.dip
import org.jetbrains.anko.wrapContent

/**
 * description:AR智能提示dialog
 * Created by liNan on  2020/8/28 09:15
 */
class SmartTipDialog : DialogFragment() {


    private lateinit var mViewModel: ScenicVm
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.halfDialog)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(phoneWidth - dip(80), wrapContent)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mViewModel = ViewModelProviders.of(this).get(ScenicVm::class.java)
        return layoutInflater.inflate(R.layout.dialog_smart_tip, null, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val locationBean: LocationBean = arguments?.getSerializable("locationBean") as LocationBean
        smartTipTvTitle.text = "AI管家发现您在${locationBean.name}附近\n推荐您游玩以下路线"
        smartTipTvContent.text = "${locationBean.suitble}\n${locationBean.route}"
        btnConfirm.setOnClickListener {
            this.dismiss()
        }
        tvGoDetail.setOnClickListener {
            mViewModel.getServicePointById(locationBean.detailId, ApiService.SCENIC_MAP_POINT_ID)
                .observe(this, Observer {
                    if (it is SuccessSource) {
                        findNavController().navigate(
                            R.id.fragment_scenic_comment,
                            bundleOf(
                                BUNDLE_SCENIC_DETAIL to it.body.data,
                                "end" to convertBaidu(
                                    (it.body.data.y ?: "0").toDouble(),
                                    ((it.body.data.x ?: "0").toDouble())
                                )
                            ),
                            navOption
                        )
                    }
                })
        }

    }
}