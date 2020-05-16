package kz.maltabu.app.maltabukz.utils.yandexAds

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.yandex.mobile.ads.nativeads.template.NativeBannerView
import kotlinx.android.synthetic.main.activity_show_ad.view.*
import kotlinx.android.synthetic.main.item_ad.view.*
import kz.maltabu.app.maltabukz.R

class AdapterHolder {
    object BlockContentProvider {
        const val NONE_TYPE = -1
        const val DEFAULT = 0
        const val NATIVE_BANNER = 1
    }

    class NativeBannerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nativeBannerView: NativeBannerView = itemView.findViewById<View>(R.id.native_template) as NativeBannerView
    }

    class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.ad_title
        val date: TextView = view.ad_date
        val location: TextView = view.ad_location
        val price: TextView = view.ad_price
        val img: ImageView = view.ad_img
        val visitors: TextView = view.ad_visitors_count_text
        val photoCount: TextView = view.ad_photo_count_text
    }
}