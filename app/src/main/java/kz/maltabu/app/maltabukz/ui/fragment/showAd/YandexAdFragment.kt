package kz.maltabu.app.maltabukz.ui.fragment.showAd

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.yandex.mobile.ads.AdRequest
import com.yandex.mobile.ads.AdRequestError
import com.yandex.mobile.ads.nativeads.*
import com.yandex.mobile.ads.nativeads.NativeAdLoader.OnImageAdLoadListener
import com.yandex.mobile.ads.nativeads.template.NativeBannerView
import kz.maltabu.app.maltabukz.R

class YandexAdFragment : Fragment() {
    private var mNativeAdLoader: NativeAdLoader? = null
    private var mNativeBannerView: NativeBannerView? = null

    companion object {
        fun newInstance() =
            YandexAdFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_image_ad, null)
        mNativeBannerView = view.findViewById(R.id.native_template)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createNativeAdLoader()
        loadAd()
    }

    private fun loadAd() {
        mNativeAdLoader!!.loadAd(AdRequest.builder().build())
    }

    private fun createNativeAdLoader() {
        val adLoaderConfiguration =
            NativeAdLoaderConfiguration.Builder("R-M-441970-1", false).setImageSizes(
                NativeAdLoaderConfiguration.NATIVE_IMAGE_SIZE_MEDIUM
            ).build()
        mNativeAdLoader = NativeAdLoader(activity!!, adLoaderConfiguration)
        mNativeAdLoader!!.setNativeAdLoadListener(mNativeAdLoadListener)
    }

    private val mNativeAdLoadListener: OnImageAdLoadListener = object : OnImageAdLoadListener {
        override fun onAppInstallAdLoaded(nativeAppInstallAd: NativeAppInstallAd) {
            nativeAppInstallAd.loadImages()
            mNativeBannerView!!.setAd(nativeAppInstallAd)
        }

        override fun onContentAdLoaded(nativeContentAd: NativeContentAd) {
            nativeContentAd.loadImages()
            mNativeBannerView!!.setAd(nativeContentAd)
        }

        override fun onImageAdLoaded(nativeImageAd: NativeImageAd) {
            nativeImageAd.loadImages()
            mNativeBannerView!!.setAd(nativeImageAd)
        }

        override fun onAdFailedToLoad(error: AdRequestError) {
            Log.d("SAMPLE_TAG", error.description)
        }
    }
}