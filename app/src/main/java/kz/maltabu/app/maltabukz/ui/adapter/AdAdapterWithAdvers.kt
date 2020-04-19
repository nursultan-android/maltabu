package kz.maltabu.app.maltabukz.ui.adapter

import android.content.Context
import android.graphics.Color
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.yandex.mobile.ads.nativeads.NativeGenericAd
import kz.maltabu.app.maltabukz.R
import kz.maltabu.app.maltabukz.network.models.response.Ad
import kz.maltabu.app.maltabukz.utils.CustomAnimator
import kz.maltabu.app.maltabukz.utils.FormatHelper
import kz.maltabu.app.maltabukz.utils.yandexAds.AdapterHolder
import kz.maltabu.app.maltabukz.utils.yandexAds.NativeBlock
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

class AdAdapterWithAdvers(val context: Context, private val chooseAd: ChooseAd) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val mBlockContentHelper= NativeBlock()
    private lateinit var mData: List<Pair<Int, Any>>

    fun setData(dataList: List<Pair<Int, Any>>) {
        mData = dataList
        mBlockContentHelper.setData(mData)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var viewHolder:RecyclerView.ViewHolder? = null
        when(viewType){
            AdapterHolder.BlockContentProvider.DEFAULT->{
                val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_ad, parent, false)
                viewHolder = AdapterHolder.ViewHolder(view)
            }
            AdapterHolder.BlockContentProvider.NATIVE_BANNER->{
                val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_native_template, parent, false)
                viewHolder = AdapterHolder.NativeBannerViewHolder(view)
            }
        }
        return viewHolder!!
    }

    override fun getItemViewType(position: Int): Int {
        return mBlockContentHelper.getItemType(position)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val type = getItemViewType(position)
        when(type){
            AdapterHolder.BlockContentProvider.NATIVE_BANNER-> {
                bindNativeBanner(holder as AdapterHolder.NativeBannerViewHolder, position)
            }
            AdapterHolder.BlockContentProvider.DEFAULT->{
                bindItem(holder as AdapterHolder.ViewHolder, position)
            }
        }
    }

    private fun bindItem(holder: AdapterHolder.ViewHolder, position: Int) {
        val ad = mData[position].second as Ad
        holder.title.text=ad.title
        holder.date.text=ad.date
        holder.price.text=FormatHelper.setFormat(ad.currency, ad.amount)
        holder.visitors.text=ad.visited.toString()
        holder.photoCount.text=ad.images.size.toString()
        if(ad.images.size>0)
            Glide.with(context).load(ad.image).placeholder(context.getDrawable(R.drawable.ic_photography_loading)).centerCrop().into(holder.img)
        else
            holder.img.setImageDrawable(context.getDrawable(R.drawable.ic_no_photo_colored))
        holder.itemView.setOnClickListener {
            chooseAd.chooseAd(ad)
        }
    }

    private fun bindNativeBanner(holder: AdapterHolder.NativeBannerViewHolder, position: Int) {
        holder.nativeBannerView.visibility = View.GONE
        if (mData[position].second is NativeGenericAd) {
            val nativeAd = mData[position].second as NativeGenericAd
            holder.nativeBannerView.setAd(nativeAd)
            holder.nativeBannerView.visibility = View.VISIBLE
            nativeAd.loadImages()
        } else {
            holder.nativeBannerView.setBackgroundColor(Color.parseColor("#000fff"))
        }
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    interface ChooseAd{
        fun chooseAd(ad: Ad)
    }

}