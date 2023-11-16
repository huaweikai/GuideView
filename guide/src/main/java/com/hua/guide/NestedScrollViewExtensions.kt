package com.hua.guide

import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ScrollView
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

suspend fun TapTarget.scrollToTarget() {
    val view = view ?: return
    if (view is RecyclerView) {
        val itemView = view.findItemViewByRecyclerView(indexAndView)
        this.view = itemView
        return
    }
    val pParent = view.parent.parent ?: return
    when (pParent) {
        is NestedScrollView -> {
            pParent.findAndScrollView(view)
        }
        is ScrollView -> {
            pParent.findAndScrollView(view)
        }
        else -> return
    }
}

/**
 * 根据NestedScrollView内部方法，计算出最终滑动的距离，用于判断滑动结束
 */
private fun NestedScrollView.getTargetScrollY(distance: Int): Int {
    val child = getChildAt(0)
    val lp = child.layoutParams as? ViewGroup.MarginLayoutParams ?: return distance
    val childSize = child.height + lp.topMargin + lp.bottomMargin
    val parentSpace = height - paddingTop - paddingBottom
    val scrollY = scrollY
    val maxY = max(0.0, (childSize - parentSpace).toDouble()).toInt()
    return (max(0.0, min((scrollY + distance).toDouble(), maxY.toDouble())) - scrollY).roundToInt()
}

fun NestedScrollView.calculateScrollDis(view: View): Int {
    val isUnder = view.top > height + scrollY
    val isAbove = view.bottom < scrollY
    // 加上一个view的height是为了尽可能的让它滚得更远一点
    val distance =  if (isUnder) {
        view.bottom - height + view.height
    } else if (isAbove) {
        view.top - scrollY - view.height
    } else {
        0
    }
    if (distance == 0) return 0
    return getTargetScrollY(distance)
}

suspend fun NestedScrollView.findAndScrollView(view: View?): View? {
    if (view == null) return null
    val distance = calculateScrollDis(view)
    if (distance == 0) return view
    val targetY = scrollY + distance
    return suspendCancellableCoroutine {
        viewTreeObserver.addOnScrollChangedListener(object :
            ViewTreeObserver.OnScrollChangedListener {
            override fun onScrollChanged() {
                if (scrollY == targetY) {
                    viewTreeObserver.removeOnScrollChangedListener(this)
                    it.resume(view)
                }
            }
        })
        smoothScrollBy(0, distance)
    }
}