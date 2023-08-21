@file:Suppress("unused")
package com.hua.guide

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.view.doOnLayout
import androidx.core.view.drawToBitmap

class TapTarget {

    internal var view: View? = null

    val title: CharSequence
    val description: CharSequence?
    internal var bounds: Rect = Rect()
    private var icon: Drawable? = null

    @JvmOverloads
    constructor(
        view: View,
        title: CharSequence,
        description: CharSequence? = null,
    ) {
        this.view = view
        this.title = title
        this.description = description
    }

    @JvmOverloads
    constructor(
        icon: Drawable,
        title: CharSequence,
        description: CharSequence?,
        bounds: Rect? = null,
        iconBounds: Rect? = null
    ) {
        this.icon = icon
        if (iconBounds == null) {
            icon.bounds = Rect(0, 0, icon.intrinsicWidth, icon.intrinsicHeight)
        } else {
            icon.bounds = iconBounds
        }
        this.title = title
        this.description = description
        bounds?.let { this.bounds = it }
    }


    fun onReady(runnable: () -> Unit) {
        val view = this.view ?: kotlin.run {
            runnable.invoke()
            return
        }
        view.doOnLayout {
            val location = IntArray(2)
            view.getLocationOnScreen(location)
            bounds = Rect(
                location[0],
                location[1],
                location[0] + view.width,
                location[1] + view.height
            )
            if (icon == null && view.width > 0 && view.height > 0) {
                val viewBitmap = view.drawToBitmap()
                val canvas = Canvas(viewBitmap)
                view.draw(canvas)
                val icon = BitmapDrawable(view.context.resources, viewBitmap)
                icon.setBounds(0, 0, view.width, view.height)
                this.icon = icon
            }
            runnable.invoke()
        }
    }

    internal var guideWidth = 1.dp

    internal var shadowWidth = 4.dp

    internal var guideColor = Color.BLUE

    private var _shadowColor: Int? = null

    internal var listener: GuideListener? = object :GuideListener {}

    internal var targetPadding = 4.dp

    internal var textWidth = wrapTextWidth

    internal var cancelable: Boolean = true

    internal var textSize = 12.sp

    internal var openPulseAnimation = true

    internal var showTextShowAnimation = true

    internal var radius: Float = 8.dp.toFloat()

    private var _descriptionTextSize: Int? = null

    internal var textPadding = 8.dp

    internal var pulseWidth = 1.dp

    internal var textMarginTarget = 16.dp

    internal var titleMarginDesc = 8.dp

    internal var shadowRadius = radius + 4.dp


    fun setGuideWidth(width: Int): TapTarget {
        this.guideWidth = width
        return this
    }

    fun setGuideColor(color: Int): TapTarget {
        this.guideColor = color
        return this
    }

    fun setGuideListener(listener: GuideListener?): TapTarget {
        this.listener = listener
        return this
    }

    fun setShadowWidth(width: Int): TapTarget {
        this.shadowWidth = width
        return this
    }

    fun setTargetPadding(padding: Int): TapTarget {
        this.targetPadding = padding
        return this
    }

    fun setTextWidth(width: Int): TapTarget {
        this.textWidth = width
        return this
    }

    fun setDescriptionTextSize(size: Int): TapTarget {
        this._descriptionTextSize = size
        return this
    }

    fun setTapRadius(radius: Float): TapTarget {
        this.radius = radius
        return this
    }

    fun setCancelable(cancelable: Boolean): TapTarget {
        this.cancelable = cancelable
        return this
    }

    fun setPulseAnimationStatus(status: Boolean): TapTarget {
        this.openPulseAnimation = status
        return this
    }

    fun setTextPadding(padding: Int): TapTarget {
        this.textPadding = padding
        return this
    }

    fun setPulseWidth(width: Int): TapTarget {
        this.pulseWidth = width
        return this
    }

    fun setTextMarginTarget(margin: Int): TapTarget {
        this.textMarginTarget = margin
        return this
    }

    fun setTitleMarginDesc(margin: Int): TapTarget {
        this.titleMarginDesc = margin
        return this
    }

    fun setShadowRadius(radius: Int): TapTarget {
        this.shadowRadius = radius.toFloat()
        return this
    }

    val shadowColor: Int = guideColor
        get() {
            if (_shadowColor == null) return guideColor
            return field
        }

    val descriptionTextSize: Int = textSize
        get() {
            if (_descriptionTextSize == null) return textSize
            return field
        }

}