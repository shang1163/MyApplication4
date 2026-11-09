package cn.edu.sicnu.myapplication4.game

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeTrue
import org.junit.Test

/**
 * 翻牌游戏模型单元测试：覆盖 [Card] 三张牌匹配规则、[Deck] 抽牌逻辑与
 * [CardMatchingGame] 选牌计分、reset 重置。
 */
class CardMatchingGameTest {

    // ---------- Card ----------

    @Test
    fun card_match_threeSameRank_scoresSix() {
        val a = Card("♥", "A")
        val b = Card("♠", "A")
        val c = Card("♦", "A")
        assertEquals(6, a.match(arrayOf(b, c)))
    }

    @Test
    fun card_match_threeSameSuit_scoresThree() {
        val a = Card("♦", "3")
        val b = Card("♦", "9")
        val c = Card("♦", "K")
        assertEquals(3, a.match(arrayOf(b, c)))
    }

    @Test
    fun card_match_threeMixedCards_scoresZero() {
        val a = Card("♥", "3")
        val b = Card("♠", "9")
        val c = Card("♦", "K")
        assertEquals(0, a.match(arrayOf(b, c)))
    }

    @Test
    fun card_match_requiresExactlyThreeCards() {
        val a = Card("♥", "A")
        assertEquals(0, a.match(arrayOf(Card("♥", "A"))))
    }

    @Test
    fun card_toString_isSuitFollowedByRank() {
        assertEquals("♥A", Card("♥", "A").toString())
    }

    @Test
    fun card_isRed_onlyForHeartAndDiamond() {
        assertTrue(Card("♥", "A").isRed)
        assertTrue(Card("♦", "A").isRed)
        assertFalse(Card("♠", "A").isRed)
        assertFalse(Card("♣", "A").isRed)
    }

    // ---------- Deck ----------

    @Test
    fun deck_containsFiftyTwoUniqueCards() {
        val deck = Deck()
        val drawn = mutableListOf<Card>()
        while (true) {
            drawn.add(deck.drawRandomCard() ?: break)
        }
        assertEquals(52, drawn.size)
        assertEquals(52, drawn.map { it.toString() }.distinct().size)
    }

    @Test
    fun deck_returnsNullAfterExhausted() {
        val deck = Deck()
        repeat(52) { deck.drawRandomCard() }
        assertNull(deck.drawRandomCard())
    }

    // ---------- CardMatchingGame ----------

    @Test
    fun game_startsWithRequestedCardCount_andZeroScore() {
        val game = CardMatchingGame(24)
        assertEquals(24, game.cardCount)
        assertEquals(0, game.score)
        assertTrue(game.cards.none { it.isChosen || it.isMatched })
        assertFalse(game.isFinished)
    }

    @Test(expected = IllegalArgumentException::class)
    fun game_rejectsZeroCards() {
        CardMatchingGame(0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun game_rejectsMoreThanFullDeck() {
        CardMatchingGame(53)
    }

    @Test
    fun choosingCard_costsOnePointAndFlipsIt() {
        val game = CardMatchingGame(12)
        game.chooseCardAtIndex(0)

        assertEquals(-game.COST_TO_CHOOSE, game.score)
        assertTrue(game.cards[0].isChosen)
    }

    @Test
    fun choosingSameCardTwice_unselectsItWithoutExtraCost() {
        val game = CardMatchingGame(12)
        game.chooseCardAtIndex(0)
        game.chooseCardAtIndex(0)

        assertFalse(game.cards[0].isChosen)
        assertEquals(-game.COST_TO_CHOOSE, game.score)
    }

    @Test
    fun matchingTriple_scoresBonus_andMarksAllThreeMatched() {
        val game = CardMatchingGame(12)
        val triple = findMatchingTriple(game.cards)
        assumeTrue("12 张牌中必定存在同花色的三张", triple != null)
        val (first, second, third) = triple!!

        game.chooseCardAtIndex(first)
        game.chooseCardAtIndex(second)
        game.chooseCardAtIndex(third)

        val cards = game.cards
        val baseScore = cards[first].match(arrayOf(cards[second], cards[third]))
        assertEquals(baseScore * game.MATCH_BONUS - 3 * game.COST_TO_CHOOSE, game.score)
        assertTrue(cards[first].isMatched)
        assertTrue(cards[second].isMatched)
        assertTrue(cards[third].isMatched)
        assertEquals(3, game.matchedCount)
    }

    @Test
    fun mismatchedTriple_appliesPenalty_andClearsEarlierSelections() {
        val game = CardMatchingGame(12)
        val triple = findMismatchingTriple(game.cards)
        assumeTrue("随机牌面中通常存在无法配对的三张牌", triple != null)
        val (first, second, third) = triple!!

        game.chooseCardAtIndex(first)
        game.chooseCardAtIndex(second)
        game.chooseCardAtIndex(third)

        assertEquals(
            -(game.MISMATCH_PENALTY + 3 * game.COST_TO_CHOOSE),
            game.score
        )
        assertFalse(game.cards[first].isChosen)
        assertFalse(game.cards[second].isChosen)
        assertTrue(game.cards[third].isChosen)
        assertEquals(0, game.matchedCount)
    }

    @Test
    fun choosingMatchedCard_isIgnored() {
        val game = CardMatchingGame(12)
        val triple = findMatchingTriple(game.cards)
        assumeTrue("12 张牌中必定存在同花色的三张", triple != null)
        val (first, second, third) = triple!!

        game.chooseCardAtIndex(first)
        game.chooseCardAtIndex(second)
        game.chooseCardAtIndex(third)
        val scoreAfterMatch = game.score

        game.chooseCardAtIndex(first)

        assertEquals(scoreAfterMatch, game.score)
        assertTrue(game.cards[first].isMatched)
    }

    @Test
    fun reset_restoresCardsAndClearsScore() {
        val game = CardMatchingGame(12)
        val triple = findMatchingTriple(game.cards)
        assumeTrue("12 张牌中必定存在同花色的三张", triple != null)
        val (first, second, third) = triple!!
        game.chooseCardAtIndex(first)
        game.chooseCardAtIndex(second)
        game.chooseCardAtIndex(third)
        assertTrue(game.score != 0)

        game.reset()

        assertEquals(12, game.cardCount)
        assertEquals(0, game.score)
        assertEquals(0, game.matchedCount)
        assertTrue(game.cards.none { it.isChosen || it.isMatched })
        assertFalse(game.isFinished)
    }

    // ---------- helpers ----------

    private fun findMatchingTriple(cards: List<Card>): Triple<Int, Int, Int>? {
        for (i in cards.indices) {
            for (j in i + 1 until cards.size) {
                for (k in j + 1 until cards.size) {
                    if (cards[i].match(arrayOf(cards[j], cards[k])) > 0) {
                        return Triple(i, j, k)
                    }
                }
            }
        }
        return null
    }

    private fun findMismatchingTriple(cards: List<Card>): Triple<Int, Int, Int>? {
        for (i in cards.indices) {
            for (j in i + 1 until cards.size) {
                for (k in j + 1 until cards.size) {
                    if (cards[i].match(arrayOf(cards[j], cards[k])) == 0) {
                        return Triple(i, j, k)
                    }
                }
            }
        }
        return null
    }
}
