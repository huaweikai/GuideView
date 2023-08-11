package com.hua.guide

import android.animation.ValueAnimator
import android.graphics.RectF
import android.view.animation.DecelerateInterpolator

// 优先放在右边，优先放在下面
sealed class TextPosition {
    val rectF = RectF()
    class Top : TextPosition() {
        override fun fromBounds(rect: RectF, margin: Int, width: Int, height: Int): RectF {
            val left = rect.centerX() - width / 2
            val right = left + width
            val top = rect.top - height - margin
            val bottom = top + height
            return RectF(left, top, right, bottom)
        }
        override fun textAnimation(bounds: RectF, margin: Int, height: Int, width: Int): ValueAnimator {
            rectF.set(fromBounds(bounds, margin, height, width))
            val animateHeight = bounds.top - rectF.top
            return ValueAnimator.ofFloat(0f, animateHeight).apply {
                duration = animationDuration
                interpolator = animationInterpolator
            }
        }

        override fun disposeAnimation(rect: RectF, currentValue: Float) {
            rect.top = this.rectF.top - currentValue
            rect.bottom = this.rectF.bottom - currentValue
        }
    }
    class Bottom: TextPosition() {
        override fun fromBounds(rect: RectF, margin: Int, width: Int, height: Int): RectF {
            val left = rect.centerX() - width / 2
            val right = left + width
            val top = rect.bottom + margin
            val bottom = top + height
            return RectF(left, top, right, bottom)
        }
        override fun textAnimation(bounds: RectF, margin: Int, height: Int, width: Int): ValueAnimator {
            rectF.set(fromBounds(bounds, margin, height, width))
            val animateHeight = rectF.bottom - bounds.bottom
            return ValueAnimator.ofFloat(0f, animateHeight).apply {
                duration = animationDuration
                interpolator = animationInterpolator
            }
        }
        override fun disposeAnimation(rect: RectF, currentValue: Float) {
            rect.top = this.rectF.top + currentValue
            rect.bottom = this.rectF.bottom + currentValue
        }
    }
    class Left: TextPosition() {
        override fun fromBounds(rect: RectF, margin: Int, width: Int, height: Int): RectF {
            val left = rect.left - width - margin
            val right = left + width
            val top = rect.centerY() - height / 2
            val bottom = top + height
            return RectF(left, top, right, bottom)
        }
        override fun textAnimation(bounds: RectF, margin: Int, height: Int, width: Int): ValueAnimator {
            rectF.set(fromBounds(bounds, margin, height, width))
            val animateWidth = rectF.left - bounds.left
            return ValueAnimator.ofFloat(0f, animateWidth).apply {
                duration = animationDuration
                interpolator = animationInterpolator
            }
        }
        override fun disposeAnimation(rect: RectF, currentValue: Float) {
            rect.right = this.rectF.right - currentValue
            rect.left = this.rectF.left - currentValue
        }
    }
    class Right: TextPosition() {
        override fun fromBounds(rect: RectF, margin: Int, width: Int, height: Int): RectF {
            val left = rect.right + margin
            val right = left + width
            val top = rect.centerY() - height / 2
            val bottom = top + height
            return RectF(left, top, right, bottom)
        }

        override fun textAnimation(bounds: RectF, margin: Int, height: Int, width: Int): ValueAnimator {
            rectF.set(fromBounds(bounds, margin, height, width))
            val animateWidth = rectF.right - bounds.right
            return ValueAnimator.ofFloat(0f, animateWidth).apply {
                duration = animationDuration
                interpolator = animationInterpolator
            }
        }

        override fun disposeAnimation(rect: RectF, currentValue: Float) {
            rect.right = this.rectF.right + currentValue
            rect.left = this.rectF.left + currentValue
        }
    }
    object Empty: TextPosition() {
        override fun fromBounds(rect: RectF, margin: Int, width: Int, height: Int): RectF {
            return RectF()
        }

        override fun textAnimation(bounds: RectF, margin: Int, height: Int, width: Int): ValueAnimator {
            throw IllegalStateException("")
        }

        override fun disposeAnimation(rect: RectF, currentValue: Float) {}
    }

    val animationDuration = 300L

    val animationInterpolator = DecelerateInterpolator()

    abstract fun fromBounds(rect: RectF, margin: Int, width: Int, height: Int): RectF

    abstract fun textAnimation(bounds: RectF, margin: Int,  height: Int, width: Int): ValueAnimator

    abstract fun disposeAnimation(rect: RectF, currentValue: Float)

}

internal fun RectF.getTextBgPosition(
    textWidth: Int,
    textHeight: Int,
    margin: Int,
    marginIcon: Int
): RectF {
    val textPadding = margin * 2
    val widthFiller = textWidth + textPadding + marginIcon
    val heightFiller = textHeight + textPadding + marginIcon
    val position = getPosition(widthFiller, heightFiller)
    return position.fromBounds(this, marginIcon, textWidth + textPadding, textHeight + textPadding)
}

// 优先放在右边，优先放在下面
fun RectF.getPosition(
    widthFiller: Int,
    heightFiller: Int,
): TextPosition {
    val edgeLength = 16.dp
    val horizontalPosition = canDropHorizontal(edgeLength, widthFiller)
    if (horizontalPosition !is TextPosition.Empty) {
        return horizontalPosition
    }
    val verticalPosition = canDropVertical(edgeLength, heightFiller)
    if (verticalPosition is TextPosition.Empty) {
        throw IllegalStateException("can not find a position to drop text")
    }
    return verticalPosition
}


private fun RectF.canDropHorizontal(
    edgeLength: Int,
    widthFiller: Int
): TextPosition {
    if (top - edgeLength < 0 || bottom + edgeLength > screenHeight) {
        return TextPosition.Empty
    }
    if (right + widthFiller < screenWidth) {
        return TextPosition.Right()
    }
    if (left - widthFiller > 0) {
        return TextPosition.Left()
    }
    return TextPosition.Empty
}

private fun RectF.canDropVertical(
    edgeLength: Int,
    heightFiller: Int
): TextPosition {
    if (bottom + edgeLength + heightFiller < screenHeight) {
        return TextPosition.Bottom()
    }
    if (top - edgeLength - heightFiller > 0) {
        return TextPosition.Top()
    }
    return TextPosition.Empty
}

