package cn.edu.sicnu.myapplication4.game

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeTrue
import org.junit.Test

/**
 * 翻牌游戏模型单元测试：覆盖 [Card] 匹配规则、[Deck] 抽牌逻辑与 [CardMatchingGame] 计分流程。
 */
class CardMatchingGameTest {

    // ---------- Card ----------

    @Test
    fun card_match_sameRank_scoresFour() {
        val a = Card("♥", "A")
        val b = Card("♠", "A")
        assertEquals(4, a.match(arrayOf(b)))
    }

    @Test
    fun card_match_sameSuit_scoresOne() {
        val a = Card("♦", "3")
        val b = Card("♦", "9")
        assertEquals(1, a.match(arrayOf(b)))
    }

    @Test
    fun card_match_differentRankAndSuit_scoresZero() {
        val a = Card("♥", "3")
        val b = Card("♠", "9")
        assertEquals(0, a.match(arrayOf(b)))
    }

    @Test
    fun card_match_onlyHandlesSingleOpponent() {
        val a = Card("♥", "A")
        assertEquals(0, a.match(arrayOf(Card("♥", "A"), Card("♠", "A"))))
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
        val game = CardMatchingGame(12)
        assertEquals(12, game.cardCount)
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
    fun matchingPair_scoresBonus_andMarksBothCardsMatched() {
        val game = CardMatchingGame(12)
        val pair = findMatchingPair(game.cards)
        assumeTrue("12 张牌中必定存在同花色的一对", pair != null)
        val (first, second) = pair!!

        game.chooseCardAtIndex(first)
        game.chooseCardAtIndex(second)

        val baseScore = if (game.cards[first].rankSymbol == game.cards[second].rankSymbol) 4 else 1
        assertEquals(baseScore * game.MATCH_BONUS - 2 * game.COST_TO_CHOOSE, game.score)
        assertTrue(game.cards[first].isMatched)
        assertTrue(game.cards[second].isMatched)
        assertEquals(2, game.matchedCount)
    }

    @Test
    fun mismatchedPair_appliesPenalty_andClearsEarlierSelection() {
        val game = CardMatchingGame(12)
        val pair = findMismatchingPair(game.cards)
        assumeTrue("随机牌面中通常存在无法配对的两张牌", pair != null)
        val (first, second) = pair!!

        game.chooseCardAtIndex(first)
        game.chooseCardAtIndex(second)

        assertEquals(-(game.MISMATCH_PENALTY + 2 * game.COST_TO_CHOOSE), game.score)
        assertFalse(game.cards[first].isChosen)
        assertTrue(game.cards[second].isChosen)
        assertEquals(0, game.matchedCount)
    }

    @Test
    fun choosingMatchedCard_isIgnored() {
        val game = CardMatchingGame(12)
        val pair = findMatchingPair(game.cards)
        assumeTrue("12 张牌中必定存在同花色的一对", pair != null)
        val (first, second) = pair!!

        game.chooseCardAtIndex(first)
        game.chooseCardAtIndex(second)
        val scoreAfterMatch = game.score

        game.chooseCardAtIndex(first)

        assertEquals(scoreAfterMatch, game.score)
        assertTrue(game.cards[first].isMatched)
    }

    // ---------- helpers ----------

    private fun findMatchingPair(cards: List<Card>): Pair<Int, Int>? {
        for (i in cards.indices) {
            for (j in i + 1 until cards.size) {
                if (cards[i].match(arrayOf(cards[j])) > 0) return i to j
            }
        }
        return null
    }

    private fun findMismatchingPair(cards: List<Card>): Pair<Int, Int>? {
        for (i in cards.indices) {
            for (j in i + 1 until cards.size) {
                if (cards[i].match(arrayOf(cards[j])) == 0) return i to j
            }
        }
        return null
    }
}
