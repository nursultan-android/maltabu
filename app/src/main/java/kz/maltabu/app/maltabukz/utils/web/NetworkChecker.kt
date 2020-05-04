package kz.maltabu.app.maltabukz.utils.web

import android.content.Context
import android.net.ConnectivityManager

class NetworkChecker(private val context: Context) {
    val isNetworkAvailable: Boolean
        get() {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = cm.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }
}