package cn.edu.sicnu.myapplication4.game

import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.edu.sicnu.myapplication4.R
import cn.edu.sicnu.myapplication4.data.PlayerRepository
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * 翻牌游戏控制器（MVC 中的 Controller）。
 * 接收 RecyclerView 的点击回调，驱动 [CardMatchingGame]，并把最新状态同步到 [PlayingCardAdapter]。
 * 竖屏使用 12 张牌（4 列），横屏使用 24 张牌（8 列）。
 */
class CardGameActivity : AppCompatActivity() {

    companion object {
        private const val PORTRAIT_CARD_COUNT = 12
        private const val LANDSCAPE_CARD_COUNT = 24
        private const val PORTRAIT_COLUMNS = 4
        private const val LANDSCAPE_COLUMNS = 8

        /** 全部配对成功后，延时弹出结果对话框。 */
        private const val WIN_DIALOG_DELAY_MS = 650L
    }

    private val handler = Handler(Looper.getMainLooper())

    private lateinit var game: CardMatchingGame
    private lateinit var adapter: PlayingCardAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var statScore: TextView
    private lateinit var statPairs: TextView
    private lateinit var statBest: TextView

    private var winDialog: AlertDialog? = null
    private var roundIsNewRecord = false
    private var roundFinished = false

    private val isLandscape: Boolean
        get() = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    private fun cardCount(): Int =
        if (isLandscape) LANDSCAPE_CARD_COUNT else PORTRAIT_CARD_COUNT

    private fun columnCount(): Int =
        if (isLandscape) LANDSCAPE_COLUMNS else PORTRAIT_COLUMNS

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_card_game)
        applySystemBarInsets()

        val toolbar = findViewById<MaterialToolbar>(R.id.gameToolbar)
        toolbar.setNavigationOnClickListener { finish() }

        statScore = findViewById(R.id.statScore)
        statPairs = findViewById(R.id.statPairs)
        statBest = findViewById(R.id.statBest)

        recyclerView = findViewById(R.id.cardRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, columnCount())
        adapter = PlayingCardAdapter { index -> onCardClicked(index) }
        recyclerView.adapter = adapter

        findViewById<MaterialButton>(R.id.btnRestart).setOnClickListener {
            startNewGame()
        }

        game = CardMatchingGame(cardCount())
        startNewGame()
    }

    /** 重新开始一局游戏。 */
    private fun startNewGame() {
        handler.removeCallbacksAndMessages(null)
        winDialog?.dismiss()
        winDialog = null
        roundIsNewRecord = false
        roundFinished = false
        game.reset()
        refreshBoardAndStats()
    }

    private fun onCardClicked(index: Int) {
        game.chooseCardAtIndex(index)

        if (PlayerRepository.updateHighScore(game.score)) {
            roundIsNewRecord = true
        }
        refreshBoardAndStats()

        if (game.isFinished && !roundFinished) {
            roundFinished = true
            handler.postDelayed({ showWinDialog() }, WIN_DIALOG_DELAY_MS)
        }
    }

    /** 将模型状态同步到 View。 */
    private fun refreshBoardAndStats() {
        adapter.submitCards(game.cards)
        statScore.text = getString(R.string.stat_score, game.score)
        statPairs.text = getString(R.string.stat_pairs, game.matchedCount, game.cardCount)
        statBest.text = getString(R.string.stat_best, PlayerRepository.player.highScore)
    }

    private fun showWinDialog() {
        if (isFinishing || isDestroyed) return
        val resultText = getString(R.string.dialog_win_message, game.score)
        val recordText = if (roundIsNewRecord) {
            getString(R.string.dialog_win_record, PlayerRepository.player.highScore)
        } else {
            getString(R.string.dialog_win_normal, PlayerRepository.player.highScore)
        }

        winDialog = MaterialAlertDialogBuilder(this)
            .setTitle(R.string.dialog_win_title)
            .setMessage("$resultText\n$recordText")
            .setCancelable(false)
            .setPositiveButton(R.string.btn_play_again) { _, _ -> startNewGame() }
            .setNegativeButton(R.string.btn_back_home) { _, _ -> finish() }
            .show()
    }

    private fun applySystemBarInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.gameRoot)) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }
    }

    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        winDialog?.dismiss()
        winDialog = null
        super.onDestroy()
    }
}
