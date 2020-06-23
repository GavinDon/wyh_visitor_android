package com.gavindon.mvvm_lib.base.my_interface

/**
 * description:
 * Created by liNan on  2019/12/17 14:19
 */
interface IView {

    /**
     * 具体的activity/fragment来实现此方法
     * 如果禁止了授权则提示去设置打开权限,
     * 打开回调到baseActivity/baseFragment时调用此方法
     */
    fun permissionForResult()

}