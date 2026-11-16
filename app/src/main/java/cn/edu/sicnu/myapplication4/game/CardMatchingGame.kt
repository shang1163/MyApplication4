package cn.edu.sicnu.myapplication4.game

/**
 * 翻牌配对游戏模型。
 * 参照附件框架：从 [Deck] 随机抽牌，点数/花色判定配对并计算得分。
 * 两张牌与三张牌游戏共用该模型，区别只在构造时传入的牌数。
 */
class CardMatchingGame(private val count: Int) {

    companion object {
        /** 完整牌堆的牌数。 */
        const val MAX_CARD_COUNT = 52

        /** 至少两张牌才能进行配对。 */
        const val MIN_CARD_COUNT = 2
    }

    private val cardList = mutableListOf<Card>()

    /** 当前牌面，供 View 同步显示。 */
    val cards: List<Card>
        get() = cardList

    var score: Int = 0
        private set

    /** 本局是否已经完成判定。 */
    var isFinished: Boolean = false
        private set

    val MISMATCH_PENALTY = 2
    val MATCH_BONUS = 4
    val COST_TO_CHOOSE = 1

    init {
        require(count in MIN_CARD_COUNT..MAX_CARD_COUNT) {
            "count 必须在 $MIN_CARD_COUNT..$MAX_CARD_COUNT 之间"
        }
        reset()
    }

    /** 重新抽牌并清空分数，用于「重新开始」一局游戏。 */
    fun reset() {
        cardList.clear()
        score = 0
        isFinished = false
        val deck = Deck()
        repeat(count) {
            deck.drawRandomCard()?.let { cardList.add(it) }
        }
    }

    fun cardAtIndex(index: Int): Card = cardList[index]

    val cardCount: Int
        get() = cardList.size

    /** 已配对成功的牌数。 */
    val matchedCount: Int
        get() = cardList.count { it.isMatched }

    /** 本局是否配对成功（全部牌都已配对）。 */
    val isWin: Boolean
        get() = cardList.isNotEmpty() && cardList.all { it.isMatched }

    /**
     * 选择一张牌：
     * 已选中的牌再次点击会取消选择；
     * 当所有牌都被选中时统一判定：所有牌点数相同或花色相同则配对成功，
     * 否则扣 [MISMATCH_PENALTY]；
     * 每次选牌扣 [COST_TO_CHOOSE]，判定后本局结束。
     */
    fun chooseCardAtIndex(index: Int) {
        if (isFinished) return

        val card = cardAtIndex(index)
        if (card.isMatched) return

        if (card.isChosen) {
            card.isChosen = false
            return
        }

        card.isChosen = true
        score -= COST_TO_CHOOSE

        val chosenCards = cardList.filter { it.isChosen && !it.isMatched }
        if (chosenCards.size == cardCount) {
            judge(chosenCards)
        }
    }

    private fun judge(chosenCards: List<Card>) {
        val matchScore = chosenCards.first().match(chosenCards.drop(1).toTypedArray())
        if (matchScore > 0) {
            score += matchScore * MATCH_BONUS
            chosenCards.forEach { it.isMatched = true }
        } else {
            score -= MISMATCH_PENALTY
        }
        isFinished = true
    }
}
