package com.hua.guide

import android.view.View
import android.view.ViewTreeObserver
import android.widget.ScrollView
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

fun ScrollView.calculateScrollDis(view: View): Int {
    val isUnder = view.top > height + scrollY
    val isAbove = view.bottom < scrollY
    // 加上一个view的height是为了尽可能的让它滚得更远一点
    val distance = if (isUnder) {
        view.bottom - height + view.height
    } else if (isAbove) {
        view.top - scrollY - view.height
    } else {
        0
    }
    if (distance == 0) return 0
    return getTargetScrollY(distance)
}

fun ScrollView.getTargetScrollY(distance: Int): Int {
    val height: Int = height - paddingBottom - paddingTop
    val bottom = getChildAt(0).height
    val maxY = max(0.0, (bottom - height).toDouble()).toInt()
    return (max(0.0, min((scrollY + distance).toDouble(), maxY.toDouble())) - scrollY).roundToInt()
}

suspend fun ScrollView.findAndScrollView(view: View?): View? {
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