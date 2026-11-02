package cn.edu.sicnu.myapplication4

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import cn.edu.sicnu.myapplication4.data.Gender
import cn.edu.sicnu.myapplication4.data.PlayerRepository
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.espresso.action.ViewActions.clearText
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.action.ViewActions.scrollTo

@RunWith(AndroidJUnit4::class)
class EditProfileActivityTest {

    @Before
    fun setUp() {
        PlayerRepository.reset()
    }

    @Test
    fun saveProfile_updatesInfoAndReturnsToMain() {
        ActivityScenario.launch(MainActivity::class.java)

        onView(withId(R.id.btnEdit)).perform(click())

        onView(withId(R.id.editName)).perform(replaceText("测试玩家"), closeSoftKeyboard())
        onView(withId(R.id.radioFemale)).perform(click())
        onView(withId(R.id.editPhone)).perform(replaceText("13911112222"), closeSoftKeyboard())
        onView(withId(R.id.btnSave)).perform(scrollTo(), click())

        onView(withId(R.id.mainName)).check(matches(withText("测试玩家")))
        onView(withId(R.id.mainGender)).check(matches(withText("女")))
        onView(withId(R.id.mainPhone)).check(matches(withText("13911112222")))

        assertEquals(Gender.FEMALE, PlayerRepository.player.gender)
        assertEquals("13911112222", PlayerRepository.player.phone)
    }

    @Test
    fun emptyName_showsValidationError() {
        ActivityScenario.launch(EditProfileActivity::class.java)

        onView(withId(R.id.editName)).perform(clearText(), closeSoftKeyboard())
        onView(withId(R.id.btnSave)).perform(scrollTo(), click())

        onView(withText("姓名不能为空")).check(matches(isDisplayed()))
        onView(withId(R.id.editRoot)).check(matches(isDisplayed()))
    }

    @Test
    fun invalidPhone_showsValidationError() {
        ActivityScenario.launch(EditProfileActivity::class.java)

        onView(withId(R.id.editPhone)).perform(replaceText("12345"), closeSoftKeyboard())
        onView(withId(R.id.btnSave)).perform(scrollTo(), click())

        onView(withText("请输入 11 位有效手机号")).check(matches(isDisplayed()))
    }
}
