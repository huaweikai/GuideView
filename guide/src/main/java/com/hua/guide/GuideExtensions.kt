package com.hua.guide

import android.app.Activity
import android.view.ViewGroup

@JvmName("showGuideView")
fun Activity?.showGuideView(
    target: TapTarget
): GuideView {
    if (this == null) throw IllegalArgumentException("Activity is null")
    val decorView = this.window.decorView as? ViewGroup
        ?: throw IllegalArgumentException("Activity has no decorView")
    val layoutParams = ViewGroup.LayoutParams(-1, -1)
    val targetView = GuideView(this, target, decorView)
    decorView.addView(targetView, layoutParams)
    return targetView
}