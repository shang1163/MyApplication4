package cn.edu.sicnu.myapplication4.game

import java.util.Random

/**
 * 52 张扑克牌组成的牌堆。
 * 参照附件框架，通过 [drawRandomCard] 随机抽牌并移除。
 */
class Deck {

    private val cards = mutableListOf<Card>()
    private val random = Random()

    init {
        for (suit in Card.validSuits) {
            for (rank in Card.rankStrings) {
                cards.add(Card(suit = suit, rank = rank))
            }
        }
    }

    fun drawRandomCard(): Card? {
        if (cards.isEmpty()) return null
        return cards.removeAt(random.nextInt(cards.size))
    }
}
