package kz.maltabu.app.maltabukz.ui.fragment

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_news_webview.*
import kz.maltabu.app.maltabukz.BuildConfig
import kz.maltabu.app.maltabukz.R


class NewsWebView(private val newsId:Int) : Fragment() {


    companion object {
        fun newInstance(newsId:Int) = NewsWebView(newsId)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_news_webview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        web_view.settings.javaScriptEnabled = true
        web_view.settings.pluginState = WebSettings.PluginState.ON
        web_view.webChromeClient = MyChrome()
        val url = "https://maltabu.kz/kk/news/s-uir-ayynda-k-ktemgi-egis-zh-mystaryna-138-my-tonna-dizel-otyny-b-lindi"
        web_view.loadUrl(url)
    }

    private fun setListeners() {
        close_web_view.setOnClickListener {
            activity!!.onBackPressed()
        }
    }

    private inner class MyChrome internal constructor() : WebChromeClient() {

        private var mCustomView: View? = null
        private var mCustomViewCallback: CustomViewCallback? = null
        private var mOriginalOrientation: Int = 0
        private var mOriginalSystemUiVisibility: Int = 0

        override fun getDefaultVideoPoster(): Bitmap? {
            return if (mCustomView == null) {
                null
            } else BitmapFactory.decodeResource(activity!!.applicationContext.resources, 2130837573)
        }

//        override fun onProgressChanged(view: WebView?, progress: Int) {
//            super.onProgressChanged(view, progress)
//            if(progress < 100 && progress_hor.visibility == View.GONE){
//                progress_hor.visibility = View.VISIBLE
//            }
//            progress_hor.progress = progress
//            if(progress == 100) {
//                progress_hor.visibility = View.GONE
//            }
//        }

        override fun onHideCustomView() {
            (activity!!.window.decorView as FrameLayout).removeView(this.mCustomView)
            this.mCustomView = null
            activity!!.window.decorView.systemUiVisibility = this.mOriginalSystemUiVisibility
            activity!!.requestedOrientation = this.mOriginalOrientation
            this.mCustomViewCallback!!.onCustomViewHidden()
            this.mCustomViewCallback = null
        }

        override fun onShowCustomView(paramView: View, paramCustomViewCallback: CustomViewCallback) {
            if (this.mCustomView != null) {
                onHideCustomView()
                return
            }
            this.mCustomView = paramView
            this.mOriginalSystemUiVisibility = activity!!.window.decorView.systemUiVisibility
            this.mOriginalOrientation = activity!!.requestedOrientation
            this.mCustomViewCallback = paramCustomViewCallback
            (activity!!.window.decorView as FrameLayout).addView(this.mCustomView, FrameLayout.LayoutParams(-1, -1))
            activity!!.window.decorView.systemUiVisibility = 3846
        }
    }
}
