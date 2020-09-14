package com.stxx.wyhvisitorandroid.view.mine

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputConnection
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import com.gavindon.mvvm_lib.base.ViewModelProviders
import com.gavindon.mvvm_lib.net.BR
import com.gavindon.mvvm_lib.net.Resource
import com.gavindon.mvvm_lib.net.SuccessSource
import com.gavindon.mvvm_lib.utils.nickNameRegex
import com.gavindon.mvvm_lib.widgets.showToast
import com.gyf.immersionbar.ImmersionBar
import com.stxx.wyhvisitorandroid.R
import com.stxx.wyhvisitorandroid.bean.UserInfoResp
import com.stxx.wyhvisitorandroid.filterSpaceEmojiSpecial
import com.stxx.wyhvisitorandroid.mplusvm.MineVm
import kotlinx.android.synthetic.main.dialog_update_userinfo.*
import java.util.regex.Pattern

/**
 * description:
 * Created by liNan on 2020/3/4 14:56

 */
class FixUserInfoFragment() : DialogFragment() {

    private var mActivity: Activity? = null

    private val mineVm: MineVm = MineView.mineVm
    private var resourceUseInfo: Resource<BR<UserInfoResp>>? = null
    private var userInfoData: UserInfoResp? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            mActivity = context
        }
//        mineVm = ViewModelProviders.of(this).get(MineVm::class.java)
        resourceUseInfo = mineVm.getUserInfo().value
        if (resourceUseInfo is SuccessSource) {
            userInfoData = (resourceUseInfo as SuccessSource).body.data
        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.fullDialog)
    }

    override fun onStart() {
        super.onStart()
        dialog?.apply {
            setCanceledOnTouchOutside(true)
        }
        dialog?.window?.setWindowAnimations(R.style.RightAnimation)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_update_userinfo, null, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ivClose.setOnClickListener {
            etFix.setText("")
        }
        etFix.filterSpaceEmojiSpecial(20).setText(userInfoData?.true_name)
        tv_menu.setOnClickListener {
            val fix = etFix.text.toString()
            if (!Pattern.matches(nickNameRegex, fix)) {
                this.context?.showToast("请输入符合规则的昵称")
                return@setOnClickListener
            }
            mineVm.updateNickName(fix).observe(viewLifecycleOwner, Observer {
                if (it is SuccessSource) {
                    userInfoData?.true_name = fix
                    mineVm.getUserInfo().postValue(resourceUseInfo)
                    dismissAllowingStateLoss()
                    disMissListener?.invoke()
                }
            })
        }
        toolbar_back.setOnClickListener {
            dismissAllowingStateLoss()
            disMissListener?.invoke()
        }

    }

    var disMissListener: (() -> Unit)? = null
    fun addOnDismissListener(l: () -> Unit) {
        disMissListener = l
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        ImmersionBar.with(this)
            .fitsSystemWindows(true)
            .statusBarColor(R.color.colorPrimaryYellow)
            .statusBarDarkFont(true)
            .init()
    }

}