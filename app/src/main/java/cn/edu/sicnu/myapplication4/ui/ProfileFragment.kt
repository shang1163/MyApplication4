package cn.edu.sicnu.myapplication4.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import cn.edu.sicnu.myapplication4.EditProfileActivity
import cn.edu.sicnu.myapplication4.R
import cn.edu.sicnu.myapplication4.data.Gender
import cn.edu.sicnu.myapplication4.data.ObjectFileStore
import cn.edu.sicnu.myapplication4.data.PlayerRepository
import com.google.android.material.button.MaterialButton

/**
 * 第 1 个 Fragment：个人信息。
 * 展示玩家资料与最高分，修改后在 onResume 中序列化保存到本地文件。
 */
class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var avatarInitial: TextView
    private lateinit var headerPlayerName: TextView
    private lateinit var headerHighScore: TextView
    private lateinit var mainName: TextView
    private lateinit var mainGender: TextView
    private lateinit var mainPhone: TextView
    private lateinit var mainHighScore: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        avatarInitial = view.findViewById(R.id.avatarInitial)
        headerPlayerName = view.findViewById(R.id.headerPlayerName)
        headerHighScore = view.findViewById(R.id.headerHighScore)
        mainName = view.findViewById(R.id.mainName)
        mainGender = view.findViewById(R.id.mainGender)
        mainPhone = view.findViewById(R.id.mainPhone)
        mainHighScore = view.findViewById(R.id.mainHighScore)

        view.findViewById<MaterialButton>(R.id.btnEdit).setOnClickListener {
            startActivity(Intent(requireContext(), EditProfileActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        if (view == null) return
        renderPlayer()
        // 每次回到个人信息页都把当前资料序列化写入文件
        ObjectFileStore.write(requireContext(), PlayerRepository.FILE_NAME, PlayerRepository.player)
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
}
