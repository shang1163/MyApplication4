package cn.edu.sicnu.myapplication4.game

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.core.content.ContextCompat
import cn.edu.sicnu.myapplication4.R
import kotlin.math.abs
import kotlin.math.min

/**
 * 单张牌的绘制与点击区域。
 * 背面为渐变图案，正面显示牌面符号，配对成功后变为绿色并显示对勾。
 */
class PlayingCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        private const val FLIP_DURATION = 260L
    }

    private val density = resources.displayMetrics.density

    private val cornerRadius = dp(16f)

    private val backPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    private val facePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
    }
    private val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = ContextCompat.getColor(context, R.color.card_shadow)
    }
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT_BOLD
    }
    private val checkPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
        color = ContextCompat.getColor(context, R.color.white)
    }
    private val checkCirclePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = ContextCompat.getColor(context, R.color.success)
    }

    private val colorCardBackStart = ContextCompat.getColor(context, R.color.card_back_start)
    private val colorCardBackEnd = ContextCompat.getColor(context, R.color.card_back_end)
    private val colorCardFace = ContextCompat.getColor(context, R.color.card_face)
    private val colorCardFaceMatched = ContextCompat.getColor(context, R.color.card_face_matched)
    private val colorCardBorder = ContextCompat.getColor(context, R.color.card_border)
    private val colorSuccess = ContextCompat.getColor(context, R.color.success)
    private val colorSymbolRed = ContextCompat.getColor(context, R.color.card_symbol_red)
    private val colorSymbolDark = ContextCompat.getColor(context, R.color.card_symbol_dark)

    private var backShader: LinearGradient? = null

    private var card: Card? = null

    /** 当前动画/绘制显示的是否为正面。 */
    private var displayedFaceUp = false

    /** 0 表示完全背面，1 表示完全正面，中间值为翻转动画过程。 */
    private var flipProgress = 0f

    private var flipAnimator: ValueAnimator? = null

    init {
        isClickable = true
        isFocusable = true
    }

    /** 绑定数据并触发翻转动画。 */
    fun bind(card: Card, index: Int, onClick: () -> Unit) {
        this.card = card

        val shouldFaceUp = card.isChosen || card.isMatched
        if (shouldFaceUp != displayedFaceUp) {
            animateFlip(shouldFaceUp)
        } else if (flipAnimator?.isRunning != true) {
            flipProgress = if (shouldFaceUp) 1f else 0f
        }

        contentDescription = when {
            card.isMatched -> context.getString(R.string.cd_card_matched, index + 1, card.toString())
            card.isChosen -> context.getString(R.string.cd_card_face, index + 1, card.toString())
            else -> context.getString(R.string.cd_card_back, index + 1)
        }
        setOnClickListener { onClick() }
        invalidate()
    }

    private fun animateFlip(faceUp: Boolean) {
        displayedFaceUp = faceUp
        flipAnimator?.cancel()
        flipAnimator = ValueAnimator.ofFloat(flipProgress, if (faceUp) 1f else 0f).apply {
            duration = FLIP_DURATION
            interpolator = DecelerateInterpolator()
            addUpdateListener {
                flipProgress = it.animatedValue as Float
                invalidate()
            }
            start()
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        backShader = LinearGradient(
            0f, 0f, 0f, h.toFloat(),
            colorCardBackStart, colorCardBackEnd,
            Shader.TileMode.CLAMP
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val card = card ?: return
        if (width <= 0 || height <= 0) return

        val rect = RectF(dp(1f), dp(2f), width - dp(1f), height - dp(2f))

        // 简单的卡片投影
        canvas.drawRoundRect(
            rect.left + dp(1f), rect.top + dp(3f),
            rect.right + dp(1f), rect.bottom + dp(3f),
            cornerRadius, cornerRadius, shadowPaint
        )

        val squeeze = abs(1f - 2f * flipProgress)
        if (squeeze < 0.02f) return

        canvas.save()
        canvas.scale(squeeze, 1f, width / 2f, height / 2f)
        if (flipProgress < 0.5f) {
            drawBack(canvas, rect)
        } else {
            drawFace(canvas, rect, card)
        }
        canvas.restore()
    }

    private fun drawBack(canvas: Canvas, rect: RectF) {
        backPaint.shader = backShader
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, backPaint)

        borderPaint.color = 0x55FFFFFF
        borderPaint.strokeWidth = dp(1.2f)
        val inset = dp(7f)
        canvas.drawRoundRect(
            RectF(rect.left + inset, rect.top + inset, rect.right - inset, rect.bottom - inset),
            cornerRadius - dp(5f), cornerRadius - dp(5f), borderPaint
        )

        textPaint.color = 0xE6FFFFFF.toInt()
        textPaint.textSize = min(rect.width(), rect.height()) * 0.34f
        val baseline = rect.centerY() - (textPaint.ascent() + textPaint.descent()) / 2f
        canvas.drawText("?", rect.centerX(), baseline, textPaint)
    }

    private fun drawFace(canvas: Canvas, rect: RectF, card: Card) {
        facePaint.color = if (card.isMatched) colorCardFaceMatched else colorCardFace
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, facePaint)

        borderPaint.color = if (card.isMatched) colorSuccess else colorCardBorder
        borderPaint.strokeWidth = if (card.isMatched) dp(2f) else dp(1.2f)
        canvas.drawRoundRect(
            RectF(rect.left + dp(1f), rect.top + dp(1f), rect.right - dp(1f), rect.bottom - dp(1f)),
            cornerRadius - dp(1f), cornerRadius - dp(1f), borderPaint
        )

        val symbolColor = if (card.isRed) colorSymbolRed else colorSymbolDark
        val minSide = min(rect.width(), rect.height())

        // 中央大花色
        textPaint.textAlign = Paint.Align.CENTER
        textPaint.color = symbolColor
        textPaint.textSize = minSide * 0.40f
        val centerBaseline = rect.centerY() - (textPaint.ascent() + textPaint.descent()) / 2f
        canvas.drawText(card.suitSymbol, rect.centerX(), centerBaseline, textPaint)

        // 左上角点数
        textPaint.textAlign = Paint.Align.LEFT
        textPaint.textSize = minSide * 0.20f
        val rankBaseline = rect.top + dp(10f) - textPaint.ascent()
        canvas.drawText(card.rankSymbol, rect.left + dp(9f), rankBaseline, textPaint)

        if (card.isMatched) {
            val cx = rect.right - dp(14f)
            val cy = rect.top + dp(14f)
            val radius = dp(8f)
            canvas.drawCircle(cx, cy, radius, checkCirclePaint)

            val check = Path().apply {
                moveTo(cx - radius * 0.45f, cy)
                lineTo(cx - radius * 0.10f, cy + radius * 0.38f)
                lineTo(cx + radius * 0.50f, cy - radius * 0.38f)
            }
            checkPaint.strokeWidth = dp(1.8f)
            canvas.drawPath(check, checkPaint)
        }
    }

    override fun onDetachedFromWindow() {
        flipAnimator?.cancel()
        flipAnimator = null
        super.onDetachedFromWindow()
    }

    private fun dp(value: Float): Float = value * density
}
