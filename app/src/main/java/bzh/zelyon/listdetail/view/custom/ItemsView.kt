package bzh.zelyon.listdetail.view.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class ItemsView<T> @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0): RecyclerView(context, attrs, defStyleAttr) {

    var items = listOf<T>()
        set(value) {
            field = value
            refresh()
        }

    var isDragNDrop = false
        set(value) {
            field = value
            itemTouchHelper.attachToRecyclerView(if (value) this else null)
        }

    var itemsListener: ItemsListener<T>? = null

    init {
        setHasFixedSize(false)
        isNestedScrollingEnabled = false
        setItemsView(1, android.R.layout.simple_list_item_1)
    }

    fun setItemsView(nbColumns: Int, idItemLayout: Int) {
        layoutManager = if (nbColumns == 1) LinearLayoutManager(context) else GridLayoutManager(context, nbColumns)
        adapter = ItemsAdapter(context, idItemLayout)
    }

    fun refresh() {
        adapter?.notifyDataSetChanged()
    }

    private val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.Callback() {

        override fun getMovementFlags(p0: RecyclerView, p1: ViewHolder) = makeMovementFlags(ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT, 0)

        override fun onMove(recyclerView: RecyclerView, viewHolder: ViewHolder, target: ViewHolder): Boolean {
            Collections.swap(items, viewHolder.adapterPosition, target.adapterPosition)
            itemsListener?.onItemsSwap(items)
            adapter?.notifyItemMoved(viewHolder.adapterPosition, target.adapterPosition)
            return true
        }

        override fun onSwiped(p0: ViewHolder, p1: Int) {}

        override fun onSelectedChanged(viewHolder: ViewHolder?, actionState: Int) {
            if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                viewHolder?.let {
                    itemsListener?.onItemStartDrag(it.itemView)
                }
            }
            super.onSelectedChanged(viewHolder, actionState)
        }

        override fun clearView(recyclerView: RecyclerView, viewHolder: ViewHolder) {
            super.clearView(recyclerView, viewHolder)
            itemsListener?.onItemEndDrag(viewHolder.itemView)
        }
    })

    inner class ItemsAdapter(val context: Context, var idItemLayout: Int): RecyclerView.Adapter<ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = object : RecyclerView.ViewHolder(LayoutInflater.from(context).inflate(idItemLayout, parent, false)) {}

        override fun getItemCount() = items.size

        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
            itemsListener?.onItemFill(viewHolder.itemView, items, position)
            viewHolder.itemView.setOnClickListener {
                itemsListener?.onItemClick(viewHolder.itemView, items, position)
            }
            viewHolder.itemView.setOnLongClickListener {
                if (isDragNDrop) {
                    itemTouchHelper.startDrag(viewHolder)
                } else {
                    itemsListener?.onItemLongClick(viewHolder.itemView, items, position)
                }
                true
            }
        }
    }

    interface ItemsListener<T> {
        fun onItemFill(itemView: View, items: List<T>, position: Int)
        fun onItemClick(itemView: View, items: List<T>, position: Int)
        fun onItemLongClick(itemView: View, items: List<T>, position: Int)
        fun onItemStartDrag(itemView: View)
        fun onItemEndDrag(itemView: View)
        fun onItemsSwap(items: List<T>)
    }
}