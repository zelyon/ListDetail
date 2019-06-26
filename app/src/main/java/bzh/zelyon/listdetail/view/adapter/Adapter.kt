package bzh.zelyon.listdetail.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import java.util.*

open class Adapter<T> (val context: Context, var idItemLayout: Int): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var items = listOf<T>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var recyclerViewDragNDrop: RecyclerView? = null
        set(value) {
            field = value
            value?.let {
                itemTouchHelper.attachToRecyclerView(it)
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = object : RecyclerView.ViewHolder(LayoutInflater.from(context).inflate(idItemLayout, parent, false)) {}

    override fun getItemCount() = items.size

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        onItemFill(viewHolder.itemView, items, position)
        viewHolder.itemView.setOnClickListener {
            onItemClick(viewHolder.itemView, items, position)
        }
        viewHolder.itemView.setOnLongClickListener {
            if (recyclerViewDragNDrop != null) {
                itemTouchHelper.startDrag(viewHolder)
            }
            else {
                onItemLongClick(viewHolder.itemView, items, position)
            }
            true
        }
    }

    open fun onItemFill(itemView: View, items: List<T>, position: Int) {}

    open fun onItemClick(itemView: View, items: List<T>, position: Int) {}

    open fun onItemLongClick(itemView: View, items: List<T>, position: Int) {}

    open fun onItemStartDrag(itemView: View) {}

    open fun onItemEndDrag(itemView: View) {}

    open fun onItemsSwap(items: List<T>) {}

    private val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.Callback(){

        override fun getMovementFlags(p0: RecyclerView, p1: RecyclerView.ViewHolder) =  makeMovementFlags(ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT, 0)

        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
            Collections.swap(items, viewHolder.adapterPosition, target.adapterPosition)
            onItemsSwap(items)
            notifyItemMoved(viewHolder.adapterPosition, target.adapterPosition)

            return true
        }

        override fun onSwiped(p0: RecyclerView.ViewHolder, p1: Int) {}

        override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
            if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                viewHolder?.let {
                    onItemStartDrag(it.itemView)
                }
            }

            super.onSelectedChanged(viewHolder, actionState)
        }

        override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
            super.clearView(recyclerView, viewHolder)
            onItemEndDrag(viewHolder.itemView)
        }
    })
}