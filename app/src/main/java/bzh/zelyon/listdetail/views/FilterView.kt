package bzh.zelyon.listdetail.views

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.design.chip.Chip
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import bzh.zelyon.listdetail.R
import kotlinx.android.synthetic.main.view_filter.view.*

class FilterView<T> @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {

    private lateinit var items:List<Item<T>>
    private lateinit var selectedValues:ArrayList<T>
    private lateinit var appliedValues:ArrayList<T>

    init {

        View.inflate(context, R.layout.view_filter, this)

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.FilterView, defStyleAttr, 0)
        title.text = typedArray.getString(R.styleable.FilterView_title)
        typedArray.recycle()
    }

    fun load(items: List<Item<T>>, selectedAndApplyValue: List<T>) {

        this.items = items
        this.selectedValues = ArrayList(selectedAndApplyValue)
        this.appliedValues = ArrayList(selectedAndApplyValue)

        reload()
    }

    fun getSelectedAndApply(): ArrayList<T> {

        appliedValues = ArrayList(selectedValues)

        return appliedValues
    }

    fun restore() {

        selectedValues = ArrayList(appliedValues)

        reload()
    }

    fun clear() {

        selectedValues = ArrayList()

        reload()
    }

    private fun reload() {

        chip_group.removeAllViews()

        for(item in items) {

            val chip = Chip(context)
            chip.text = item.label
            chip.tag = item.value
            chip.isCheckable = true
            chip.isChecked = selectedValues.contains(item.value)
            chip.chipIcon = item.image
            chip.setOnCheckedChangeListener { _, _ ->

                if(selectedValues.contains(item.value)) {

                    selectedValues.remove(item.value)
                }
                else {

                    selectedValues.add(item.value)
                }
            }

            chip_group.addView(chip)
        }
    }

    class Item<T> (val value: T, val label: String, val image: Drawable)
}