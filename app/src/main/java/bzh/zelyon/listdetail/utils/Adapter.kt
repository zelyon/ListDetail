package bzh.zelyon.listdetail.utils

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.Adapter
import android.support.v7.widget.RecyclerView.ViewHolder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

abstract class Adapter<T>(val context: Context, var idItemLayout: Int): Adapter<ViewHolder>() {

    var datas: List<T> = ArrayList()
        set(value) {

            field = value

            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return object : RecyclerView.ViewHolder(LayoutInflater.from(context).inflate(idItemLayout, parent, false)) {}
    }

    override fun getItemCount(): Int {

        return datas.size
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        onItemFill(viewHolder.itemView, datas, position)

        viewHolder.itemView.setOnClickListener {

            onItemClick(viewHolder.itemView, datas, position)
        }

        viewHolder.itemView.setOnLongClickListener {

            onItemLongClick(viewHolder.itemView, datas, position)

            true
        }
    }

    abstract fun onItemFill(itemView: View, datas: List<T>, position: Int)

    abstract fun onItemClick(itemView: View, datas: List<T>, position: Int)

    abstract fun onItemLongClick(itemView: View, datas: List<T>, position: Int)
}