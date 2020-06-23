package com.gavindon.mvvm_lib.widgets

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

/**
 * description:简单提示对话框
 * Created by liNan on 2019/12/30 10:19

 */
class TipDialog : DialogFragment() {

    var title: String = ""
    var message: String = ""

    private var strConfirm: String = ""
    private var strCancel: String = ""

    private lateinit var onConfirm: (dialog: DialogInterface) -> Unit
    private var onCancel1: ((dialog: DialogInterface) -> Unit)? = null


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(this.requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("确认") { dialog, _ ->
                if (this::onConfirm.isInitialized) {
                    onConfirm.invoke(dialog)
                }
            }.setNegativeButton("取消") { dialog, _ ->
                dismiss()
                if (onCancel1 != null) {
                    onCancel1?.invoke(dialog)
                }
            }.show()

    }


    inline fun builds(block: TipDialog.() -> Unit): TipDialog {
        this.block()
        return this
    }

    fun confirm(txtConfirm: String = "确定", confirm: (dialog: DialogInterface) -> Unit) {
        this.strConfirm = txtConfirm
        onConfirm = confirm
    }

    fun confirm(@StringRes txtResId: Int, confirm: (dialog: DialogInterface) -> Unit) {
        this.strConfirm = resources.getString(txtResId)
        onConfirm = confirm
    }

    fun cancel(txtCancel: String = "取消", cancel: ((dialog: DialogInterface) -> Unit)? = null) {
        this.strConfirm = txtCancel
        onCancel1 = cancel
    }
}

