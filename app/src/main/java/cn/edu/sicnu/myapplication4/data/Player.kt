package cn.edu.sicnu.myapplication4.data

/** 玩家性别。 */
enum class Gender {
    MALE,
    FEMALE
}

/** 玩家基本信息。 */
data class Player(
    val name: String,
    val gender: Gender,
    val phone: String,
    val highScore: Int
)

/**
 * 玩家数据仓库。
 * 按实验要求，修改后的资料只保存在内存中，不写入文件。
 */
object PlayerRepository {

    val DEFAULT_PLAYER = Player(
        name = "玩家一号",
        gender = Gender.MALE,
        phone = "13800000000",
        highScore = 0
    )

    var player: Player = DEFAULT_PLAYER
        private set

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
