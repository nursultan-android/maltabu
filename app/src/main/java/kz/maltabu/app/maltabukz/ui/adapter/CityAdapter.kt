package kz.maltabu.app.maltabukz.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_menu.view.*
import kz.maltabu.app.maltabukz.R
import kz.maltabu.app.maltabukz.network.models.response.AmountType
import kz.maltabu.app.maltabukz.network.models.response.City

class CityAdapter(val chooser: ChooseCity) : RecyclerView.Adapter<CityAdapter.ViewHolder>() {
    private var cities : List<City> = ArrayList()

    fun setData(cities: List<City>) {
        this.cities = cities
        notifyDataSetChanged()
    }

    fun clearData(){
        this.cities=ArrayList<City>()
    }

    fun getData():List<City>{
        return cities
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val viewHolder: ViewHolder
        viewHolder = ViewHolder(inflater.inflate(R.layout.item_category, parent, false))
        return viewHolder
    }

    override fun getItemCount(): Int {
        return cities.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val city =cities[position]
        holder.menuName.text=city.name
        holder.itemView.setOnClickListener {
            chooser.chooseCity(city)
        }
    }

    class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        val menuName: TextView = view.menu_title
    }

    interface ChooseCity {
        fun chooseCity(city: City)
    }

}