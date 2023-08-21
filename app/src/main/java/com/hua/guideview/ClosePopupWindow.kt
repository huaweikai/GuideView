package com.hua.guideview

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.PopupWindow
import com.hua.guide.dp

class ClosePopupWindow(
    context: Context
): PopupWindow(context) {


    init {
        isOutsideTouchable = false
        isFocusable = false
        setBackgroundDrawable(null as? Drawable?)
        val view = LayoutInflater.from(context).inflate(R.layout.layout_pop_up, null, false)
        view.setOnClickListener {
            // 结束了
            Log.e("TAG", "结束了")
            onClickListener?.invoke(this)
        }
        contentView = view
    }

    private var onClickListener: ((PopupWindow) -> Unit)? = null

    fun setOnClickListener(listener: ((PopupWindow) -> Unit)? = null) {
        onClickListener = listener
    }

    fun show(window: Window) {
        val widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        val heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        contentView.measure(widthSpec, heightSpec)
        val x = window.decorView.width - contentView.measuredWidth - 16.dp
        val y = window.decorView.height - contentView.measuredHeight - 16.dp
        showAtLocation(window.decorView, 0, x, y)

    }

}