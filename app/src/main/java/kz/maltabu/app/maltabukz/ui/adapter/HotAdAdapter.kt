package kz.maltabu.app.maltabukz.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_ad.view.*
import kotlinx.android.synthetic.main.item_hot.view.*
import kz.maltabu.app.maltabukz.R
import kz.maltabu.app.maltabukz.network.models.response.Ad
import kz.maltabu.app.maltabukz.utils.CustomAnimator

class HotAdAdapter(val context: Context, private val chooseAd: ChooseAd) : RecyclerView.Adapter<HotAdAdapter.ViewHolder>() {
    private var adList : List<Ad> = ArrayList()

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
        holder.price.text=ad.amount.toString()+ad.currency
        holder.photoCount.text=ad.images.size.toString()
        holder.location.text=ad.city
        Glide.with(context).load(ad.image).placeholder(context.getDrawable(R.drawable.ic_no_photo)).centerCrop().into(holder.img)
        holder.itemView.setOnClickListener {
            CustomAnimator.animateHotViewLinear(holder.itemView)
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