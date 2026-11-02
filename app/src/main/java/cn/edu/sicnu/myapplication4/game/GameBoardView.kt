package cn.edu.sicnu.myapplication4.game

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import kotlin.math.roundToInt

/**
 * 翻牌游戏牌面 View。
 * 按 3 列网格排列所有牌，自动计算卡片尺寸并保持统一间距与比例。
 */
class GameBoardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {

    fun interface OnCardClickListener {
        fun onCardClicked(index: Int)
    }

    companion object {
        private const val COLUMNS = 3

        /** 牌宽与牌高的比例。 */
        private const val CARD_ASPECT = 0.72f
    }

    private val density = resources.displayMetrics.density
    private val gap = dp(10f)
    private val boardPadding = dp(12f)

    private var listener: OnCardClickListener? = null

    private var cellWidth = 0f
    private var cellHeight = 0f
    private var originX = 0f
    private var originY = 0f

    fun setOnCardClickListener(listener: OnCardClickListener?) {
        this.listener = listener
    }

    /** 将模型中的牌同步到界面。 */
    fun bindCards(cards: List<Card>) {
        if (childCount != cards.size) {
            removeAllViews()
            repeat(cards.size) {
                addView(PlayingCardView(context))
            }
        }
        cards.forEachIndexed { index, card ->
            val cardView = getChildAt(index) as PlayingCardView
            cardView.tag = "card_$index"
            cardView.bind(card, index) {
                listener?.onCardClicked(index)
            }
        }
        requestLayout()
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        val rows = (childCount + COLUMNS - 1) / COLUMNS

        if (childCount == 0 || width <= 0 || height <= 0) {
            setMeasuredDimension(width, height)
            return
        }

        val availableWidth = (width - boardPadding * 2 - gap * (COLUMNS - 1))
            .coerceAtLeast(1f)
        val availableHeight = (height - boardPadding * 2 - gap * (rows - 1))
            .coerceAtLeast(1f)

        var cardWidth = availableWidth / COLUMNS
        var cardHeight = cardWidth / CARD_ASPECT

        val maxCardHeight = availableHeight / rows
        if (cardHeight > maxCardHeight) {
            cardHeight = maxCardHeight
            cardWidth = cardHeight * CARD_ASPECT
        }

        cellWidth = cardWidth
        cellHeight = cardHeight

        val gridWidth = COLUMNS * cellWidth + gap * (COLUMNS - 1)
        val gridHeight = rows * cellHeight + gap * (rows - 1)
        originX = (width - gridWidth) / 2f
        originY = (height - gridHeight) / 2f

        val childWidthSpec = MeasureSpec.makeMeasureSpec(cellWidth.roundToInt(), MeasureSpec.EXACTLY)
        val childHeightSpec = MeasureSpec.makeMeasureSpec(cellHeight.roundToInt(), MeasureSpec.EXACTLY)
        for (i in 0 until childCount) {
            getChildAt(i).measure(childWidthSpec, childHeightSpec)
        }

        setMeasuredDimension(width, height)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            val column = i % COLUMNS
            val row = i / COLUMNS
            val left = (originX + column * (cellWidth + gap)).roundToInt()
            val top = (originY + row * (cellHeight + gap)).roundToInt()
            child.layout(left, top, left + child.measuredWidth, top + child.measuredHeight)
        }
    }

    override fun shouldDelayChildPressedState(): Boolean = false

    private fun dp(value: Float): Float = value * density
}
