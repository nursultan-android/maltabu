package kz.maltabu.app.maltabukz.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.makeramen.roundedimageview.RoundedImageView
import kotlinx.android.synthetic.main.item_ad.view.*
import kz.maltabu.app.maltabukz.R
import kz.maltabu.app.maltabukz.di.BaseUseCase
import kz.maltabu.app.maltabukz.network.models.response.Ad
import kz.maltabu.app.maltabukz.utils.CustomAnimator
import kz.maltabu.app.maltabukz.utils.FormatHelper
import org.koin.core.inject

class AdAdapter(val context: Context, val chooseAd: AdAdapterWithAdvers.ChooseAd) : RecyclerView.Adapter<AdAdapter.ViewHolder>(), BaseUseCase {
    private var adList : List<Ad> = ArrayList()
    private val formatHelper: FormatHelper by inject()
    private val glideManager: RequestManager by inject()

    fun setData(adList: List<Ad>) {
        this.adList = adList
        notifyDataSetChanged()
    }

    fun clearData(){
        this.adList=ArrayList<Ad>()
    }

    fun getData():List<Ad>{
        return adList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val viewHolder: ViewHolder
        viewHolder = ViewHolder(inflater.inflate(R.layout.item_ad, parent, false))
        return viewHolder
    }

    override fun getItemCount(): Int {
        return adList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ad =adList[position]
        holder.title.text=ad.title
        holder.date.text=ad.date
        holder.price.text= formatHelper.setFormat(ad.currency, ad.amount)
        holder.visitors.text=ad.visited.toString()
        holder.photoCount.text=ad.images.size.toString()
        if(ad.images.size>0)
            glideManager.load(ad.image).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).placeholder(context.getDrawable(R.drawable.ic_photography_loading)).centerCrop().into(holder.img)
        else
            holder.img.setImageDrawable(context.getDrawable(R.drawable.ic_no_photo_colored))
        holder.itemView.setOnClickListener {
            CustomAnimator.animateHotViewLinear(holder.itemView)
            chooseAd.chooseAd(ad)
        }
    }

    class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.ad_title
        val date: TextView = view.ad_date
        val price: TextView = view.ad_price
        val img: ImageView = view.ad_img
        val visitors: TextView = view.ad_visitors_count_text
        val photoCount: TextView = view.ad_photo_count_text
    }

    interface ChooseAd{
        fun chooseAd(ad: Ad)
    }

}