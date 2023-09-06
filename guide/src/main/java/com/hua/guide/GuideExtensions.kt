package com.hua.guide

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.PopupWindow

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


@JvmName("showGuideWithDialog")
fun Dialog?.showGuideView(
    target: TapTarget,
): GuideView {
    if (this == null) throw IllegalArgumentException("Dialog is null")
    val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as? WindowManager ?:
    throw IllegalArgumentException("WindowManager is null")
    val targetView = GuideView(context, target, windowManager)
    windowManager.addView(targetView, guideWindowParams)
    return targetView
}

fun PopupWindow?.showGuideView(
    target: TapTarget
): GuideView {
    if (this == null) throw IllegalArgumentException("PopupWindow is null")
    val context = contentView.context ?: throw IllegalArgumentException("contentView is null")
    val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as? WindowManager ?:
        throw IllegalArgumentException("WindowManager is null")
    val targetView = GuideView(contentView.context, target, windowManager)
    windowManager.addView(targetView, guideWindowParams)
    return targetView
}

private val guideWindowParams: WindowManager.LayoutParams get()  {
    return WindowManager.LayoutParams().apply {
        type = WindowManager.LayoutParams.TYPE_APPLICATION
        format = PixelFormat.RGBA_8888
        flags = 0
        gravity = Gravity.START or Gravity.TOP
        x = 0
        y = 0
        width = WindowManager.LayoutParams.MATCH_PARENT
        height = WindowManager.LayoutParams.MATCH_PARENT
    }
}