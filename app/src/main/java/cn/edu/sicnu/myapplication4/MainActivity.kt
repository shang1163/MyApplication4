package cn.edu.sicnu.myapplication4

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import cn.edu.sicnu.myapplication4.data.Gender
import cn.edu.sicnu.myapplication4.data.PlayerRepository
import cn.edu.sicnu.myapplication4.game.CardGameActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * 主界面：显示当前玩家的基本信息，并提供修改资料与开始游戏的入口。
 */
class MainActivity : AppCompatActivity() {

    private lateinit var avatarInitial: TextView
    private lateinit var headerPlayerName: TextView
    private lateinit var headerHighScore: TextView
    private lateinit var mainName: TextView
    private lateinit var mainGender: TextView
    private lateinit var mainPhone: TextView
    private lateinit var mainHighScore: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        applySystemBarInsets()

        avatarInitial = findViewById(R.id.avatarInitial)
        headerPlayerName = findViewById(R.id.headerPlayerName)
        headerHighScore = findViewById(R.id.headerHighScore)
        mainName = findViewById(R.id.mainName)
        mainGender = findViewById(R.id.mainGender)
        mainPhone = findViewById(R.id.mainPhone)
        mainHighScore = findViewById(R.id.mainHighScore)

        findViewById<MaterialButton>(R.id.btnEdit).setOnClickListener {
            startActivity(Intent(this, EditProfileActivity::class.java))
        }
        findViewById<MaterialButton>(R.id.btnPlay).setOnClickListener {
            startActivity(Intent(this, CardGameActivity::class.java))
        }

        findViewById<MaterialToolbar>(R.id.mainToolbar).setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_about -> {
                    showAboutDialog()
                    true
                }

                else -> false
            }
        }
    }

    /** 菜单中的「关于」对话框。 */
    private fun showAboutDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.about_title)
            .setMessage(R.string.about_message)
            .setPositiveButton(R.string.btn_ok, null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        renderPlayer()
    }

    private fun renderPlayer() {
        val player = PlayerRepository.player

        avatarInitial.text = player.name.take(1)
        headerPlayerName.text = player.name
        headerHighScore.text = getString(R.string.header_high_score, player.highScore)

        mainName.text = player.name
        mainGender.text = when (player.gender) {
            Gender.MALE -> getString(R.string.gender_male)
            Gender.FEMALE -> getString(R.string.gender_female)
        }
        mainPhone.text = player.phone
        mainHighScore.text = getString(R.string.score_value, player.highScore)
    }

    private fun applySystemBarInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }
    }
}
