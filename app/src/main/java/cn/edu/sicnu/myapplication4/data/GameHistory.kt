package cn.edu.sicnu.myapplication4.data

import java.io.Serializable

/** 一局翻牌游戏的记录：结束时间、牌面、得分与胜负。 */
data class GameRecord(
    val time: Long,
    val cards: List<String>,
    val score: Int,
    val win: Boolean
) : Serializable

/** 某个翻牌游戏的全部历史记录，整体序列化后保存到文件。 */
data class GameHistory(
    val records: MutableList<GameRecord> = mutableListOf()
) : Serializable {

    val total: Int get() = records.size

    val wins: Int get() = records.count { it.win }
}
