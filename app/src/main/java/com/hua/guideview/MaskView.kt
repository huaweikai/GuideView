package com.hua.guideview

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import android.view.View

class MaskView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0
): View(context, attributeSet, defStyle) {

    init {
        setBackgroundColor(Color.TRANSPARENT)
        setOnClickListener {
            // 点击遮罩层，不做任何事情
            Log.e("TAG", "maskView onClick")
        }
    }

}