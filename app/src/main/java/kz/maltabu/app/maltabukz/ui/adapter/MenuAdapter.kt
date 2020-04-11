package kz.maltabu.app.maltabukz.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_menu.view.*
import kz.maltabu.app.maltabukz.R
import kz.maltabu.app.maltabukz.network.models.response.MenuCategory
import kz.maltabu.app.maltabukz.utils.SvgLoaderUtils
import org.jetbrains.anko.sdk27.coroutines.onClick

class MenuAdapter(private val context: Context, private val choose: ChooseCategory) :
    RecyclerView.Adapter<MenuAdapter.ViewHolder>() {
    private var categories : List<MenuCategory> = ArrayList()

    fun setData(categories: List<MenuCategory>) {
        this.categories = categories
        notifyDataSetChanged()
    }

    fun clearData(){
        this.categories=ArrayList<MenuCategory>()
    }

    fun getData():List<MenuCategory>{
        return categories
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val viewHolder: ViewHolder
        viewHolder = ViewHolder(inflater.inflate(R.layout.item_menu, parent, false))
        return viewHolder
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentCategory =categories[position]
        holder.menuName.text=currentCategory.name
        if(currentCategory.image.endsWith("svg"))
            SvgLoaderUtils.fetchSvg(context, currentCategory.image, holder.menuImage)
        else
            Glide.with(context).load(currentCategory.image).centerCrop().fitCenter().into(holder.menuImage)
        holder.itemView.onClick {
            choose.chooseCategory(currentCategory)
        }
    }

    class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        val menuName: TextView = view.menu_title
        val menuImage: ImageView = view.menu_img
    }

    interface ChooseCategory{
        public fun chooseCategory(category: MenuCategory){}
    }
}