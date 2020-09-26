package kz.maltabu.app.maltabukz.ui.activity

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.webkit.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main_web_view.*
import kz.maltabu.app.maltabukz.BuildConfig
import kz.maltabu.app.maltabukz.R


class MainActivityWebView : AppCompatActivity() {
    private val REQUEST_SELECT_FILE = 100
    var uploadMessage: ValueCallback<Array<Uri>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_web_view)
        main_web_view.settings.javaScriptEnabled = true
        main_web_view.settings.domStorageEnabled = true
        main_web_view.settings.pluginState=WebSettings.PluginState.ON
        main_web_view.webViewClient=WebViewController(this)
        main_web_view.webChromeClient=CustomWebChromeClient(this)
        main_web_view.loadUrl("${BuildConfig.BASE_URL}/kk")
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
            }
            view.loadUrl(url)
            return true
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
}
