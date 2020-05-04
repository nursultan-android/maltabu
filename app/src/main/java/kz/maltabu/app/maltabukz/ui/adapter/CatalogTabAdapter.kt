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
import kotlinx.android.synthetic.main.item_menu.view.*
import kz.maltabu.app.maltabukz.R
import kz.maltabu.app.maltabukz.di.BaseUseCase
import kz.maltabu.app.maltabukz.network.models.response.CategoryChild
import kz.maltabu.app.maltabukz.network.models.response.MenuCategory
import kz.maltabu.app.maltabukz.utils.CustomAnimator
import kz.maltabu.app.maltabukz.utils.SvgLoaderUtils
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.koin.core.inject

class CatalogTabAdapter(private val context: Context, private val listener: ChooseCategory) : RecyclerView.Adapter<CatalogTabAdapter.ViewHolder>(), BaseUseCase {
    private var categories : List<CategoryChild> = ArrayList()
    private var selectedTavIndex = 0
    private val glideManager: RequestManager by inject()

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
        viewHolder = ViewHolder(inflater.inflate(R.layout.item_catalog, parent, false))
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
            glideManager.load(currentCategory.image).centerCrop().fitCenter().into(holder.menuImage)
        holder.itemView.onClick {
            selectedTavIndex = position
            listener.chooseCatalog(position, currentCategory)
            notifyDataSetChanged()
            CustomAnimator.animateTab(holder.itemView)
        }

        if(selectedTavIndex==position){
            holder.menuName.alpha=1f
            holder.menuImage.alpha=1f
        } else {
            holder.menuName.alpha=0.3f
            holder.menuImage.alpha=0.3f
        }
    }

    class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        val menuName: TextView = view.menu_title
        val menuImage: ImageView = view.menu_img
    }

    interface ChooseCategory{
        fun chooseCatalog(position: Int, category: CategoryChild){}
    }
}