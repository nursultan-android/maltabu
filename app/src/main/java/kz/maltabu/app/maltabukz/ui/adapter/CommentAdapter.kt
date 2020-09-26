package kz.maltabu.app.maltabukz.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_comment.view.*
import kotlinx.android.synthetic.main.item_menu.view.*
import kz.maltabu.app.maltabukz.R
import kz.maltabu.app.maltabukz.network.models.comment.Comment
import kz.maltabu.app.maltabukz.network.models.response.CategoryChild
import kz.maltabu.app.maltabukz.network.models.response.MenuCategory
import org.jetbrains.anko.sdk27.coroutines.onClick

class CommentAdapter(private val context: Context) : RecyclerView.Adapter<CommentAdapter.ViewHolder>() {
    private var comments : List<Comment> = ArrayList()

    fun setData(comments: List<Comment>) {
        this.comments = comments
        notifyDataSetChanged()
    }

    fun clearData(){
        this.comments=ArrayList<Comment>()
    }

    fun getData():List<Comment>{
        return comments
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val viewHolder: ViewHolder
        viewHolder = ViewHolder(inflater.inflate(R.layout.item_comment, parent, false))
        return viewHolder
    }

    override fun getItemCount(): Int {
        return comments.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentComment =comments[position]
        holder.date.text=currentComment.date
        holder.text.text=currentComment.text
        holder.owner.text="*****${currentComment.user.name.substring(currentComment.user.name.length-5)}"
    }

    class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        val owner: TextView = view.comment_owner
        val date: TextView = view.comment_date
        val text: TextView = view.comment_text
    }
}