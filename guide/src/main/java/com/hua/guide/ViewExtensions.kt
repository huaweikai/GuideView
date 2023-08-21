package com.hua.guide

import android.graphics.Rect
import android.view.View

val View.drawingRect: Rect
    get() {
        val rect = Rect()
        getDrawingRect(rect)
        return rect
    }

val View.locationRect: Rect
    get() {
        return Rect(this.left, this.top, this.right, this.bottom)
    }