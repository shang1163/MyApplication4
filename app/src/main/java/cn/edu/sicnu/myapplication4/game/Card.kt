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
     * 与另一张牌比较：
     * 点数相同得 4 分，仅花色相同得 1 分，否则不得分。
     */
    fun match(otherCards: Array<Card>): Int {
        var score = 0
        if (otherCards.size == 1) {
            val otherCard = otherCards[0]
            if (otherCard.rank == rank) {
                score = 4
            } else if (otherCard.suit == suit) {
                score = 1
            }
        }
        return score
    }
}
