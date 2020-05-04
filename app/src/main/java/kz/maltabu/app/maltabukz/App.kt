package kz.maltabu.app.maltabukz

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.util.Log
import com.google.firebase.crashlytics.internal.model.CrashlyticsReport
import com.yandex.metrica.YandexMetrica
import com.yandex.metrica.YandexMetricaConfig
import io.paperdb.Paper
import kz.maltabu.app.maltabukz.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin


class App : Application() {
    companion object {
        const val CHANNEL_ID = "firebase_push"
    }

    override fun onCreate() {
        super.onCreate()
        Paper.init(this)
        startKoin{
            androidLogger()
            androidContext(this@App)
            modules(appModule)
        }
        try {
            initAdverbService()
        } catch (e:Exception){
            Log.d("TAGg", e.message)
        }
    }

    private fun initAdverbService(){
        val config = YandexMetricaConfig.newConfigBuilder(resources.getString(R.string.yandex_app_id)).build()
        YandexMetrica.activate(applicationContext, config)
        YandexMetrica.enableActivityAutoTracking(this)
        createNotificationChannel()
        com.google.android.gms.ads.MobileAds.initialize(this) {}
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "Firebase Notification Channell", NotificationManager.IMPORTANCE_HIGH)
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}