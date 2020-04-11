package kz.maltabu.app.maltabukz.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_menu.view.*
import kz.maltabu.app.maltabukz.R
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols


class PhoneAdapter(private val context: Context, private val callMaker: MakeCall) : RecyclerView.Adapter<PhoneAdapter.ViewHolder>() {
    private var phones : List<String> = ArrayList()

    fun setData(phones: List<String>) {
        this.phones = phones
        notifyDataSetChanged()
    }

    fun clearData(){
        this.phones=ArrayList<String>()
    }

    fun getData():List<String>{
        return phones
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val viewHolder: ViewHolder
        viewHolder = ViewHolder(inflater.inflate(R.layout.item_phone, parent, false))
        return viewHolder
    }

    override fun getItemCount(): Int {
        return phones.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.menuName.text="+7 ${phones[position]}"
        holder.itemView.setOnClickListener {
            callMaker.makeCall(phones[position])
        }
    }

    class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        val menuName: TextView = view.menu_title
    }

    interface MakeCall{
        public fun makeCall(phone: String)
    }
}