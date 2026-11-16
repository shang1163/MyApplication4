package cn.edu.sicnu.myapplication4

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import cn.edu.sicnu.myapplication4.data.ObjectFileStore
import cn.edu.sicnu.myapplication4.data.PlayerRepository
import cn.edu.sicnu.myapplication4.ui.ProfileFragment
import cn.edu.sicnu.myapplication4.ui.ThreeCardGameFragment
import cn.edu.sicnu.myapplication4.ui.TwoCardGameFragment
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * 主界面：承载个人信息、两张牌、三张牌三个 Fragment。
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        applySystemBarInsets()

        if (!playerRestored) {
            // 进程启动时从文件中反序列化上次保存的玩家信息，旋转屏幕时不再重复读取
            PlayerRepository.restore(
                ObjectFileStore.read(this, PlayerRepository.FILE_NAME, PlayerRepository.DEFAULT_PLAYER)
            )
            playerRestored = true
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

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.setOnItemSelectedListener { item ->
            showFragment(
                when (item.itemId) {
                    R.id.nav_profile -> TAG_PROFILE
                    R.id.nav_game_two -> TAG_GAME_TWO
                    else -> TAG_GAME_THREE
                }
            )
            true
        }

        if (savedInstanceState == null) {
            bottomNav.selectedItemId = R.id.nav_profile
        }
    }

    /** 三个 Fragment 只创建一次，用 hide/show 切换，避免来回切换丢失当前状态。 */
    private fun showFragment(tag: String) {
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        fragmentManager.fragments.forEach { fragment ->
            if (fragment.tag != tag && !fragment.isHidden) transaction.hide(fragment)
        }
        val existing = fragmentManager.findFragmentByTag(tag)
        if (existing == null) {
            transaction.add(R.id.fragmentContainer, createFragment(tag), tag)
        } else {
            transaction.show(existing)
        }
        transaction.commit()
    }

    private fun createFragment(tag: String): Fragment = when (tag) {
        TAG_PROFILE -> ProfileFragment()
        TAG_GAME_TWO -> TwoCardGameFragment()
        else -> ThreeCardGameFragment()
    }

    /** 菜单中的「关于」对话框。 */
    private fun showAboutDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.about_title)
            .setMessage(R.string.about_message)
            .setPositiveButton(R.string.btn_ok, null)
            .show()
    }

    private fun applySystemBarInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }
    }

    companion object {
        private const val TAG_PROFILE = "fragment_profile"
        private const val TAG_GAME_TWO = "fragment_game_two"
        private const val TAG_GAME_THREE = "fragment_game_three"

        /** 标记本次进程是否已经从文件中恢复过玩家信息。 */
        @Volatile
        private var playerRestored = false
    }
}
