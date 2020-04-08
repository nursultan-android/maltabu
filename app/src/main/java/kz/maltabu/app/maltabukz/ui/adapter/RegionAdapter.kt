package kz.maltabu.app.maltabukz.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_menu.view.*
import kz.maltabu.app.maltabukz.R
import kz.maltabu.app.maltabukz.network.models.response.City
import kz.maltabu.app.maltabukz.network.models.response.Region

class RegionAdapter(val chooser: ChooseRegion) : RecyclerView.Adapter<RegionAdapter.ViewHolder>() {
    private var regions : List<Region> = ArrayList()

    fun setData(regions: List<Region>) {
        this.regions = regions
        notifyDataSetChanged()
    }

    fun clearData(){
        this.regions=ArrayList<Region>()
    }

    fun getData():List<Region>{
        return regions
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val viewHolder: ViewHolder
        viewHolder = ViewHolder(inflater.inflate(R.layout.item_category, parent, false))
        return viewHolder
    }

    override fun getItemCount(): Int {
        return regions.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentCategory =regions[position]
        holder.menuName.text=currentCategory.name
        holder.itemView.setOnClickListener {
            chooser.chooseRegion(currentCategory)
        }
    }

    class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        val menuName: TextView = view.menu_title
    }

    interface ChooseRegion {
        fun chooseRegion(region:Region)
    }
}