package kz.maltabu.app.maltabukz.utils.yandexAds

import android.content.Context
import android.graphics.drawable.ColorDrawable
import com.google.android.ads.nativetemplates.NativeTemplateStyle
import com.google.android.ads.nativetemplates.TemplateView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.formats.NativeAdOptions
import com.google.android.gms.ads.formats.UnifiedNativeAd
import kz.maltabu.app.maltabukz.R

class GoogleAdmobHelper(private val context: Context, private val adId: String, private val tamplateAd: TemplateView) {

    private val adLoader = AdLoader.Builder(context, adId)
        .forUnifiedNativeAd { ad : UnifiedNativeAd ->
            val styles = NativeTemplateStyle.Builder().withMainBackgroundColor(ColorDrawable(context.resources.getColor(R.color.MaltabuYellow))).build()
            tamplateAd.setNativeAd(ad)
            tamplateAd.setStyles(styles)
        }
        .withAdListener(object : AdListener() {
            override fun onAdFailedToLoad(errorCode: Int) {
            }
        })
        .withNativeAdOptions(NativeAdOptions.Builder().build())
        .build()

    fun loadAd(){
        adLoader.loadAd(AdRequest.Builder().build())
    }
}

