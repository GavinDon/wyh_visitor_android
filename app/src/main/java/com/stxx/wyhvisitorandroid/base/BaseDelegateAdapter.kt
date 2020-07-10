package com.stxx.wyhvisitorandroid.base

import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.vlayout.DelegateAdapter
import com.alibaba.android.vlayout.LayoutHelper
import com.alibaba.android.vlayout.layout.SingleLayoutHelper


/**
 * description:静态布局(ex:列表标题)
 * Created by liNan on 2020/1/10 9:20

 */
open class OnlyShowDelegateAdapter :
    DelegateAdapter.Adapter<BaseDelegateVH> {

    private var mLayoutId: Int = 0
    private var mLayoutHelper: LayoutHelper
    private var mCount: Int = 1

    constructor(mLayoutId: Int) : this(mLayoutId, SingleLayoutHelper(), 1)
    constructor(mLayoutId: Int, layoutHelper: LayoutHelper, count: Int) {
        this.mLayoutHelper = layoutHelper
        this.mLayoutId = mLayoutId
        //如果是gridLayout  count就不是1
        this.mCount = count
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseDelegateVH {
        return BaseDelegateVH(
            LayoutInflater.from(parent.context).inflate(mLayoutId, parent, false)
        )
    }

    override fun getItemCount(): Int = mCount
    override fun getItemViewType(position: Int) = mLayoutId
    override fun onCreateLayoutHelper() = mLayoutHelper
    override fun onBindViewHolder(holder: BaseDelegateVH, position: Int) {
    }

}

/**
 *  description:需要更改数据(ex:列表标题)
 * 需要动态添加数据
 *
 */
open class VBaseAdapter<T> : DelegateAdapter.Adapter<VBaseViewHolder<T>> {

    //布局文件资源ID
    private var mResLayout = 0
    private var mLayoutHelper: LayoutHelper
    //数据源
    private var mData: MutableList<T>
    //如果是单布局时的数据
    protected var singleData: List<T>? = null
    private var isSingleLayoutHelper: Boolean = false

    constructor() : this(0)
    constructor(layoutId: Int) : this(arrayListOf<T>(), layoutId, SingleLayoutHelper())
    constructor(layoutId: Int, layoutHelper: LayoutHelper) : this(
        arrayListOf<T>(),
        layoutId,
        layoutHelper
    )

    constructor(mData: ArrayList<T>, layoutId: Int, layoutHelper: LayoutHelper) {
        this.mData = mData
        this.mResLayout = layoutId
        this.mLayoutHelper = layoutHelper
        isSingleLayoutHelper = layoutHelper is SingleLayoutHelper
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VBaseViewHolder<T> {
        return VBaseViewHolder(
            LayoutInflater.from(parent.context!!).inflate(
                mResLayout,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = if (isSingleLayoutHelper) 1 else mData.size

    override fun getItemViewType(position: Int) = mResLayout

    override fun onCreateLayoutHelper(): LayoutHelper = mLayoutHelper


    fun setNewData(data: ArrayList<T>) {
        mData = data
        notifyDataSetChanged()
    }

    fun addData(data: ArrayList<T>) {
        mData.addAll(data)
        notifyDataSetChanged()
    }

    /*当是singleLayoutHelper时*/
    fun setSingerLayoutData(data: List<T>) {
        this.singleData = data
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: VBaseViewHolder<T>, position: Int) {

        if (!isSingleLayoutHelper) {
            holder.setData(position, mData[position])
            convert(holder, mData[position])
        } else {
            convert(holder, null)
        }
    }

    open fun convert(holder: VBaseViewHolder<T>, item: T?) {

    }

}

open class VBaseViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val views: SparseArray<View> = SparseArray()

    fun <T : View> getView(@IdRes viewId: Int): T {
        val view = getViewOrNull<T>(viewId)
        checkNotNull(view) { "No view found with id $viewId" }
        return view
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : View> getViewOrNull(@IdRes viewId: Int): T? {
        val view = views.get(viewId)
        if (view == null) {
            itemView.findViewById<T>(viewId)?.let {
                views.put(viewId, it)
                return it
            }
        }
        return view as? T
    }

    fun <T : View> Int.findView(): T? {
        return itemView.findViewById(this)
    }

    open fun setText(@IdRes viewId: Int, value: CharSequence?): VBaseViewHolder<T> {
        getView<TextView>(viewId).text = value
        return this
    }

    open fun setText(@IdRes viewId: Int, @StringRes strId: Int): VBaseViewHolder<T> {
        getView<TextView>(viewId).setText(strId)
        return this
    }

    private var mData: T? = null
    private var pos: Int? = null

    fun setData(position: Int, data: T?) {
        this.mData = data
        this.pos = position
    }

    fun convert(helper: VBaseViewHolder<T>, data: T?) {

    }

}