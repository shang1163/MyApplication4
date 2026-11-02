package cn.edu.sicnu.myapplication4

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import cn.edu.sicnu.myapplication4.data.PlayerRepository
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @Before
    fun setUp() {
        PlayerRepository.reset()
    }

    @Test
    fun mainScreen_showsCurrentPlayerInfo() {
        ActivityScenario.launch(MainActivity::class.java)

        onView(withId(R.id.mainName)).check(matches(withText("玩家一号")))
        onView(withId(R.id.mainGender)).check(matches(withText("男")))
        onView(withId(R.id.mainPhone)).check(matches(withText("13800000000")))
        onView(withId(R.id.mainHighScore)).check(matches(withText("0 分")))
    }

    @Test
    fun mainScreen_showsUpdatedHighScore() {
        PlayerRepository.updateHighScore(30)

        ActivityScenario.launch(MainActivity::class.java)

        onView(withId(R.id.mainHighScore)).check(matches(withText("30 分")))
        onView(withId(R.id.headerHighScore)).check(matches(withText("最高分 30 分")))
    }
}
