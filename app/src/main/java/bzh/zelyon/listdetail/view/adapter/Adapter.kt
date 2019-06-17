package bzh.zelyon.listdetail.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

abstract class Adapter<T> (val context: Context, var idItemLayout: Int): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var items = listOf<T>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = object : RecyclerView.ViewHolder(LayoutInflater.from(context).inflate(idItemLayout, parent, false)) {}

    override fun getItemCount() = items.size

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        onItemFill(viewHolder.itemView, items, position)
        viewHolder.itemView.setOnClickListener {
            onItemClick(viewHolder.itemView, items, position)
        }
        viewHolder.itemView.setOnLongClickListener {
            onItemLongClick(viewHolder.itemView, items, position)
            true
        }
    }

    abstract fun onItemFill(itemView: View, items: List<T>, position: Int)

    abstract fun onItemClick(itemView: View, items: List<T>, position: Int)

    abstract fun onItemLongClick(itemView: View, items: List<T>, position: Int)
}