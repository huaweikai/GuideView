package com.hua.guide

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.view.MotionEvent
import android.view.View
import android.view.ViewManager
import android.view.ViewTreeObserver
import androidx.core.graphics.withSave
import kotlin.math.roundToInt

@SuppressLint("ViewConstructor")
class GuideView @JvmOverloads constructor(
    context: Context,
    private val tapTarget: TapTarget,
    private val parent: ViewManager? = null
) : View(context) {

    private val globalLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
        initTapTarget()
    }

    init {
        viewTreeObserver.addOnGlobalLayoutListener(globalLayoutListener)
        super.setOnClickListener {
            dismiss()
        }
    }

    private var titleLayout: Layout? = null

    private var descriptionLayout: Layout? = null

    private val staticTargetBounds = RectF()

    private val shadowTargetBounds = RectF()

    private val textPadding = 4.dp

    private val textMarginIcon = 16.dp + tapTarget.shadowWidth

    private val textRectBounds = RectF()

    private fun initTapTarget() {
        updateTextLayouts()
        tapTarget.onReady {
            staticTargetBounds.set(tapTarget.bounds)
            val offset = IntArray(2)
            getLocationOnScreen(offset)
            staticTargetBounds.offset(-offset[0].toFloat(), -offset[1].toFloat())
            if (tapTarget.targetPadding != 0) {
                staticTargetBounds.inset(-tapTarget.targetPadding.toFloat(), -tapTarget.targetPadding.toFloat())
            }
            shadowTargetBounds.set(staticTargetBounds)
            shadowTargetBounds.inset(-2.dp.toFloat(), -2.dp.toFloat())
            textRectBounds.set(staticTargetBounds)
            textRectBounds.set(textRectBounds.getTextBgPosition(textWidth, textHeight, textPadding, textMarginIcon))
            invalidate()
        }
    }

    private var lastTouchX = 0f

    private var lastTouchY = 0f

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            lastTouchX = it.x
            lastTouchY = it.y
        }
        return super.onTouchEvent(event)
    }

    private fun updateTextLayouts() {
        val textWidth = width.coerceAtMost(tapTarget.textWidth)
        if (textWidth <= 0) return
        titleLayout = StaticLayout.Builder
            .obtain(tapTarget.title, 0, tapTarget.title.length, titlePaint, textWidth)
            .setAlignment(Layout.Alignment.ALIGN_NORMAL)
            .setLineSpacing(0f, 1f)
            .setIncludePad(false)
            .build()
        val description = tapTarget.description ?: return
        descriptionLayout = StaticLayout.Builder
            .obtain(description, 0, description.length, descriptionPaint, textWidth)
            .setAlignment(Layout.Alignment.ALIGN_NORMAL)
            .setLineSpacing(0f, 1f)
            .setIncludePad(false)
            .build()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // 画tapTargetView
        canvas.drawRoundRect(
            staticTargetBounds,
            tapTarget.radius,
            tapTarget.radius,
            guidePaint
        )
        // 画阴影
        canvas.drawRoundRect(
            shadowTargetBounds,
            tapTarget.radius,
            tapTarget.radius,
            shadowPaint
        )
        // 画Text的背景
        canvas.drawRoundRect(
            textRectBounds,
            tapTarget.radius,
            tapTarget.radius,
            textRectPaint
        )
        // 画文字
        canvas.withSave {
            canvas.translate(textRectBounds.left + textPadding, textRectBounds.top + textPadding)
            titleLayout?.draw(canvas)
            descriptionLayout?.let {
                canvas.translate(0f, ((titleLayout?.height ?: 0) + 8.dp).toFloat())
                it.draw(canvas)
            }
        }
    }

    override fun setOnClickListener(l: OnClickListener?) {}

    fun dismiss() {
        viewTreeObserver.removeOnGlobalLayoutListener(globalLayoutListener)
        parent?.removeView(this)
    }

    private val titlePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = tapTarget.textSize.toFloat()
        typeface = Typeface.create("snas-serif-medium", Typeface.NORMAL)
    }

    private val textRectPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.FILL
        setShadowLayer(10f, 2f, 2f, Color.BLACK.setAlpha(0.3f))
    }

    private val descriptionPaint: TextPaint by lazy {
        TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = tapTarget.descriptionTextSize.toFloat()
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
            alpha = (0.54f * 255).roundToInt()
        }
    }

    private val guidePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = tapTarget.guideColor
        style = Paint.Style.STROKE
        strokeWidth = tapTarget.guideWidth.toFloat()
    }

    private val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = tapTarget.shadowColor
        style = Paint.Style.STROKE
        maskFilter = BlurMaskFilter(tapTarget.shadowWidth.toFloat(), BlurMaskFilter.Blur.NORMAL)
        alpha = 100
        strokeWidth = tapTarget.shadowWidth.toFloat()
    }

    private val textHeight: Int
        get() {
            val titleLayout = this.titleLayout ?: return 0
            val descLayout = this.descriptionLayout ?: return titleLayout.height
            return titleLayout.height + descLayout.height
        }

    private val textWidth: Int
        get() {
            val titleLayout = this.titleLayout ?: return 0
            val descriptionLayout =
                this.descriptionLayout ?: return titleLayout.width
            return titleLayout.width.coerceAtLeast(descriptionLayout.width)
        }
}