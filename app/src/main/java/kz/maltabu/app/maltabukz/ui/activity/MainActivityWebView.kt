package kz.maltabu.app.maltabukz.ui.activity

import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.webkit.*
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import io.paperdb.Paper
import kotlinx.android.synthetic.main.activity_main_web_view.*
import kotlinx.android.synthetic.main.dialog_update.*
import kz.maltabu.app.maltabukz.BuildConfig
import kz.maltabu.app.maltabukz.R
import kz.maltabu.app.maltabukz.utils.web.VersionChecker
import org.jsoup.Jsoup


class MainActivityWebView : AppCompatActivity() {
    private val REQUEST_SELECT_FILE = 100
    var uploadMessage: ValueCallback<Array<Uri>>? = null
    private lateinit var customDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_web_view)
        Paper.init(this)
        customDialog = Dialog(this)
        setViewViewSettings()
        getTokenFirebase()
        try{
            checkVersion()
        } catch (e:Exception){
            Log.d("TAGg", e.message)
        }
    }

    private fun getTokenFirebase() {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener(object : OnCompleteListener<String?> {
                override fun onComplete(@NonNull task: Task<String?>) {
                    if (!task.isSuccessful) {
                        return
                    }
                    Log.d("TAGg", task.result!!)
                }
            })
    }

    private fun setViewViewSettings() {
        main_web_view.settings.javaScriptEnabled = true
        main_web_view.settings.domStorageEnabled = true
        main_web_view.settings.pluginState=WebSettings.PluginState.ON
        main_web_view.webViewClient=WebViewController(this)
        main_web_view.webChromeClient=CustomWebChromeClient(this)
        main_web_view.loadUrl("${BuildConfig.BASE_URL}")
    }

    class WebViewController(val context: Context): WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String?): Boolean {
            if(url!!.startsWith("tel:")){
                val intent = Intent(
                    Intent.ACTION_DIAL,
                    Uri.parse(url)
                )
                context.startActivity(intent)
                view.reload()
                return true
            } else {
                return if (url == null || url.startsWith("http://") || url.startsWith("https://"))
                    false
                else try {
                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(url)
                    )
                    view.context.startActivity(intent)
                    true
                } catch (e: java.lang.Exception) {
                    true
                }
            }
        }
    }

    class CustomWebChromeClient(private val myActivity: MainActivityWebView) : WebChromeClient(){

        override fun onShowFileChooser(
            webView: WebView?,
            filePathCallback:  ValueCallback<Array<Uri>>,
            fileChooserParams: FileChooserParams
        ): Boolean {

            if (myActivity.uploadMessage != null) {
                myActivity.uploadMessage!!.onReceiveValue(null)
                myActivity.uploadMessage = null
            }
            myActivity.uploadMessage = filePathCallback
            val intent: Intent = fileChooserParams.createIntent()
            try {
                myActivity.startActivityForResult(intent, 100)
            } catch (e: ActivityNotFoundException) {
                myActivity.uploadMessage = null
                Toast.makeText(myActivity, "Cannot open file chooser", Toast.LENGTH_LONG).show()
                return false
            }
            return true
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (event!!.action === KeyEvent.ACTION_DOWN) {
            when (keyCode) {
                KeyEvent.KEYCODE_BACK -> {
                    if (main_web_view.canGoBack()) {
                        main_web_view.goBack()
                    } else {
                        finish()
                    }
                    return true
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_SELECT_FILE) {
            try {
                if (uploadMessage == null) return
                uploadMessage!!.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data))
                uploadMessage = null
            } catch (e:Exception){}
        }
    }

    private fun checkVersion(){
        val checker = VersionChecker()
        checker.getData().observe(this, Observer {
            try {
                val doc = Jsoup.parse(it)
                val span = doc.select("span").first()
                compareVersions(span.text())
            } catch (e: Exception){
                Log.d("TAGg", e.message)
            }
        })
        checker.getVersion()
    }

    private fun compareVersions(versionFromPlayMarket: String) {
        val versionName = BuildConfig.VERSION_NAME
        if(versionFromPlayMarket!=versionName){
            showUpdateDialog()
        }
    }

    private fun showUpdateDialog() {
        customDialog.setContentView(R.layout.dialog_update)
        customDialog.update_button.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(resources.getString(R.string.play_market_link))))
            customDialog.dismiss()
        }
        customDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        customDialog.show()
    }

    private fun appInstalledOrNot(uri: String): Boolean {
        val pm: PackageManager = packageManager
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES)
            return true
        } catch (e: PackageManager.NameNotFoundException) {
        }
        return false
    }
}
