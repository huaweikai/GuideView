package com.hua.guide


import android.graphics.RectF

internal var edgeLength = 8.dp

// 优先放在右边，优先放在下面
sealed class TextPosition(
    val targetBounds: RectF,
    textPadding: Int,
    val marginTarget: Int,
    textWidth: Int,
    textHeight: Int
) {

    val height = textHeight + textPadding * 2
    val width = textWidth + textPadding * 2

    class Top(
        targetBounds: RectF,
        textPadding: Int,
        marginTarget: Int,
        textWidth: Int,
        textHeight: Int
    ): TextPosition(targetBounds, textPadding, marginTarget, textWidth, textHeight) {
        override fun fromBounds(): RectF {
            val left = targetBounds.centerX() - width / 2
            val right = left + width
            val top = targetBounds.top - height - marginTarget
            val bottom = top + height
            return RectF(left, top, right, bottom)
        }
    }
    class Bottom(
        targetBounds: RectF,
        textPadding: Int,
        marginTarget: Int,
        textWidth: Int,
        textHeight: Int
    ): TextPosition(targetBounds, textPadding, marginTarget, textWidth, textHeight) {
        override fun fromBounds(): RectF {
            val left = targetBounds.centerX() - width / 2
            val right = left + width
            val top = targetBounds.bottom + marginTarget
            val bottom = top + height
            return RectF(left, top, right, bottom)
        }
    }
    class Left(
        targetBounds: RectF,
        textPadding: Int,
        marginTarget: Int,
        textWidth: Int,
        textHeight: Int
    ): TextPosition(targetBounds, textPadding, marginTarget, textWidth, textHeight) {
        override fun fromBounds(): RectF {
            val left = targetBounds.left - width - marginTarget
            val right = left + width
            val top = targetBounds.centerY() - height / 2
            val bottom = top + height
            return RectF(left, top, right, bottom)
        }
    }
    class Right(
        targetBounds: RectF,
        textPadding: Int,
        marginTarget: Int,
        textWidth: Int,
        textHeight: Int
    ): TextPosition(targetBounds, textPadding, marginTarget, textWidth, textHeight) {
        override fun fromBounds(): RectF {
            val left = targetBounds.right + marginTarget
            val right = left + width
            val top = targetBounds.centerY() -  height / 2
            val bottom = top + height
            return RectF(left, top, right, bottom)
        }
    }
    object Empty: TextPosition(RectF(), 0, 0, 0, 0)


    open fun fromBounds(): RectF = targetBounds

}

internal fun RectF.getTextBgPosition(
    textWidth: Int,
    textHeight: Int,
    textPadding: Int,
    marginIcon: Int
): RectF {
    val position = getPosition(textPadding, marginIcon, textWidth, textHeight)
    return position.fromBounds()
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

