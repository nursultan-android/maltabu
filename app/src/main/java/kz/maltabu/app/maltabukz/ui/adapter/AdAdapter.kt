package kz.maltabu.app.maltabukz.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.makeramen.roundedimageview.RoundedImageView
import kotlinx.android.synthetic.main.item_ad.view.*
import kz.maltabu.app.maltabukz.R
import kz.maltabu.app.maltabukz.network.models.response.Ad

class AdAdapter(val context: Context) : RecyclerView.Adapter<AdAdapter.ViewHolder>() {
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
        holder.price.text=ad.amount.toString()
        holder.visitors.text=ad.visited.toString()
        holder.photoCount.text=ad.images.size.toString()
        Glide.with(context).load(ad.image).centerCrop().into(holder.img)
    }

    class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.ad_title
        val date: TextView = view.ad_date
        val price: TextView = view.ad_price
        val img: RoundedImageView = view.ad_img
        val visitors: TextView = view.ad_visitors_count_text
        val photoCount: TextView = view.ad_photo_count_text
    }

}