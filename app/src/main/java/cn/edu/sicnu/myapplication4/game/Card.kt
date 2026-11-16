package cn.edu.sicnu.myapplication4.game

/**
 * 一张扑克牌。
 * 参照附件框架：花色、点数私有，公开选牌/配对状态，并提供 [match] 计分规则。
 */
class Card(
    private var suit: String,
    private var rank: String,
    var isChosen: Boolean = false,
    var isMatched: Boolean = false
) {

    companion object {
        /** 全部牌点数/花色相同时的匹配分值。 */
        private const val RANK_MATCH_SCORE = 6

        /** 全部牌花色相同时的匹配分值。 */
        private const val SUIT_MATCH_SCORE = 3

        val rankStrings = arrayOf("A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K")
        val validSuits = arrayOf("♥", "♦", "♠", "♣")
    }

    /** 花色符号，供界面绘制使用。 */
    val suitSymbol: String
        get() = suit

    /** 点数符号，供界面绘制使用。 */
    val rankSymbol: String
        get() = rank

    /** 红色花色用于界面着色。 */
    val isRed: Boolean
        get() = suit == "♥" || suit == "♦"

    override fun toString(): String = suit + rank

    /**
     * 两张或三张牌一起比较：
     * 所有牌点数相同得 6 分，所有牌花色相同得 3 分，否则不得分。
     * [otherCards] 为同一组中其余已选中的牌。
     */
    fun match(otherCards: Array<Card>): Int {
        val allCards = otherCards + this
        if (allCards.size < 2) return 0
        return when {
            allCards.all { it.rank == rank } -> RANK_MATCH_SCORE
            allCards.all { it.suit == suit } -> SUIT_MATCH_SCORE
            else -> 0
        }
    }
}
