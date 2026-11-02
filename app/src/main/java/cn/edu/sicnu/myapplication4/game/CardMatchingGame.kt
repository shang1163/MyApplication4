package cn.edu.sicnu.myapplication4.game

/**
 * 翻牌配对游戏模型。
 * 参照附件框架：从 [Deck] 随机抽牌，按点数/花色判定配对并计算得分。
 */
class CardMatchingGame(count: Int) {

    companion object {
        /** 完整牌堆的牌数。 */
        const val MAX_CARD_COUNT = 52
    }

    private val cardList = mutableListOf<Card>()

    /** 当前牌面，供 View 同步显示。 */
    val cards: List<Card>
        get() = cardList

    var score: Int = 0
        private set

    val MISMATCH_PENALTY = 2
    val MATCH_BONUS = 4
    val COST_TO_CHOOSE = 1

    init {
        require(count in 1..MAX_CARD_COUNT) { "count 必须在 1..$MAX_CARD_COUNT 之间" }
        val deck = Deck()
        repeat(count) {
            deck.drawRandomCard()?.let { cardList.add(it) }
        }
    }

    fun cardAtIndex(index: Int): Card = cardList[index]

    val cardCount: Int
        get() = cardList.size

    /** 已配对的牌数（成对增加，始终为偶数）。 */
    val matchedCount: Int
        get() = cardList.count { it.isMatched }

    /** 所有牌是否都已配对。 */
    val isFinished: Boolean
        get() = cardList.all { it.isMatched }

    /**
     * 选择一张牌：
     * 已选中的牌再次点击会取消选择；
     * 与另一张选中的牌点数相同得 4 分、花色相同得 1 分，乘以 [MATCH_BONUS]；
     * 配对失败扣 [MISMATCH_PENALTY]，每次选牌扣 [COST_TO_CHOOSE]。
     */
    fun chooseCardAtIndex(index: Int) {
        val card = cardAtIndex(index)
        if (card.isMatched) return

        if (card.isChosen) {
            card.isChosen = false
            return
        }

        for (otherCard in cardList) {
            if (otherCard.isChosen && !otherCard.isMatched) {
                val matchScore = card.match(arrayOf(otherCard))
                if (matchScore > 0) {
                    score += matchScore * MATCH_BONUS
                    otherCard.isMatched = true
                    card.isMatched = true
                } else {
                    score -= MISMATCH_PENALTY
                    otherCard.isChosen = false
                }
                break
            }
        }

        score -= COST_TO_CHOOSE
        card.isChosen = true
    }
}
