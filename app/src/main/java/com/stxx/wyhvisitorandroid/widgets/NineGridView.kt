package com.stxx.wyhvisitorandroid.widgets

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AlertDialog
import androidx.core.view.children
import com.gavindon.mvvm_lib.utils.dp2px
import com.squareup.picasso.Picasso
import com.stxx.wyhvisitorandroid.R
import org.jetbrains.annotations.NotNull


/**
 * description:添加图片九宫格控件
 * Created by liNan on  2020/2/18 22:40
 */
class NineGridView : ViewGroup {

    private lateinit var finalData: MutableList<ImageView>
    private val viewGroupWidth by lazy {
        val dm = resources.displayMetrics
        dm.widthPixels
    }

    private var imageViewSource: MutableList<ImageView> = mutableListOf()
    private var addImageBtn: ImageView? = null


    //是添加图片还是展示图片 0添加 1展示
    private var mode = 1

    //点击添加图片的按钮
    private var ivAddImageSource = -1

    //一行显示几张图片
    private var columnSize = 3
    //显示几行
    private var rowSize = 1

    //最多显示几行
    private var maxRow = 1

    //图片之间的间隔
    private var gap = 1

    private val addImageTag = "addImageTag"

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {

        val typedArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.NineGridView
        )
        ivAddImageSource = typedArray.getResourceId(
            R.styleable.NineGridView_add_image,
            -1
        )
        gap = typedArray.getInteger(R.styleable.NineGridView_spaceGap, 0)
        columnSize =
            typedArray.getInteger(R.styleable.NineGridView_columnNum, 1)

        maxRow =
            typedArray.getInteger(R.styleable.NineGridView_maxRow, 1)

        mode = typedArray.getInt(R.styleable.NineGridView_mode, 1)

        if (mode == 0) {
            addImageBtn = createAddImageView()
            show(mutableListOf())
        }


        typedArray.recycle()


    }

    /**
     * 创建添加图片按钮imageView
     */
    private fun createAddImageView(): ImageView {
        val childWidth =
            ((viewGroupWidth - paddingLeft - paddingRight) - (columnSize - 1) * gap.dp2px) / columnSize

        return with(ImageView(this.context)) {
            Picasso.get().load(ivAddImageSource).resize(childWidth, childWidth).into(this)
            tag = addImageTag
            layoutParams = LayoutParams(childWidth, childWidth)
            this
        }
    }

    fun getAddImageBtn() = addImageBtn

    fun createUriImageView(uri: Uri): ImageView {
        val childWidth =
            ((viewGroupWidth - paddingLeft - paddingRight) - (columnSize - 1) * gap.dp2px) / columnSize
        return with(ImageView(this.context)) {
            Picasso.get().load(uri).resize(childWidth, childWidth).into(this)
            tag = uri
            layoutParams = LayoutParams(childWidth, childWidth)
            this
        }
    }

    fun createPathImageView(path: String): ImageView {
        val childWidth =
            ((viewGroupWidth - paddingLeft - paddingRight) - (columnSize - 1) * gap.dp2px) / columnSize
        return with(ImageView(this.context)) {
            Picasso.get().load("file://$path").resize(childWidth, childWidth).into(this)
            tag = path
            layoutParams = LayoutParams(childWidth, childWidth)
            this
        }
    }


    fun createPathImageView(path: Int): ImageView {
        val childWidth =
            ((viewGroupWidth - paddingLeft - paddingRight) - (columnSize - 1) * gap.dp2px) / columnSize
        return with(ImageView(this.context)) {
            Picasso.get().load(path).resize(childWidth, childWidth).into(this)
            layoutParams = LayoutParams(childWidth, childWidth)
            this
        }
    }

    fun createUrlImageView(url: String): ImageView {
        val childWidth =
            ((viewGroupWidth - paddingLeft - paddingRight) - (columnSize - 1) * gap.dp2px) / columnSize
        return with(ImageView(this.context)) {
            Picasso.get().load(url).resize(childWidth, childWidth).into(this)
            layoutParams = LayoutParams(childWidth, childWidth)
            this
        }
    }

    fun addData(data: MutableList<ImageView>) {
        imageViewSource.addAll(data)
        if (imageViewSource.contains(addImageBtn)) {
            imageViewSource.remove(addImageBtn)
        }
        show(imageViewSource)
    }

    fun setNewData(data: MutableList<ImageView>) {
        imageViewSource = data
        show(data)
    }

    /*创建图片时是根据path来创建 */
    val pathData: () -> List<String>
        get() = {
            imageViewSource.remove(addImageBtn)
            imageViewSource.map { it.tag.toString() }
        }
    /*创建图片时是根据Uri来创建 */
    val uriData: () -> List<Uri>
        get() = {
            imageViewSource.remove(addImageBtn)
            imageViewSource.map { it.tag as Uri }
        }


    private fun show(@NotNull data: MutableList<ImageView>) {
        removeAllViews()
        //最多可显示多少张图片
        val maxLength = maxRow * columnSize
        finalData = if (data.size >= maxLength) {
            data.subList(0, maxLength)
        } else {
            if (mode == 0) {
                data.add(data.size, addImageBtn!!)
            }
            data
        }

        finalData.forEach {
            //如果已经添加过则不再添加
            if (it.parent == null) {
                it.setOnLongClickListener { v ->
                    //如果是添加图片的image 则不设置长按事件
                    if (v.tag?.toString() == addImageTag) {
                        return@setOnLongClickListener false
                    } else {
                        if (mode == 1) {
                            return@setOnLongClickListener false
                        }
                        showDeleteDialog(v)
                        return@setOnLongClickListener true
                    }
                }
                addView(it)
            }
        }

    }

    private fun showDeleteDialog(it: View) {
        AlertDialog.Builder(it.context).setTitle("删除?")
            .setPositiveButton("确认") { _, _ ->
                this.removeView(it)
                imageViewSource.remove(it)
                if (imageViewSource.contains(addImageBtn)) {
                    imageViewSource.remove(addImageBtn)
                }
                show(imageViewSource)
            }.setNegativeButton("取消") { _, _ ->

            }.show()

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val sizeWidth = MeasureSpec.getSize(widthMeasureSpec)

//        x + x + gap + x + gap
//        3 x +2 gap = to
//                3 x = to -2 gap /3
        val childWidth =
            ((viewGroupWidth - paddingLeft - paddingRight) - (columnSize - 1) * gap.dp2px) / columnSize
        var totalHeight = 0
        if (this::finalData.isInitialized) {
            //计算可显示几行图片
            rowSize = finalData.size / columnSize + (if (finalData.size % columnSize == 0) 0 else 1)

            if (finalData.size > 0) {
                totalHeight = childWidth * rowSize + (rowSize - 1) * gap.dp2px
            }
        }
        val totalWidth = sizeWidth - paddingLeft - paddingRight

        setMeasuredDimension(totalWidth, totalHeight)
    }


    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {

        var viewLeft = paddingLeft
        var viewTop = paddingTop

        children.forEachIndexed { index, view ->
            val viewWidth = view.layoutParams.width
            view.layout(viewLeft, viewTop, viewLeft + viewWidth, viewTop + viewWidth)
            when {
                index == 0 -> {
                    viewLeft += viewWidth + gap.dp2px
                }
                //换行时重置left top
                (index + 1) % columnSize == 0 -> {
                    viewTop += viewWidth + gap.dp2px
                    viewLeft = paddingLeft
                }
                else -> viewLeft += viewWidth + gap.dp2px
            }

        }

    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        removeAllViews()
    }


}

data class ImageType(val uri: Uri? = null, @DrawableRes val imgId: Int? = null)
