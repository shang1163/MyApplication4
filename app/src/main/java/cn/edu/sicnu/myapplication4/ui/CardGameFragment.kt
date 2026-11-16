package cn.edu.sicnu.myapplication4.ui

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.edu.sicnu.myapplication4.R
import cn.edu.sicnu.myapplication4.data.GameHistory
import cn.edu.sicnu.myapplication4.data.GameRecord
import cn.edu.sicnu.myapplication4.data.ObjectFileStore
import cn.edu.sicnu.myapplication4.data.PlayerRepository
import cn.edu.sicnu.myapplication4.game.CardMatchingGame
import cn.edu.sicnu.myapplication4.game.PlayingCardAdapter
import com.google.android.material.button.MaterialButton
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 翻牌游戏 Fragment 的公共实现：
 * 复用实验五的牌面绘制 [PlayingCardAdapter] 与配对模型 [CardMatchingGame]，
 * 每局结果写入 [GameHistory] 后序列化保存到本地文件。
 */
abstract class CardGameFragment : Fragment(R.layout.fragment_card_game) {

    /** 牌数：两张牌 / 三张牌 */
    protected abstract val cardCount: Int

    /** 历史记录保存的文件名 */
    protected abstract val historyFileName: String

    /** 游戏标题 */
    protected abstract val titleRes: Int

    /** 游戏规则说明 */
    protected abstract val subtitleRes: Int

    private lateinit var game: CardMatchingGame
    private lateinit var adapter: PlayingCardAdapter
    private lateinit var statScore: TextView
    private lateinit var statPairs: TextView
    private lateinit var statBest: TextView
    private lateinit var resultText: TextView
    private lateinit var historyText: TextView

    private var history = GameHistory()
    private var roundRecorded = false

    private val timeFormat = SimpleDateFormat("MM-dd HH:mm", Locale.CHINA)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<TextView>(R.id.gameTitle).text = getString(titleRes)
        view.findViewById<TextView>(R.id.gameSubtitle).text = getString(subtitleRes)

        statScore = view.findViewById(R.id.statScore)
        statPairs = view.findViewById(R.id.statPairs)
        statBest = view.findViewById(R.id.statBest)
        resultText = view.findViewById(R.id.textResult)
        historyText = view.findViewById(R.id.textHistory)

        adapter = PlayingCardAdapter { index -> onCardClicked(index) }
        view.findViewById<RecyclerView>(R.id.cardRecyclerView).apply {
            layoutManager = GridLayoutManager(requireContext(), cardCount)
            adapter = this@CardGameFragment.adapter
            if (cardCount == 2) {
                // 两张牌时限制牌面宽度，避免单张牌过大
                setPadding(dp(56), dp(12), dp(56), dp(12))
            }
        }

        // 从文件中反序列化历史记录
        history = ObjectFileStore.read(requireContext(), historyFileName, GameHistory())

        view.findViewById<MaterialButton>(R.id.btnRestart).setOnClickListener {
            startNewGame()
        }

        startNewGame()
    }

    private fun startNewGame() {
        game = CardMatchingGame(cardCount)
        roundRecorded = false
        resultText.visibility = View.GONE
        refreshBoardAndStats()
        refreshHistory()
    }

    private fun onCardClicked(index: Int) {
        game.chooseCardAtIndex(index)
        if (game.isFinished && !roundRecorded) {
            roundRecorded = true
            recordRound()
        }
        refreshBoardAndStats()
    }

    /** 记录本局结果：更新最高分、写入历史记录文件。 */
    private fun recordRound() {
        val win = game.isWin
        if (PlayerRepository.updateHighScore(game.score)) {
            ObjectFileStore.write(requireContext(), PlayerRepository.FILE_NAME, PlayerRepository.player)
        }

        history.records.add(
            GameRecord(
                time = System.currentTimeMillis(),
                cards = game.cards.map { it.toString() },
                score = game.score,
                win = win
            )
        )
        ObjectFileStore.write(requireContext(), historyFileName, history)

        resultText.visibility = View.VISIBLE
        resultText.text = getString(
            if (win) R.string.game_result_win else R.string.game_result_lose,
            game.score
        )
        resultText.setTextColor(
            ContextCompat.getColor(requireContext(), if (win) R.color.success else R.color.danger)
        )
        refreshHistory()
    }

    private fun refreshBoardAndStats() {
        adapter.submitCards(game.cards)
        statScore.text = getString(R.string.stat_score, game.score)
        statPairs.text = getString(R.string.stat_pairs, game.matchedCount, game.cardCount)
        statBest.text = getString(R.string.stat_best, PlayerRepository.player.highScore)
    }

    private fun refreshHistory() {
        historyText.text = if (history.records.isEmpty()) {
            getString(R.string.history_empty)
        } else {
            history.records.takeLast(MAX_HISTORY_LINES).reversed().joinToString("\n") { record ->
                getString(
                    R.string.record_line,
                    timeFormat.format(Date(record.time)),
                    record.cards.joinToString(" "),
                    record.score,
                    getString(if (record.win) R.string.record_win else R.string.record_lose)
                )
            }
        }
    }

    private fun dp(value: Int): Int = (resources.displayMetrics.density * value).toInt()

    companion object {
        private const val MAX_HISTORY_LINES = 10
    }
}
