package com.gavindon.mvvm_lib.base

import android.Manifest
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.gavindon.mvvm_lib.R
import com.gavindon.mvvm_lib.base.my_interface.IView
import com.gavindon.mvvm_lib.status.StatusView
import com.tbruyelle.rxpermissions2.RxPermissions
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.runtime.Permission
import io.reactivex.disposables.CompositeDisposable

/**
 * description:
 * Created by liNan on 2020/1/17 9:26

 */
abstract class MVVMBaseFragment : Fragment(), IView {

    private val mCompositeDisposable: CompositeDisposable by lazy { CompositeDisposable() }

    private var isInitView: Boolean = false

    private val rxPermissions: RxPermissions by lazy { RxPermissions(this) }

    val mStatusView: StatusView? by lazy {
        if (mStatusViewId != null) {
            view?.findViewById<StatusView>(mStatusViewId!!)
        } else {
            null
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(layoutId, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.i("lifecycle", "onAttach")
    }

    override fun onPause() {
        super.onPause()
        val im = this.context?.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        im.hideSoftInputFromWindow(this.view?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        Log.i("lifecycle", "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.i("lifecycle", "onStop")
    }

    override fun onResume() {
        super.onResume()
        Log.i("lifecycle", "onResume")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        /*   if (!isInitView) {
               onInit(savedInstanceState)
               isInitView = true
               Log.i("lifecycle", "onInit")
           }*/

        onInit(savedInstanceState)

    }


    override fun onDestroyView() {
        super.onDestroyView()
        Log.i("lifecycle", "onDestroyView")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("lifecycle", "onDestroy")
    }

    override fun onDetach() {
        super.onDetach()
        Log.i("lifecycle", "onDetach")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    /**
     * 权限请求
     */
    protected fun requestPermission(
        vararg permission: String,
        onGrantedAction: () -> Unit
    ) {
        AndPermission.with(this)
            .runtime()
            .permission(permission)
            .onGranted {
                if (it.size == permission.size) {
                    onGrantedAction()
                }
            }
            .onDenied {
                val permissionNames: List<String?> =
                    Permission.transformText(context, it)
                val message = this.requireContext().getString(
                    R.string.message_permission_always_failed,
                    TextUtils.join("\n", permissionNames)
                )
                //当点击禁止且勾选不再询问时
                if (AndPermission.hasAlwaysDeniedPermission(context, it)) {
                    showDeniedPermission(message)
                } else {
                    //当点击禁止时
                    showDeniedPermission(message)
                }
            }.start()
    }

    protected fun requestPermission2(
        vararg permissions: String,
        onGrantedAction: () -> Unit
    ) {
        var deniedPermission: List<String>? = null
        val grantedPermission = mutableListOf<String>()
        val obj = rxPermissions.requestEach(
            *permissions
        ).subscribe({ permission ->
            when {
                permission.granted -> {
                    //把已经授权的权限添加到新集合
                    grantedPermission.add(permission.name)
                    //未授权集合
                    deniedPermission = permissions.filter { !grantedPermission.contains(it) }
                    if (grantedPermission.size == permissions.size) {
                        onGrantedAction()
                    }
                }

                permission.shouldShowRequestPermissionRationale -> {
                }
                else -> {
                    // At least one denied permission with ask never again
                    // Need to go to the settings
                    val permissionNames: List<String?> =
                        Permission.transformText(
                            context,
                            deniedPermission
                        )
                    val message = this.requireContext().getString(
                        R.string.message_permission_always_failed,
                        TextUtils.join("\n", permissionNames)
                    )
                    showDeniedPermission(message)
                }
            }


        }, {

        })


    }

    /**
     * 提示缺少什么权限
     */
    private fun showDeniedPermission(message: String) {
        AlertDialog.Builder(this.requireContext())
            .setMessage(message)
            .setPositiveButton("去设置") { _, _ ->
                AndPermission.with(this).runtime().setting()
                    .start(PermissionCode.cameraCode)
            }.setNegativeButton("取消") { _, _ ->
            }.show()
    }


    @get:LayoutRes
    abstract val layoutId: Int

    @get:IdRes
    abstract val mStatusViewId: Int?

    abstract fun onInit(savedInstanceState: Bundle?)


}