package cn.edu.sicnu.myapplication4.game

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * 翻牌游戏模型单元测试：覆盖 [Card] 两张/三张牌匹配规则、[Deck] 抽牌逻辑与
 * [CardMatchingGame] 选牌计分、判定、reset 重置。
 */
class CardMatchingGameTest {

    // ---------- Card ----------

    @Test
    fun card_twoSameRank_scoresSix() {
        val a = Card("♥", "A")
        val b = Card("♠", "A")
        assertEquals(6, a.match(arrayOf(b)))
    }

    @Test
    fun card_twoSameSuit_scoresThree() {
        val a = Card("♦", "3")
        val b = Card("♦", "K")
        assertEquals(3, a.match(arrayOf(b)))
    }

    @Test
    fun card_twoMixedCards_scoresZero() {
        val a = Card("♥", "3")
        val b = Card("♠", "9")
        assertEquals(0, a.match(arrayOf(b)))
    }

    @Test
    fun card_threeSameRank_scoresSix() {
        val a = Card("♥", "A")
        val b = Card("♠", "A")
        val c = Card("♦", "A")
        assertEquals(6, a.match(arrayOf(b, c)))
    }

    @Test
    fun card_threeSameSuit_scoresThree() {
        val a = Card("♦", "3")
        val b = Card("♦", "9")
        val c = Card("♦", "K")
        assertEquals(3, a.match(arrayOf(b, c)))
    }

    @Test
    fun card_threeMixedCards_scoresZero() {
        val a = Card("♥", "3")
        val b = Card("♠", "9")
        val c = Card("♦", "K")
        assertEquals(0, a.match(arrayOf(b, c)))
    }

    @Test
    fun card_singleCard_scoresZero() {
        assertEquals(0, Card("♥", "A").match(emptyArray()))
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
        val game = CardMatchingGame(3)
        assertEquals(3, game.cardCount)
        assertEquals(0, game.score)
        assertTrue(game.cards.none { it.isChosen || it.isMatched })
        assertFalse(game.isFinished)
        assertFalse(game.isWin)
    }

    @Test(expected = IllegalArgumentException::class)
    fun game_rejectsSingleCard() {
        CardMatchingGame(1)
    }

    @Test(expected = IllegalArgumentException::class)
    fun game_rejectsMoreThanFullDeck() {
        CardMatchingGame(53)
    }

    @Test
    fun choosingCard_costsOnePointAndFlipsIt() {
        val game = CardMatchingGame(2)
        game.chooseCardAtIndex(0)

        assertEquals(-game.COST_TO_CHOOSE, game.score)
        assertTrue(game.cards[0].isChosen)
    }

    @Test
    fun choosingSameCardTwice_unselectsItWithoutExtraCost() {
        val game = CardMatchingGame(2)
        game.chooseCardAtIndex(0)
        game.chooseCardAtIndex(0)

        assertFalse(game.cards[0].isChosen)
        assertEquals(-game.COST_TO_CHOOSE, game.score)
    }

    @Test
    fun twoCardGame_finishesAfterBothCardsChosen_andScoresByMatchRule() {
        val game = CardMatchingGame(2)
        val cards = game.cards
        val baseScore = cards[0].match(arrayOf(cards[1]))

        game.chooseCardAtIndex(0)
        game.chooseCardAtIndex(1)

        val expected = if (baseScore > 0) {
            baseScore * game.MATCH_BONUS - 2 * game.COST_TO_CHOOSE
        } else {
            -game.MISMATCH_PENALTY - 2 * game.COST_TO_CHOOSE
        }
        assertEquals(expected, game.score)
        assertTrue(game.isFinished)
        assertEquals(baseScore > 0, game.isWin)
        assertEquals(if (baseScore > 0) 2 else 0, game.matchedCount)
    }

    @Test
    fun threeCardGame_finishesAfterThreeCardsChosen() {
        val game = CardMatchingGame(3)
        game.chooseCardAtIndex(0)
        game.chooseCardAtIndex(1)
        assertFalse(game.isFinished)

        game.chooseCardAtIndex(2)
        assertTrue(game.isFinished)
        assertTrue(game.matchedCount == 0 || game.matchedCount == 3)
    }

    @Test
    fun choosingAfterFinished_isIgnored() {
        val game = CardMatchingGame(2)
        game.chooseCardAtIndex(0)
        game.chooseCardAtIndex(1)
        val scoreAfterFinish = game.score

        game.chooseCardAtIndex(0)

        assertEquals(scoreAfterFinish, game.score)
        assertTrue(game.isFinished)
    }

    @Test
    fun reset_restoresCardsAndClearsScore() {
        val game = CardMatchingGame(3)
        game.chooseCardAtIndex(0)
        game.chooseCardAtIndex(1)
        game.chooseCardAtIndex(2)
        assertTrue(game.isFinished)

        game.reset()

        assertEquals(3, game.cardCount)
        assertEquals(0, game.score)
        assertEquals(0, game.matchedCount)
        assertTrue(game.cards.none { it.isChosen || it.isMatched })
        assertFalse(game.isFinished)
        assertFalse(game.isWin)
    }
}
