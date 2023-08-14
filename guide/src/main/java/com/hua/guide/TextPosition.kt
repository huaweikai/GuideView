package com.hua.guide

import android.animation.ValueAnimator
import android.graphics.RectF
import android.view.animation.AccelerateDecelerateInterpolator

internal var edgeLength = 8.dp

// 优先放在右边，优先放在下面
sealed class TextPosition(
    targetBounds: RectF,
    textPadding: Int,
    val marginTarget: Int,
    textWidth: Int,
    textHeight: Int
) {

    val height = textHeight + textPadding * 2
    val width = textWidth + textPadding * 2

    val targetBounds = targetBounds.toNew()

    class Top(
        targetBounds: RectF,
        textPadding: Int,
        marginTarget: Int,
        textWidth: Int,
        textHeight: Int
    ): TextPosition(targetBounds, textPadding, marginTarget, textWidth, textHeight) {
        override fun getTextRectBounds(): RectF {
            val left = targetBounds.centerX() - width / 2
            val right = left + width
            val top = targetBounds.top - height - marginTarget
            val bottom = top + height
            return RectF(left, top, right, bottom)
        }

        override fun getTextStartAnimation(textRectF: RectF): ValueAnimator? {
            // 计算高度差，做出向上移动的动画
            val targetRectBounds = getTextRectBounds()
            val diff = targetBounds.top - targetRectBounds.top
            val bounds = calculateAnimateStartBound(targetRectBounds, targetBounds, textRectF, this)
            return ValueAnimator.ofFloat(0f, diff).apply {
                duration = 100
                interpolator = AccelerateDecelerateInterpolator()
                addUpdateListener {
                    textRectF.bottom = bounds.bottom - it.animatedValue as Float
                    textRectF.top = bounds.top - it.animatedValue as Float
                }
                start()
            }
        }
    }
    class Bottom(
        targetBounds: RectF,
        textPadding: Int,
        marginTarget: Int,
        textWidth: Int,
        textHeight: Int
    ): TextPosition(targetBounds, textPadding, marginTarget, textWidth, textHeight) {
        override fun getTextRectBounds(): RectF {
            val left = targetBounds.centerX() - width / 2
            val right = left + width
            val top = targetBounds.bottom + marginTarget
            val bottom = top + height
            return RectF(left, top, right, bottom)
        }

        override fun getTextStartAnimation(textRectF: RectF): ValueAnimator? {
            // 计算高度差，做出向下移动的动画
            val targetRectBounds = getTextRectBounds()
            val diff = targetRectBounds.top - targetBounds.top
            val bounds = calculateAnimateStartBound(targetRectBounds, targetBounds,  textRectF,this)
            return ValueAnimator.ofFloat(0f, diff).apply {
                duration = 100
                interpolator = AccelerateDecelerateInterpolator()
                addUpdateListener {
                    textRectF.bottom = bounds.bottom + it.animatedValue as Float
                    textRectF.top = bounds.top + it.animatedValue as Float
                }
                start()
            }
        }
    }
    class Left(
        targetBounds: RectF,
        textPadding: Int,
        marginTarget: Int,
        textWidth: Int,
        textHeight: Int
    ): TextPosition(targetBounds, textPadding, marginTarget, textWidth, textHeight) {
        override fun getTextRectBounds(): RectF {
            val left = targetBounds.left - width - marginTarget
            val right = left + width
            val top = targetBounds.centerY() - height / 2
            val bottom = top + height
            return RectF(left, top, right, bottom)
        }

        override fun getTextStartAnimation(textRectF: RectF): ValueAnimator? {
            // 计算宽度差，做出向左移动的动画
            val targetRectBounds = getTextRectBounds()
            val diff = targetBounds.left - targetRectBounds.left
            val bounds = calculateAnimateStartBound(targetRectBounds, targetBounds,  textRectF,this)
            return ValueAnimator.ofFloat(0f, diff).apply {
                duration = 100
                interpolator = AccelerateDecelerateInterpolator()
                addUpdateListener {
                    textRectF.left = bounds.left - it.animatedValue as Float
                    textRectF.right = bounds.right - it.animatedValue as Float
                }
                start()
            }
        }
    }
    class Right(
        targetBounds: RectF,
        textPadding: Int,
        marginTarget: Int,
        textWidth: Int,
        textHeight: Int
    ): TextPosition(targetBounds, textPadding, marginTarget, textWidth, textHeight) {
        override fun getTextRectBounds(): RectF {
            val left = targetBounds.right + marginTarget
            val right = left + width
            val top = targetBounds.centerY() - height / 2
            val bottom = top + height
            return RectF(left, top, right, bottom)
        }
        override fun getTextStartAnimation(textRectF: RectF): ValueAnimator? {
            // 计算宽度差，做出向右移动的动画
            val targetRectBounds = getTextRectBounds()
            val diff = targetRectBounds.left - targetBounds.left
            val bounds = calculateAnimateStartBound(targetRectBounds, targetBounds,textRectF,  this)
            return ValueAnimator.ofFloat(0f, diff).apply {
                duration = 100
                interpolator = AccelerateDecelerateInterpolator()
                addUpdateListener {
                    textRectF.left = bounds.left + it.animatedValue as Float
                    textRectF.right = bounds.right + it.animatedValue as Float
                }
                start()
            }
        }
    }
    object Empty: TextPosition(RectF(), 0, 0, 0, 0)

    open fun getTextRectBounds(): RectF = targetBounds

    open fun getTextStartAnimation(textRectF: RectF): ValueAnimator? = null

    companion object {

        fun calculateAnimateStartBound(
            animateTarget: RectF,
            nowStartBounds: RectF,
            textRectF: RectF,
            position: TextPosition
        ): RectF {
            val diffHeight = animateTarget.height() - nowStartBounds.height()
            val diffWidth = animateTarget.width() - nowStartBounds.width()
            if (diffHeight == 0f && diffWidth == 0f) {
                return nowStartBounds
            }
            val rectF = nowStartBounds.toNew()
            rectF.inset(-diffWidth / 2, -diffHeight / 2)
            when (position) {
                is Left -> {
                    rectF.left += diffWidth / 2
                    rectF.right += diffWidth / 2
                }
                is Right -> {
                    rectF.left += diffWidth / 2
                    rectF.right += diffWidth / 2
                }
                is Top -> {
                    rectF.top += diffHeight / 2
                    rectF.bottom += diffHeight / 2
                }
                is Bottom -> {
                    rectF.top += diffHeight / 2
                    rectF.bottom += diffHeight / 2
                }
                else -> {}
            }
            textRectF.set(rectF)
            return rectF
        }

    }

}

// 优先放在右边，优先放在下面
fun RectF.getPosition(
    textPadding: Int,
    marginTarget: Int,
    textWidth: Int,
    textHeight: Int
): TextPosition {
    val horizontalPosition = canDropHorizontal(textPadding, marginTarget, textWidth, textHeight)
    if (horizontalPosition !is TextPosition.Empty) {
        return horizontalPosition
    }
    val verticalPosition = canDropVertical(textPadding, marginTarget, textWidth, textHeight)
    if (verticalPosition is TextPosition.Empty) {
        throw IllegalStateException("can not find a position to drop text")
    }
    return verticalPosition
}


private fun RectF.canDropHorizontal(
    textPadding: Int,
    marginTarget: Int,
    textWidth: Int,
    textHeight: Int
): TextPosition {
    val widthFiller = textWidth + textPadding * 2 + marginTarget
    if (top - edgeLength < 0 || bottom + edgeLength > screenHeight) {
        return TextPosition.Empty
    }
    if (right + widthFiller < screenWidth) {
        return TextPosition.Right(this, textPadding, marginTarget, textWidth, textHeight)
    }
    if (left - widthFiller > 0) {
        return TextPosition.Left(this, textPadding, marginTarget, textWidth, textHeight)
    }
    return TextPosition.Empty
}

private fun RectF.canDropVertical(
    textPadding: Int,
    marginTarget: Int,
    textWidth: Int,
    textHeight: Int
): TextPosition {
    val heightFiller = textHeight + textPadding * 2 + marginTarget
    if (bottom + edgeLength + heightFiller < screenHeight) {
        return TextPosition.Bottom(this, textPadding, marginTarget, textWidth, textHeight)
    }
    if (top - edgeLength - heightFiller > 0) {
        return TextPosition.Top(this, textPadding, marginTarget, textWidth, textHeight)
    }
    return TextPosition.Empty
}

