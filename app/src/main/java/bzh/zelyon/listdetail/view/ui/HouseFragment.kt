package bzh.zelyon.listdetail.view.ui

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.widget.NestedScrollView
import androidx.viewpager.widget.PagerAdapter
import bzh.zelyon.lib.extension.colorResToColorInt
import bzh.zelyon.lib.extension.setImage
import bzh.zelyon.lib.extension.showFragment
import bzh.zelyon.lib.ui.component.CollectionsView
import bzh.zelyon.lib.ui.component.ViewParams
import bzh.zelyon.lib.ui.view.fragment.AbsToolBarFragment
import bzh.zelyon.listdetail.R
import bzh.zelyon.listdetail.db.DB
import bzh.zelyon.listdetail.model.Character
import bzh.zelyon.listdetail.model.House
import bzh.zelyon.listdetail.model.Region
import bzh.zelyon.listdetail.util.share
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.fragment_house.*

class HouseFragment: AbsToolBarFragment() {

    companion object {

        const val ID = "ID"
        const val PLACEHOLDER = "PLACEHOLDER"

        fun newInstance(id: Long, placeholder: Bitmap) =
            HouseFragment().apply {
                arguments = Bundle().apply {
                    putLong(ID, id)
                    putParcelable(PLACEHOLDER, placeholder)
                }
            }
    }

    private var house: House? = null
    private var region: Region? = null
    private var placeholder: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedElementEnterTransition = TransitionInflater.from(absActivity).inflateTransition(R.transition.enter_transition)
        exitTransition = TransitionInflater.from(absActivity).inflateTransition(R.transition.exit_transition)
        postponeEnterTransition()
        startPostponedEnterTransition()

        arguments?.let {
            house = DB.getHouseDao().getById(it.getLong(ID))
            placeholder = it.getParcelable(PLACEHOLDER)
            region = DB.getRegionDao().getById(house?.region ?: 0)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        house?.let {
            image.transitionName = it.id.toString()
            image.setImage(it.getImage(), if (placeholder != null) BitmapDrawable(absActivity.resources, placeholder) else null)
            view_pager.adapter = PageAdapter()
            view_pager.offscreenPageLimit = Int.MAX_VALUE
            tab_layout.setupWithViewPager(view_pager)
        }
    }

    override fun getIdLayout() = R.layout.fragment_house

    override fun onIdClick(id: Int) {
        when(id) {
            R.id.share -> house?.let { Character.getByFilters(houses = arrayListOf(it.id)).share(absActivity) }
        }
    }

    override fun getTitleToolBar() = house?.label ?: ""

    override fun showBack() = true

    override fun getIdMenu() = R.menu.character

    override fun getIdToolbar() = R.id.toolbar

    inner class PageAdapter : PagerAdapter() {

        override fun isViewFromObject(view: View, `object`: Any) = view === `object`

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }
        override fun getItemPosition(`object`: Any) = POSITION_NONE

        override fun getCount() = 3

        override fun getPageTitle(position: Int) = getString(if (position == 0) R.string.fragment_house_tab_infos else if (position == 1) R.string.fragment_house_tab_characters else R.string.fragment_house_tab_region)

        override fun instantiateItem(container: ViewGroup, position: Int): View {
            val linearLayout = LinearLayout(absActivity)
            linearLayout.orientation = LinearLayout.VERTICAL
            val nestedScrollView = NestedScrollView(absActivity)
            nestedScrollView.addView(linearLayout, ViewParams(absActivity, ViewParams.MATCH, ViewParams.MATCH).linear())
            when (position) {
                0 -> {
                    house?.let {
                        val wrecked = AppCompatTextView(absActivity)
                        wrecked.text = getString(R.string.fragment_house_wrecked)
                        wrecked.visibility = if (it.wrecked) View.VISIBLE else View.GONE
                        linearLayout.addView(wrecked, ViewParams(absActivity).margins(8).linear())
                        val city = AppCompatTextView(absActivity)
                        city.text = getString(R.string.fragment_house_capital, it.city)
                        city.visibility = if (it.city.isNotBlank()) View.VISIBLE else View.GONE
                        linearLayout.addView(city, ViewParams(absActivity).margins(8).linear())
                        val devise = AppCompatTextView(absActivity)
                        devise.text = getString(R.string.fragment_house_devise, it.devise)
                        devise.visibility = if (it.devise.isNotBlank()) View.VISIBLE else View.GONE
                        linearLayout.addView(devise, ViewParams(absActivity).margins(8).linear())
                        val proverb = AppCompatTextView(absActivity)
                        proverb.text = getString(R.string.fragment_house_proverb, it.proverb)
                        proverb.visibility = if (it.proverb != null) View.VISIBLE else View.GONE
                        linearLayout.addView(proverb, ViewParams(absActivity).margins(8).linear())
                    }
                }
                1 -> {
                    house?.let {
                        val itemsView = CollectionsView(absActivity)
                        itemsView.nbColumns = 3
                        itemsView.idLayoutItem = R.layout.item_module
                        itemsView.items = Character.getByFilters(houses = arrayListOf(it.id)).toMutableList()
                        itemsView.helper = object : CollectionsView.Helper() {
                            override fun onBindItem(itemView: View, items: MutableList<*>, position: Int) {
                                val character = items[position]
                                if (character is Character) {
                                    val image = itemView.findViewById<AppCompatImageView>(R.id.image)
                                    image.transitionName = character.id.toString()
                                    image.setImage(character.getThumbnail())
                                    val badge = itemView.findViewById<AppCompatImageView>(R.id.badge)
                                    badge.visibility = if (character.id == house?.lord ?: 0) View.VISIBLE else View.GONE
                                    badge.setColorFilter(absActivity.colorResToColorInt(R.color.yellow))
                                    badge.setImageResource(R.drawable.ic_lord)
                                    itemView.findViewById<AppCompatTextView>(R.id.name).text = character.name
                                }
                            }

                            override fun onItemClick(itemView: View, items: MutableList<*>, position: Int) {
                                val character = items[position]
                                if (character is Character) {
                                    val image = itemView.findViewById<AppCompatImageView>(R.id.image)
                                    absActivity.showFragment(CharacterFragment.newInstance(character.id, (image.drawable as BitmapDrawable).bitmap), transitionView =  image)
                                }
                            }
                        }
                        linearLayout.addView(itemsView)
                    }
                }
                2 -> {
                    region?.let {
                        val regionName = AppCompatTextView(absActivity)
                        regionName.text = it.label
                        regionName.textSize = 16f
                        regionName.gravity = Gravity.CENTER
                        linearLayout.addView(regionName, ViewParams(absActivity).margins(8).centerGravity().linear())
                        val map = AppCompatImageView(absActivity)
                        Glide.with(absActivity).load(it.getMap()).override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).into(map)
                        linearLayout.addView(map, ViewParams(absActivity, ViewParams.MATCH, ViewParams.MATCH).linear())
                    }
                }
            }
            container.addView(nestedScrollView)

            return nestedScrollView
        }
    }
}