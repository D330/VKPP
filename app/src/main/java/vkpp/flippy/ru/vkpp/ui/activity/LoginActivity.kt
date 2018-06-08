package vkpp.flippy.ru.vkpp.ui.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.vk.sdk.VKScope
import com.vk.sdk.VKSdk
import com.vk.sdk.api.VKError
import com.vk.sdk.VKAccessToken
import com.vk.sdk.VKCallback
import android.content.Intent
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_login.*
import vkpp.flippy.ru.vkpp.App
import vkpp.flippy.ru.vkpp.R
import vkpp.flippy.ru.vkpp.data.Prefs

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (VKSdk.isLoggedIn()) {
            startMainActivity()
        } else {
            setContentView(R.layout.activity_login)
            authBtn.setOnClickListener {
                VKSdk.login(this, VKScope.OFFLINE, VKScope.MESSAGES, VKScope.PHOTOS)
            }
        }
    }

    private fun startMainActivity() {
        startActivity(Intent(this@LoginActivity, JobActivity::class.java))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val tokenCallback = object : VKCallback<VKAccessToken> {
            override fun onResult(res: VKAccessToken) {
                Prefs.getInstance().token = res.accessToken
                startMainActivity()
            }

            override fun onError(error: VKError) {
                Toast.makeText(App.context, "Операция отменена или произошла ошибка", Toast.LENGTH_SHORT).show()
            }
        }

        if (!VKSdk.onActivityResult(requestCode, resultCode, data, tokenCallback)) {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

}
