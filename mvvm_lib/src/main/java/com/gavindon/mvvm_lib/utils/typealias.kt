package com.gavindon.mvvm_lib.utils

import com.gavindon.mvvm_lib.base.MVVMBaseViewModel
import com.gavindon.mvvm_lib.net.BR
import com.gavindon.mvvm_lib.net.Resource
import com.gavindon.mvvm_lib.net.SingleLiveEvent

/**
 * description:
 * Created by liNan on 2019/12/24 15:47

 */
typealias Parameters = List<Pair<String, Any?>>

typealias onSuccess = (String) -> Unit
typealias onFailed = (Throwable) -> Unit
typealias onSuccessT<T> = (T) -> Unit
typealias onSuccessBr = (BR<String>) -> Unit
typealias singLiveData<T> = SingleLiveEvent<Resource<BR<T>>>
