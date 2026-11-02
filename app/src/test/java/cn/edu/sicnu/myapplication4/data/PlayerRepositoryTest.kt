package cn.edu.sicnu.myapplication4.data

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class PlayerRepositoryTest {

    @Before
    fun setUp() {
        PlayerRepository.reset()
    }

    @After
    fun tearDown() {
        PlayerRepository.reset()
    }

    @Test
    fun defaultPlayer_hasExpectedInfo() {
        val player = PlayerRepository.player

        assertEquals("玩家一号", player.name)
        assertEquals(Gender.MALE, player.gender)
        assertEquals("13800000000", player.phone)
        assertEquals(0, player.highScore)
    }

    @Test
    fun updateProfile_changesBasicInfo_butKeepsHighScore() {
        PlayerRepository.updateHighScore(40)

        PlayerRepository.updateProfile(name = "李小明", gender = Gender.FEMALE, phone = "13911112222")

        val player = PlayerRepository.player
        assertEquals("李小明", player.name)
        assertEquals(Gender.FEMALE, player.gender)
        assertEquals("13911112222", player.phone)
        assertEquals(40, player.highScore)
    }

    @Test
    fun updateHighScore_onlyAcceptsHigherScore() {
        assertTrue(PlayerRepository.updateHighScore(30))
        assertEquals(30, PlayerRepository.player.highScore)

        assertFalse(PlayerRepository.updateHighScore(20))
        assertFalse(PlayerRepository.updateHighScore(30))
        assertEquals(30, PlayerRepository.player.highScore)

        assertTrue(PlayerRepository.updateHighScore(50))
        assertEquals(50, PlayerRepository.player.highScore)
    }

    @Test
    fun reset_restoresDefaultPlayer() {
        PlayerRepository.updateProfile("王五", Gender.FEMALE, "13812345678")
        PlayerRepository.updateHighScore(60)

        PlayerRepository.reset()

        assertEquals(PlayerRepository.DEFAULT_PLAYER, PlayerRepository.player)
    }
}
