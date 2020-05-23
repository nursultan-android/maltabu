package kz.maltabu.app.maltabukz.service

import android.R
import android.app.IntentService
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import io.paperdb.Paper
import io.reactivex.disposables.CompositeDisposable
import kz.maltabu.app.maltabukz.model.NewAdBody
import java.io.File

class SendAdIntentService(name: String?) : IntentService(name) {
    private val CHANNEL_ID = "uploadNotificationChannel"
    private var wakeLock: PowerManager.WakeLock? = null
    private val disposable = CompositeDisposable()

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(CHANNEL_ID, "Foreground Service Channel", NotificationManager.IMPORTANCE_DEFAULT)
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    override fun onHandleIntent(intent: Intent?) {
        val inputService = intent!!.getStringExtra("inputService")
        Log.d("TAGg", "started")
        createNotificationChannel()
        val notification = NotificationCompat.Builder(this, CHANNEL_ID).setContentTitle("sync").setSmallIcon(R.drawable.stat_sys_download).build()
        startForeground(1, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("TAGg", "destroyed")
        wakeLock!!.release()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            stopForeground(true)
        }
    }

    override fun onCreate() {
        super.onCreate()
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "eSpartaApp:Wakelock")
        wakeLock!!.acquire()
    }

    override fun startForegroundService(service: Intent?): ComponentName? {
        return super.startForegroundService(service)
    }

    private fun sendNewAaImages(){
        val images = Paper.book().read<List<File>>("imgFiles")

    }
}