package com.gavindon.mvvm_lib.status

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import androidx.core.view.children
import com.gavindon.mvvm_lib.R
import com.gavindon.mvvm_lib.widgets.DonButton


/**
 * description:
 * Created by liNan on 2019/12/25 15:10

 */
class StatusView : FrameLayout {

    @LayoutRes
    private var emptyViewId: Int = R.layout.custom_empty_view
    @LayoutRes
    private var netWorkErrorView: Int = R.layout.custom_no_network_view
    @LayoutRes
    private var retryViewId = R.layout.custom_no_network_view
    private val defaultLayoutParams: LayoutParams =
        LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, Gravity.CENTER)

    private val emptyView: View by lazy { inflate(context, emptyViewId, null) }
    private val retryView: View by lazy { inflate(context, retryViewId, null) }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.StatusView)
        val N: Int = typedArray.indexCount
        for (i in 0 until N) {
            when (val attr: Int = typedArray.getIndex(i)) {
                R.styleable.StatusView_empty_view -> {
                    emptyViewId = typedArray.getResourceId(attr, R.layout.custom_empty_view)
                }
                R.styleable.StatusView_network_view -> {
                    netWorkErrorView =
                        typedArray.getResourceId(attr, R.layout.custom_no_network_view)
                }

            }
        }
        typedArray.recycle()
        id = R.id.statusView
//        addView(emptyView, defaultLayoutParams)
//        addView(retryView, defaultLayoutParams)
        hideAllView()
    }


    fun showEmpty() {
        emptyView.tag = "empty"
        addView(emptyView)
//        emptyView.visibility = View.VISIBLE
//        retryView.visibility = View.GONE

    }

    fun hideAllView() {
        for (i in children) {
            if (i.tag == "retry") {
                removeView(retryView)
            } else if (i.tag == "empty") {
                removeView(emptyView)
            }
        }
        /*   emptyView.visibility = View.GONE*/
//        retryView.visibility = View.GONE
    }


    fun showRetryView(onRetry: () -> Unit) {
        retryView.tag = "retry"
        addView(retryView)
//          retryView.visibility = View.VISIBLE
//          emptyView.visibility = View.GONE
        retryView.findViewById<DonButton>(R.id.btnRetry).setOnClickListener {
            onRetry()
        }
    }

}
