package com.udacity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var widthSize = 0
    private var heightSize = 0

    private val valueAnimator = ValueAnimator()
    private var loadingProgress = 0f
    private var loadingAngle = 0f
    private var textColor: Int = Color.WHITE

    private var buttonText: String = context.getString(R.string.button_text)
    private var loadingColor: Int = ContextCompat.getColor(context, R.color.colorPrimaryDark)
    private var completedColor: Int = ContextCompat.getColor(context, R.color.colorPrimary)
    private var buttonColor: Int = ContextCompat.getColor(context, R.color.colorPrimary)
    private var borderColor: Int = ContextCompat.getColor(context, R.color.colorPrimary)
    private var borderWidth: Float = 4f

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 40f
        textAlign = Paint.Align.CENTER
    }

    init {
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.LoadingButton, 0, 0)
            buttonColor = typedArray.getColor(R.styleable.LoadingButton_backgroundColor, buttonColor)
            textColor = typedArray.getColor(R.styleable.LoadingButton_textColor, Color.WHITE)
            typedArray.recycle()
        }
        paint.color = buttonColor
        paint.textSize = 50f
    }

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { _, _, new ->
        when (new) {
            ButtonState.Loading -> {
                startLoadingAnimation()
                buttonText = context.getString(R.string.button_text_loading)
            }

            ButtonState.Completed -> {
                loadingProgress = 0f
                buttonText = context.getString(R.string.button_text)
                valueAnimator?.cancel()
                loadingProgress = 0f
                buttonText = context.getString(R.string.button_text)
                invalidate()
            }

            ButtonState.Clicked -> {
                buttonState = ButtonState.Loading
            }
        }
    }

    fun setOnLoadingButtonClick() {
        buttonState = ButtonState.Clicked
    }

    fun downloadComplete() {
        buttonState = ButtonState.Completed
    }


    private fun startLoadingAnimation() {
        valueAnimator.apply {
            setFloatValues(0f, 1f)
            duration = 2000
            repeatCount = ValueAnimator.INFINITE
            addUpdateListener { animator ->
                loadingProgress = animator.animatedValue as Float
                loadingAngle += 5f
                invalidate()
            }
            start()
        }
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        paint.color = buttonColor
        paint.style = Paint.Style.FILL
        canvas?.drawRoundRect(
            0f, 0f, widthSize.toFloat(), heightSize.toFloat(),
            16f, 16f, paint
        )

        if (borderWidth > 0) {
            paint.color = borderColor
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = borderWidth
            canvas?.drawRoundRect(
                0f, 0f, widthSize.toFloat(), heightSize.toFloat(),
                16f, 16f, paint
            )
        }
        if (buttonState == ButtonState.Loading) {
            paint.color = loadingColor
            paint.style = Paint.Style.FILL
            canvas?.drawRect(0f, 0f, widthSize * loadingProgress, heightSize.toFloat(), paint)

            paint.color = Color.WHITE
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 8f

            val radius = heightSize / 4f
            val centerX = widthSize / 2f
            val centerY = heightSize / 2f
            val oval = RectF(
                centerX - radius, centerY - radius,
                centerX + radius, centerY + radius
            )

            canvas?.drawArc(
                oval, loadingAngle, 270f, false, paint
            )
        }

        paint.color = Color.WHITE
        paint.style = Paint.Style.FILL
        val textX = widthSize / 2f
        val textY = (heightSize / 2f) - (paint.descent() + paint.ascent()) / 2
        canvas?.drawText(buttonText, textX, textY, paint)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (buttonState == ButtonState.Loading) {
            return false
        }

        return super.onTouchEvent(event) || event?.let {
            when (it.action) {
                MotionEvent.ACTION_DOWN -> {
                    buttonState = ButtonState.Clicked
                    true
                }
                MotionEvent.ACTION_UP -> {
                    buttonText = context.getString(R.string.button_text)
                    buttonState = ButtonState.Completed
                    true
                }
                else -> false
            }
        } ?: false
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val minh: Int = paddingTop + paddingBottom + suggestedMinimumHeight
        val h: Int = resolveSizeAndState(minh, heightMeasureSpec, 0)

        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }
}
