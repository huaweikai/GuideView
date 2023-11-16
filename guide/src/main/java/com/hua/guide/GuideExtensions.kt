package com.hua.guide

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.PopupWindow
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewTreeLifecycleOwner

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
    windowManager.addView(targetView, context.guideWindowParams)
    return targetView
}

/**
 * 设置lifecycleOwner来实现如果在PopupWindow需要翻页时，启动协程使用
 */
fun PopupWindow?.showGuideView(
    target: TapTarget,
    lifecycleOwner: LifecycleOwner? = null
): GuideView {
    if (this == null) throw IllegalArgumentException("PopupWindow is null")
    val context = contentView.context ?: throw IllegalArgumentException("contentView is null")
    val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as? WindowManager ?:
        throw IllegalArgumentException("WindowManager is null")
    ViewTreeLifecycleOwner.set(contentView, lifecycleOwner)
    val targetView = GuideView(context, target, windowManager)
    windowManager.addView(targetView, context.guideWindowParams)
    return targetView
}

private val Context.guideWindowParams: WindowManager.LayoutParams get()  {
    return WindowManager.LayoutParams().apply {
        type = WindowManager.LayoutParams.TYPE_APPLICATION
        format = PixelFormat.RGBA_8888
        flags = 0.guideFlag
        gravity = Gravity.START or Gravity.TOP
        x = 0
        y = 0
        width = WindowManager.LayoutParams.MATCH_PARENT
        height = guideHeight
    }
}

val Int.guideFlag: Int
    get() = this or WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS

val Context.guideHeight: Int
    get() = screenHeight - statusHeight

