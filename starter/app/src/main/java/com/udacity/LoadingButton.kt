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
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.animation.addListener
import androidx.core.content.ContextCompat
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private var textWidth = 0f
    private var textSizing = resources.getDimension(R.dimen.default_text_size)
    private var circleHorizontalOffset = textSizing / 2

    private var progress = 0f;

    private var valueAnimator = ValueAnimator()
    private var title = "Download"

    var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when (new) {
            ButtonState.Loading -> startAnimation()
            ButtonState.Completed -> stopAnimation()
        }


    }
    private val paint = Paint().apply {
        isAntiAlias = true;
        textSize = textSizing
    }

    init {
        valueAnimator = ValueAnimator.ofFloat(0f, 1f);
        valueAnimator.setDuration(5500)
        valueAnimator.repeatCount=ValueAnimator.INFINITE
        valueAnimator.addUpdateListener {
            progress = it.animatedValue as Float
            invalidate()
            Log.d("Animations", progress.toString())
        }
        valueAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                progress = 0f
            }
        })
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas != null) {
            drawBackgroundColor(canvas)
            drawProgressBackground(canvas)
            drawTitle(canvas)
            drawCirclarProgress(canvas)

        }
        Log.d("Animations", progress.toString())

    }

    private fun drawTitle(canvas: Canvas) {
        paint.color = Color.WHITE
        textWidth = paint.measureText(title)
        canvas.drawText(
            title,
            widthSize / 2 - textWidth / 2,
            heightSize / 2 - (paint.descent() + paint.ascent()) / 2,
            paint
        )

    }

    private fun drawCirclarProgress(canvas: Canvas) {
        canvas.save()
        canvas.translate(
            widthSize / 2 + textWidth / 2 + circleHorizontalOffset,
            heightSize / 2 - textSizing / 2
        )
        paint.color = ContextCompat.getColor(context, R.color.colorAccent)
        canvas.drawArc(RectF(0f, 0f, textSizing, textSizing), 0F, progress * 360f, true, paint)
        canvas.restore()
    }

    private fun drawBackgroundColor(canvas: Canvas) {
        paint.color = ContextCompat.getColor(context, R.color.colorPrimary)
        canvas.drawRect(0f, 0f, widthSize.toFloat(), heightSize.toFloat(), paint)

    }

    private fun drawProgressBackground(canvas: Canvas?) {
        paint.color = ContextCompat.getColor(context, R.color.colorPrimaryDark)
        canvas?.drawRect(0f, 0f, progress * widthSize.toFloat(), heightSize.toFloat(), paint)
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

    private fun startAnimation() {
        title="we are loading ..."
        valueAnimator.start()

    }

    private fun stopAnimation() {
        title="Download"
        progress=0f
        Log.d("Adham", "stopAnimation")
        valueAnimator.cancel()
        invalidate()
    }
}


