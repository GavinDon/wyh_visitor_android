package com.stxx.wyhvisitorandroid.adapter

import com.chad.library.adapter.base.BaseDelegateMultiAdapter
import com.chad.library.adapter.base.delegate.BaseMultiTypeDelegate
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.stxx.wyhvisitorandroid.R


/**
 * description:
 * Created by liNan on  2020/4/22 15:28
 */

class BDBotChatAdapter(data: MutableList<DelegateMultiEntity>?) :
    BaseDelegateMultiAdapter<DelegateMultiEntity, BaseViewHolder>(data) {
    init {
        setMultiTypeDelegate(object : BaseMultiTypeDelegate<DelegateMultiEntity>() {
            override fun getItemType(data: List<DelegateMultiEntity>, position: Int): Int {
                val type = data[position].itemType
                return if (type == DelegateMultiEntity.BOT_LEFT) {
                    DelegateMultiEntity.BOT_LEFT
                } else {
                    DelegateMultiEntity.BOT_RIGHT
                }
            }
        })
        getMultiTypeDelegate()
            ?.addItemType(DelegateMultiEntity.BOT_LEFT, R.layout.bot_left)
            ?.addItemType(DelegateMultiEntity.BOT_RIGHT, R.layout.bot_right)
    }

    override fun convert(holder: BaseViewHolder, item: DelegateMultiEntity) {
        if (holder.itemViewType == DelegateMultiEntity.BOT_LEFT) {
            holder.setText(R.id.botLeft, item.chat)
        } else {
            holder.setText(R.id.botRight, item.chat)
        }

    }

}

class DelegateMultiEntity {
    var chat: String? = null
    var itemType: Int = 1

    companion object {
        const val BOT_LEFT = 1
        const val BOT_RIGHT = 2
    }
}

// 方式二：实现自己的代理类
class MyMultiTypeDelegate : BaseMultiTypeDelegate<DelegateMultiEntity?>() {

    init {
        addItemType(DelegateMultiEntity.BOT_LEFT, R.layout.bot_left)
        addItemType(DelegateMultiEntity.BOT_RIGHT, R.layout.bot_right)
    }

    override fun getItemType(data: List<DelegateMultiEntity?>, position: Int): Int {
        return if (position % 2 == 0) {
            DelegateMultiEntity.BOT_RIGHT
        } else {
            DelegateMultiEntity.BOT_LEFT
        }
    }
}