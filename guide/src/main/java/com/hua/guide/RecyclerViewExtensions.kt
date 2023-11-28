package com.hua.guide

import android.view.View
import androidx.core.view.doOnLayout
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import java.lang.NullPointerException
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
    var view = findViewHolderForAdapterPosition(position)?.itemView
    if (view == null && childCount <= 0) {
        // 500毫秒内如果没有摆放完就抛出异常
        val result = withTimeoutOrNull(500L) {
            while (true) {
                delay(100L)
                val hasChild = suspendCancellableCoroutine<Boolean> { scope ->
                    doOnLayout { scope.resume(childCount > 0) }
                }
                if (hasChild) break
            }
        }
        if (result == null) {
            if (context.isDebug()) throw NullPointerException("recyclerView always layout error")
            return null
        }
        // 然后再次获取View
        view = findViewHolderForAdapterPosition(position)?.itemView
    }
    if (view != null && view.top >= 0) {
        return view
    }
    return scrollToPositionReturnView(position)
}

private suspend fun RecyclerView.scrollToPositionReturnView(position: Int): View? {
    return suspendCancellableCoroutine {
        stopScroll()
        addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    removeOnScrollListener(this)
                    it.resume(findViewHolderForAdapterPosition(position)?.itemView)
                }
            }
        })
        doOnLayout { smoothScrollToPosition(position) }
    }
}