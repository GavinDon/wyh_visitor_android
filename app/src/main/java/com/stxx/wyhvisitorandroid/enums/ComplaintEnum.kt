package com.stxx.wyhvisitorandroid.enums

import androidx.annotation.StringRes
import com.gavindon.mvvm_lib.base.MVVMBaseApplication
import com.stxx.wyhvisitorandroid.R

/**
 * description:投诉建议类型
 * Created by liNan on 2020/3/10 15:38

 */
enum class ComplaintEnum(@StringRes private val cons: Int) {
    SUGGEST(R.string.visitor_server_khjy),
    COMPLAINT(R.string.visitor_server_tsjb),
    MEDICAL_HELPER(R.string.visitor_server_yljz),
    FINDING_THINGS(R.string.visitor_server_xwqs),
    PERSONNEL_LOSE(R.string.visitor_server_ryzs),
    ACCESSIBILITY_SERVICES(R.string.visitor_server_wzafu),
    PUBLIC_SECURITY_REPORT(R.string.visitor_server_zajb),
    CLIENT_ASK(R.string.visitor_server_khzx);

    fun getValue() = cons

    companion object {
        fun getItem(pos: Int): ComplaintEnum {
            for (i in values()) {
                if (pos == i.ordinal) {
                    return i
                }
            }
            return SUGGEST
        }

        fun getPositionByValue(value: String): ComplaintEnum {
            for (i in values()) {
                if (MVVMBaseApplication.appContext.getString(i.cons) == value) {
                    return i
                }
            }
            return SUGGEST
        }

    }
}