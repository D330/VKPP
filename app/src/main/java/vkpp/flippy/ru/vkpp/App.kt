package vkpp.flippy.ru.vkpp

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.vk.sdk.VKSdk

class App : Application() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = this
        VKSdk.initialize(this)
    }
}