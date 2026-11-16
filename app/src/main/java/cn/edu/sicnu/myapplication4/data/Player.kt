package cn.edu.sicnu.myapplication4.data

import java.io.Serializable

/** 玩家性别。 */
enum class Gender {
    MALE,
    FEMALE
}

/**
 * 玩家基本信息。
 * 实现 Serializable，配合 [ObjectFileStore] 用对象流保存到内部文件。
 */
data class Player(
    val name: String,
    val gender: Gender,
    val phone: String,
    val highScore: Int
) : Serializable

/**
 * 玩家数据仓库。
 * 内存中保持当前玩家，Fragment 负责把数据序列化写入 / 读取本地文件。
 */
object PlayerRepository {

    /** 个人信息保存的文件名。 */
    const val FILE_NAME = "player.dat"

    val DEFAULT_PLAYER = Player(
        name = "玩家一号",
        gender = Gender.MALE,
        phone = "13800000000",
        highScore = 0
    )

    var player: Player = DEFAULT_PLAYER
        private set

    /** 启动时用文件中反序列化出的玩家信息恢复数据。 */
    fun restore(saved: Player) {
        player = saved
    }

    /** 修改姓名、性别、电话，保留历史最高分。 */
    fun updateProfile(name: String, gender: Gender, phone: String) {
        player = player.copy(name = name, gender = gender, phone = phone)
    }

    /**
     * 刷新最高分。
     * @return 是否创造了新纪录。
     */
    fun updateHighScore(score: Int): Boolean {
        if (score <= player.highScore) return false
        player = player.copy(highScore = score)
        return true
    }

    /** 恢复默认数据，便于测试与重新演示。 */
    fun reset() {
        player = DEFAULT_PLAYER
    }
}
