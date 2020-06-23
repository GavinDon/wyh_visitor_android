package com.stxx.wyhvisitorandroid.base

import android.util.SparseArray
import android.view.View
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView


/**
 * description:
 * Created by liNan on 2020/1/10 9:22

 */
class BaseDelegateVH(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val views: SparseArray<View> by lazy { SparseArray<View>() }

    fun setText(@IdRes idRes: Int, txt: String): BaseDelegateVH {
        val textView = getView<TextView>(idRes)
        textView?.text = txt
        return this
    }

    fun setText(@IdRes idRes: Int, @StringRes txt: Int): BaseDelegateVH {
        val textView = getView<TextView>(idRes)
        textView?.setText(txt)
        return this
    }

    fun <T : View> getView(@IdRes idRes: Int): T? {
        var view = views.get(idRes)
        if (view == null) {
            view = itemView.findViewById(idRes)
            views.put(idRes, view)
        }
        return view as? T
    }

    fun setOnClickListener() {
    }

}