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
        /** 一局配对所需的牌数：本实验为三张牌玩法。 */
        const val MATCH_GROUP_SIZE = 3

        /** 三张牌点数全部相同时的匹配分值。 */
        private const val TRIPLE_RANK_SCORE = 6

        /** 三张牌花色全部相同时的匹配分值。 */
        private const val TRIPLE_SUIT_SCORE = 3

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
     * 三张牌一起比较：
     * 三张点数全部相同得 6 分，三张花色全部相同得 3 分，否则不得分。
     * [otherCards] 应为另外两张已选中的牌。
     */
    fun match(otherCards: Array<Card>): Int {
        var score = 0
        val allCards = otherCards + this
        if (allCards.size == MATCH_GROUP_SIZE) {
            if (allCards.all { it.rank == rank }) {
                score = TRIPLE_RANK_SCORE
            } else if (allCards.all { it.suit == suit }) {
                score = TRIPLE_SUIT_SCORE
            }
        }
        return score
    }
}
