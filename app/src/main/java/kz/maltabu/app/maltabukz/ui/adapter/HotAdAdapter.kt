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
import kotlinx.android.synthetic.main.item_hot.view.*
import kz.maltabu.app.maltabukz.R
import kz.maltabu.app.maltabukz.di.BaseUseCase
import kz.maltabu.app.maltabukz.network.models.response.Ad
import kz.maltabu.app.maltabukz.utils.FormatHelper
import org.koin.core.inject

class HotAdAdapter(val context: Context, private val chooseAd: ChooseAd) : RecyclerView.Adapter<HotAdAdapter.ViewHolder>(), BaseUseCase {
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
        viewHolder = ViewHolder(inflater.inflate(R.layout.item_hot, parent, false))
        return viewHolder
    }

    override fun getItemCount(): Int {
        return adList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ad =adList[position]
        holder.price.text= formatHelper.setFormat(ad.currency, ad.amount)
        holder.photoCount.text=ad.images.size.toString()
        holder.location.text=ad.city
        if(ad.images.size>0)
            glideManager.load(ad.image).placeholder(context.getDrawable(R.drawable.ic_photography_loading)).centerCrop().into(holder.img)
        else
            holder.img.setImageDrawable(context.getDrawable(R.drawable.ic_no_photo_colored))
        holder.itemView.setOnClickListener {
            chooseAd.chooseAd(ad)
        }
    }

    class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        val price: TextView = view.hot_ad_price
        val location: TextView = view.hot_ad_location
        val img: ImageView = view.hot_ad_img
        val photoCount: TextView = view.hot_ad_photo_count
    }

    interface ChooseAd{
        fun chooseAd(ad: Ad)
    }

}