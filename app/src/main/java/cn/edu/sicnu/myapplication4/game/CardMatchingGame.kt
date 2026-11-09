package cn.edu.sicnu.myapplication4.game

/**
 * 三张牌配对游戏模型。
 * 参照附件框架：从 [Deck] 随机抽牌，按点数/花色判定配对并计算得分。
 */
class CardMatchingGame(private val count: Int) {

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
        reset()
    }

    /** 重新抽牌并清空分数，用于「重新开始」一局游戏。 */
    fun reset() {
        cardList.clear()
        score = 0
        val deck = Deck()
        repeat(count) {
            deck.drawRandomCard()?.let { cardList.add(it) }
        }
    }

    fun cardAtIndex(index: Int): Card = cardList[index]

    val cardCount: Int
        get() = cardList.size

    /** 已配对成功的牌数（三张一组，始终为 3 的倍数）。 */
    val matchedCount: Int
        get() = cardList.count { it.isMatched }

    /** 所有牌是否都已配对。 */
    val isFinished: Boolean
        get() = cardList.all { it.isMatched }

    /**
     * 选择一张牌：
     * 已选中的牌再次点击会取消选择；
     * 累计选满三张时统一判定：三张点数相同或花色相同则配对成功，
     * 否则扣 [MISMATCH_PENALTY] 并取消前两张的选择；
     * 每次选牌扣 [COST_TO_CHOOSE]。
     */
    fun chooseCardAtIndex(index: Int) {
        val card = cardAtIndex(index)
        if (card.isMatched) return

        if (card.isChosen) {
            card.isChosen = false
            return
        }

        val chosenCards = cardList.filter { it.isChosen && !it.isMatched }
        if (chosenCards.size == Card.MATCH_GROUP_SIZE - 1) {
            val matchScore = card.match(chosenCards.toTypedArray())
            if (matchScore > 0) {
                score += matchScore * MATCH_BONUS
                chosenCards.forEach { it.isMatched = true }
                card.isMatched = true
            } else {
                score -= MISMATCH_PENALTY
                chosenCards.forEach { it.isChosen = false }
            }
        }

        score -= COST_TO_CHOOSE
        card.isChosen = true
    }
}
