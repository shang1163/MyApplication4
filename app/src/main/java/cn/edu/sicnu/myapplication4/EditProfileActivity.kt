package cn.edu.sicnu.myapplication4

import android.os.Bundle
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import cn.edu.sicnu.myapplication4.data.Gender
import cn.edu.sicnu.myapplication4.data.PlayerRepository
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

/**
 * 信息修改界面。
 * 校验通过后更新内存中的玩家资料并返回主界面。
 */
class EditProfileActivity : AppCompatActivity() {

    companion object {
        private val PHONE_PATTERN = Regex("^1[3-9]\\d{9}$")
    }

    private lateinit var nameLayout: TextInputLayout
    private lateinit var phoneLayout: TextInputLayout
    private lateinit var editName: TextInputEditText
    private lateinit var editPhone: TextInputEditText
    private lateinit var radioMale: RadioButton
    private lateinit var radioFemale: RadioButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_profile)
        applySystemBarInsets()

        findViewById<MaterialToolbar>(R.id.editToolbar).setNavigationOnClickListener { finish() }

        nameLayout = findViewById(R.id.nameLayout)
        phoneLayout = findViewById(R.id.phoneLayout)
        editName = findViewById(R.id.editName)
        editPhone = findViewById(R.id.editPhone)
        radioMale = findViewById(R.id.radioMale)
        radioFemale = findViewById(R.id.radioFemale)

        fillCurrentProfile()

        findViewById<MaterialButton>(R.id.btnSave).setOnClickListener { saveProfile() }
    }

    private fun fillCurrentProfile() {
        val player = PlayerRepository.player
        editName.setText(player.name)
        editPhone.setText(player.phone)
        when (player.gender) {
            Gender.MALE -> radioMale.isChecked = true
            Gender.FEMALE -> radioFemale.isChecked = true
        }
    }

    private fun saveProfile() {
        nameLayout.error = null
        phoneLayout.error = null

        val name = editName.text?.toString()?.trim().orEmpty()
        val phone = editPhone.text?.toString()?.trim().orEmpty()

        if (name.isEmpty()) {
            nameLayout.error = getString(R.string.error_name_empty)
            return
        }
        if (!PHONE_PATTERN.matches(phone)) {
            phoneLayout.error = getString(R.string.error_phone_invalid)
            return
        }

        val gender = if (radioFemale.isChecked) Gender.FEMALE else Gender.MALE
        PlayerRepository.updateProfile(name = name, gender = gender, phone = phone)

        Toast.makeText(this, R.string.profile_saved, Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun applySystemBarInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.editRoot)) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }
    }
}
