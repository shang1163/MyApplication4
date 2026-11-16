package cn.edu.sicnu.myapplication4.ui

import cn.edu.sicnu.myapplication4.R

/** 第 3 个 Fragment：三张牌翻牌游戏。 */
class ThreeCardGameFragment : CardGameFragment() {

    override val cardCount: Int = 3

    override val historyFileName: String = "game_three_cards.dat"

    override val titleRes: Int = R.string.game_title_three

    override val subtitleRes: Int = R.string.game_subtitle_three
}
