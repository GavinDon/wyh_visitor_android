package com.stxx.wyhvisitorandroid.view.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.gavindon.mvvm_lib.base.MVVMBaseApplication
import com.gavindon.mvvm_lib.base.ViewModelProviders
import com.gavindon.mvvm_lib.net.HttpManager
import com.gavindon.mvvm_lib.utils.SpUtils
import com.gavindon.mvvm_lib.utils.phoneWidth
import com.gavindon.mvvm_lib.widgets.DonButton
import com.gavindon.mvvm_lib.widgets.ToastUtil
import com.gavindon.mvvm_lib.widgets.showToast
import com.stxx.wyhvisitorandroid.*
import com.stxx.wyhvisitorandroid.base.MyApplication
import com.stxx.wyhvisitorandroid.mplusvm.LoginVm
import com.stxx.wyhvisitorandroid.view.login.LoginActivity
import com.stxx.wyhvisitorandroid.widgets.SendSmsView
import org.jetbrains.anko.support.v4.dip
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.wrapContent

/**
 * description:
 * Created by liNan on  2020/5/20 15:42
 */
class TipInputDialog : BaseDialogFragment() {

    private lateinit var loginVm: LoginVm


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.fullDialog)
        loginVm = ViewModelProviders.of(this).get(LoginVm::class.java)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(phoneWidth - dip(80), wrapContent)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    inline fun builds(block: TipInputDialog.() -> Unit): TipInputDialog {
        this.block()
        return this
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_tip_input, null, false)
        val resetSendSmsView = view.findViewById<SendSmsView>(R.id.sendSmsView)
        val resetPhone = view.findViewById<EditText>(R.id.etResetPhone)
        val btnConfirm = view.findViewById<DonButton>(R.id.btnConfirm)
        val btnCancel = view.findViewById<DonButton>(R.id.btnCancel)
        resetPhone.setText(SpUtils.get(LOGIN_NAME_SP, ""))
        resetSendSmsView.setOnSmsClickListener {
            loginVm.getSmsCode(listOf(Pair("phone", it)))
        }
        btnConfirm.setOnClickListener {
            val code: String = resetSendSmsView.getEditText().text.toString().trim()
            if (code.trim().isNotEmpty()) {
                loginVm.writeOff(code, {
                    SpUtils.clearAll()
                    HttpManager.instance.removeHeader()
                    MVVMBaseApplication.removeAllActivity()
                    this.context?.showToast("您的帐号已经成功注销")
                    findNavController().navigate(
                        R.id.login_activity,
                        null,
                        navOptions { })
                }, {
                })
            } else {
                this.context?.showToast("请输入验证码")
            }
        }
        btnCancel.setOnClickListener {
            this.dismiss()
        }
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

}