package kz.maltabu.app.maltabukz.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_menu.view.*
import kz.maltabu.app.maltabukz.R
import kz.maltabu.app.maltabukz.network.models.response.CategoryChild
import org.jetbrains.anko.sdk27.coroutines.onClick

class CatalogAdapter(val listener: CategoryAdapter.ChooseCategory) :
    RecyclerView.Adapter<CatalogAdapter.ViewHolder>() {
    private var categories : List<CategoryChild> = ArrayList()

    fun setData(categories: List<CategoryChild>) {
        this.categories = categories
        notifyDataSetChanged()
    }

    fun clearData(){
        this.categories=ArrayList<CategoryChild>()
    }

    fun getData():List<CategoryChild>{
        return categories
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val viewHolder: ViewHolder
        viewHolder = ViewHolder(inflater.inflate(R.layout.item_category, parent, false))
        return viewHolder
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentCategory =categories[position]
        holder.menuName.text=currentCategory.name
        holder.itemView.onClick {
            listener.chooseCatalog(currentCategory)
        }
    }

    class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        val menuName: TextView = view.menu_title
    }

}