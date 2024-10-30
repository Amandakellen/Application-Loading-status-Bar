package com.udacity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0

    private val valueAnimator = ValueAnimator()

    private var buttonText: String = context.getString(R.string.button_text)
    private var loadingColor: Int = Color.BLUE
    private var completedColor: Int = ContextCompat.getColor(context, R.color.colorPrimary)
    private var buttonColor: Int = ContextCompat.getColor(context, R.color.colorPrimary)
    private var borderColor: Int = Color.BLACK
    private var borderWidth: Float = 4f
    private var loadingProgress = 0f

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when (new) {
            ButtonState.Loading -> {
                startLoadingAnimation()
                buttonText = context.getString(R.string.button_text_loading)
            }

            ButtonState.Completed -> {
                loadingProgress = 0f
                buttonText = context.getString(R.string.button_text)
                invalidate()

            }

            ButtonState.Clicked -> { /* Lidar com o estado de clicado */
            }
        }
    }


    init {
        setOnClickListener {
            if (buttonState == ButtonState.Completed) {
                buttonState = ButtonState.Loading
            }
        }
    }

    private fun startLoadingAnimation() {
        valueAnimator.apply {
            setFloatValues(0f, 1f)
            duration = 2000
            addUpdateListener { animator ->
                loadingProgress = animator.animatedValue as Float
                invalidate()
            }
            start()

            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    buttonState = ButtonState.Completed
                }
            })

        }

    }



    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        val paint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
            color = buttonColor
        }

        canvas?.drawRoundRect(
            0f, 0f, widthSize.toFloat(), heightSize.toFloat(),
            16f, 16f, paint
        )


        if (borderWidth > 0) {
            paint.style = Paint.Style.STROKE
            paint.color = borderColor
            paint.strokeWidth = borderWidth
            canvas?.drawRoundRect(
                0f, 0f, widthSize.toFloat(), heightSize.toFloat(),
                16f, 16f, paint
            )
        }

        if (buttonState == ButtonState.Loading) {
            paint.color = loadingColor
            canvas?.drawRect(0f, 0f, widthSize * loadingProgress, heightSize.toFloat(), paint)
        }


        paint.style = Paint.Style.FILL
        paint.color = Color.WHITE
        paint.textSize = 40f
        val textWidth = paint.measureText(buttonText)
        val textX = (widthSize - textWidth) / 2
        val textY = (heightSize + paint.textSize) / 2 - 10
        canvas?.drawText(buttonText, textX, textY, paint)

    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
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
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

}