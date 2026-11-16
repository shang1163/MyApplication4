package cn.edu.sicnu.myapplication4.ui

import cn.edu.sicnu.myapplication4.R

/** 第 2 个 Fragment：两张牌翻牌游戏。 */
class TwoCardGameFragment : CardGameFragment() {

    override val cardCount: Int = 2

    override val historyFileName: String = "game_two_cards.dat"

    override val titleRes: Int = R.string.game_title_two

    override val subtitleRes: Int = R.string.game_subtitle_two
}
