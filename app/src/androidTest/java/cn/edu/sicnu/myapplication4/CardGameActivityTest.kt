package cn.edu.sicnu.myapplication4

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withTagValue
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.espresso.action.ViewActions.click
import androidx.test.ext.junit.runners.AndroidJUnit4
import cn.edu.sicnu.myapplication4.data.PlayerRepository
import cn.edu.sicnu.myapplication4.game.CardGameActivity
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.startsWith
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CardGameActivityTest {

    @Before
    fun setUp() {
        PlayerRepository.reset()
    }

    private fun card(index: Int) = onView(withTagValue(equalTo<Any>("card_$index")))

    @Test
    fun gameStarts_withAllCardsFaceDownAndZeroStats() {
        ActivityScenario.launch(CardGameActivity::class.java)

        onView(withId(R.id.statScore)).check(matches(withText("得分 0")))
        onView(withId(R.id.statPairs)).check(matches(withText("配对 0/12")))
        onView(withId(R.id.statBest)).check(matches(withText("最高分 0")))
        card(0).check(matches(withContentDescription("牌1，背面")))
        card(11).check(matches(withContentDescription("牌12，背面")))
    }

    @Test
    fun tappingCard_flipsIt_andTappingAgainCancelsSelection() {
        ActivityScenario.launch(CardGameActivity::class.java)

        card(0).perform(click())
        card(0).check(matches(withContentDescription(startsWith("牌1，已翻开"))))
        onView(withId(R.id.statScore)).check(matches(withText("得分 -1")))

        card(0).perform(click())
        card(0).check(matches(withContentDescription("牌1，背面")))
        onView(withId(R.id.statScore)).check(matches(withText("得分 -1")))
    }

    @Test
    fun restart_resetsBoardAndStats() {
        ActivityScenario.launch(CardGameActivity::class.java)

        card(0).perform(click())
        card(1).perform(click())

        onView(withId(R.id.btnRestart)).perform(click())

        onView(withId(R.id.statScore)).check(matches(withText("得分 0")))
        onView(withId(R.id.statPairs)).check(matches(withText("配对 0/12")))
        card(0).check(matches(withContentDescription("牌1，背面")))
        card(1).check(matches(withContentDescription("牌2，背面")))
    }

    @Test
    fun newHighScore_isShownInStats() {
        PlayerRepository.updateHighScore(20)

        ActivityScenario.launch(CardGameActivity::class.java)

        onView(withId(R.id.statBest)).check(matches(withText("最高分 20")))
    }
}
