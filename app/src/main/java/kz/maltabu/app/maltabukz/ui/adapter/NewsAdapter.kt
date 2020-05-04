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
import kotlinx.android.synthetic.main.item_ad.view.*
import kotlinx.android.synthetic.main.item_news.view.*
import kz.maltabu.app.maltabukz.R
import kz.maltabu.app.maltabukz.di.BaseUseCase
import kz.maltabu.app.maltabukz.network.models.response.News
import org.koin.core.inject

class NewsAdapter(val context: Context, private val chooser: ChooseNews) : RecyclerView.Adapter<NewsAdapter.ViewHolder>(), BaseUseCase {
    private var newsList : List<News> = ArrayList()
    private val glideManager: RequestManager by inject()

    fun setData(newsList: List<News>) {
        this.newsList = newsList
        notifyDataSetChanged()
    }

    fun clearData(){
        this.newsList=ArrayList<News>()
    }

    fun getData():List<News>{
        return newsList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val viewHolder: ViewHolder
        viewHolder = ViewHolder(inflater.inflate(R.layout.item_news, parent, false))
        return viewHolder
    }

    override fun getItemCount(): Int {
        return newsList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val news =newsList[position]
        holder.title.text=news.title
        holder.date.text=news.date
        holder.visitors.text=news.visited.toString()
        glideManager.load(news.image).placeholder(context.getDrawable(R.drawable.ic_no_photo_colored)).centerCrop().into(holder.img)
        holder.itemView.setOnClickListener {
            chooser.chooseNews(news)
        }
    }

    class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.news_title
        val date: TextView = view.news_date
        val visitors: TextView = view.visitors_count
        val img: ImageView = view.news_image
    }

    interface ChooseNews{
        fun chooseNews(news: News)
    }

}