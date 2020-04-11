package kz.maltabu.app.maltabukz

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.yandex.metrica.YandexMetrica
import com.yandex.metrica.YandexMetricaConfig
import io.paperdb.Paper

class App : Application() {
    companion object {
        const val CHANNEL_ID = "firebase_push"
    }

    override fun onCreate() {
        super.onCreate()
        Paper.init(this)
        val config = YandexMetricaConfig.newConfigBuilder(resources.getString(R.string.yandex_app_id)).build()
        YandexMetrica.activate(applicationContext, config)
        YandexMetrica.enableActivityAutoTracking(this)
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "Firebase Notification Channell", NotificationManager.IMPORTANCE_HIGH)
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}