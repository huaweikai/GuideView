package com.hua.guide

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * 该方法用于处理RecyclerView中的itemView，如果有index就是拿到Rv中的某个ItemView，如果为null就是拿到Rv本身
 * 如果index不为空且里面的id也不为空，则会锁定itemView中的view，如果该view没有点击事件，则自动处理，帮点击事件传给itemView处理
 */
suspend fun RecyclerView.findItemViewByRecyclerView(
    indexAndView: Pair<Int, Int?>? = null
): View? {
    return if (indexAndView != null) {
        val itemView = findViewByPosition(indexAndView.first)
        val itemViewId = indexAndView.second
        if (itemViewId != null) {
            val childView: View? = itemView?.findViewById(itemViewId)
            if (childView?.hasOnClickListeners() == false) {
                childView.setOnClickListener {
                    itemView.performClick()
                    childView.setOnClickListener(null)
                    childView.isClickable = false
                }
            }
            childView
        } else itemView
    } else this
}

private suspend fun RecyclerView.findViewByPosition(position: Int): View? {
    return suspendCancellableCoroutine {
        val firstVisibleIndex =
            (layoutManager as? LinearLayoutManager)?.findFirstVisibleItemPosition() ?: 0
        val lastVisibleIndex =
            (layoutManager as? LinearLayoutManager)?.findLastVisibleItemPosition() ?: 0
        if (position in firstVisibleIndex .. lastVisibleIndex) {
            it.resume(findViewHolderForAdapterPosition(position)?.itemView)
        } else {
            stopScroll()
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        removeOnScrollListener(this)
                        it.resume(findViewHolderForAdapterPosition(position)?.itemView)
                    }
                }
            })
            smoothScrollToPosition(position)
        }
    }
}