package kz.maltabu.app.maltabukz.ui.fragment.newAd

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.InterstitialAd
import kotlinx.android.synthetic.main.activity_new_ad.*
import kz.maltabu.app.maltabukz.R
import kz.maltabu.app.maltabukz.ui.activity.NewAdActivity

class SuccessFragment : Fragment() {
    private lateinit var interstitialAd: InterstitialAd

    fun setAd(interstitialAd: InterstitialAd){
        this.interstitialAd = interstitialAd
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        interstitialAd.adListener = object : AdListener() {
            override fun onAdClosed() {
                activity!!.finish()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_ad_success, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as NewAdActivity).hideLoader()
        setArrowButtonColor()
    }

    private fun setArrowButtonColor(){
        activity!!.back_arrow.setOnClickListener {
            if (interstitialAd.isLoaded)
                interstitialAd.show()
            else
                activity!!.finish()
        }
    }
}
