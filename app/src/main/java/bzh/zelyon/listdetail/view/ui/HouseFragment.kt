package bzh.zelyon.listdetail.view.ui

import android.content.Context
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
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import bzh.zelyon.listdetail.R
import bzh.zelyon.listdetail.db.DB
import bzh.zelyon.listdetail.model.Character
import bzh.zelyon.listdetail.model.House
import bzh.zelyon.listdetail.model.Region
import bzh.zelyon.listdetail.util.colorResToColorInt
import bzh.zelyon.listdetail.util.init
import bzh.zelyon.listdetail.util.setImageUrl
import bzh.zelyon.listdetail.util.share
import bzh.zelyon.listdetail.view.adapter.Adapter
import bzh.zelyon.listdetail.view.custom.ViewParams
import kotlinx.android.synthetic.main.fragment_house.*

class HouseFragment: AbsToolBarFragment() {

    companion object {

        const val ID  = "ID"
        const val PLACEHOLDER  = "PLACEHOLDER"

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

        sharedElementEnterTransition = TransitionInflater.from(mainActivity).inflateTransition(R.transition.enter_transition)
        exitTransition = TransitionInflater.from(mainActivity).inflateTransition(R.transition.exit_transition)
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
            image.setImageUrl(it.getImage(), if (placeholder != null) BitmapDrawable(mainActivity.resources, placeholder) else null)
            view_pager.adapter = PageAdapter()
            view_pager.offscreenPageLimit = Int.MAX_VALUE
            tab_layout.setupWithViewPager(view_pager)
        }
    }

    override fun getLayoutId() = R.layout.fragment_house

    override fun onIdClick(id: Int) {
        when(id) {
            R.id.share -> house?.let { Character.getByFilters(houses = arrayListOf(it.id)).share(mainActivity) }
        }
    }

    override fun getTitle() = house?.label ?: ""

    override fun showBack() = true

    override fun getIdMenu() = R.menu.character

    inner class PageAdapter : PagerAdapter() {

        override fun isViewFromObject(view: View, `object`: Any) = view === `object`

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }
        override fun getItemPosition(`object`: Any) = POSITION_NONE

        override fun getCount() = 3

        override fun getPageTitle(position: Int) = getString(if (position == 0) R.string.fragment_house_tab_infos else if (position == 1) R.string.fragment_house_tab_characters else R.string.fragment_house_tab_region)

        override fun instantiateItem(container: ViewGroup, position: Int): View {
            val linearLayout = LinearLayout(mainActivity)
            linearLayout.orientation = LinearLayout.VERTICAL
            val nestedScrollView = NestedScrollView(mainActivity)
            nestedScrollView.addView(linearLayout, ViewParams(mainActivity, ViewParams.MATCH, ViewParams.MATCH).linear())
            when (position) {
                0 -> {
                    house?.let {
                        val wrecked = AppCompatTextView(mainActivity)
                        wrecked.text = getString(R.string.fragment_house_wrecked)
                        wrecked.visibility = if (it.wrecked) View.VISIBLE else View.GONE
                        linearLayout.addView(wrecked, ViewParams(mainActivity).margins(8).linear())
                        val city = AppCompatTextView(mainActivity)
                        city.text = getString(R.string.fragment_house_capital, it.city)
                        city.visibility = if (it.city.isNotBlank()) View.VISIBLE else View.GONE
                        linearLayout.addView(city, ViewParams(mainActivity).margins(8).linear())
                        val devise = AppCompatTextView(mainActivity)
                        devise.text = getString(R.string.fragment_house_devise, it.devise)
                        devise.visibility = if (it.devise.isNotBlank()) View.VISIBLE else View.GONE
                        linearLayout.addView(devise, ViewParams(mainActivity).margins(8).linear())
                        val proverb = AppCompatTextView(mainActivity)
                        proverb.text = getString(R.string.fragment_house_proverb, it.proverb)
                        proverb.visibility = if (it.proverb != null) View.VISIBLE else View.GONE
                        linearLayout.addView(proverb, ViewParams(mainActivity).margins(8).linear())
                    }
                }
                1 -> {
                    house?.let {
                        val characterAdapter = CharacterAdapter(mainActivity, R.layout.item_module)
                        characterAdapter.items = Character.getByFilters(houses = arrayListOf(it.id))
                        val recyclerView = RecyclerView(mainActivity)
                        recyclerView.init(3)
                        recyclerView.adapter = characterAdapter
                        linearLayout.addView(recyclerView)
                    }
                }
                2 -> {
                    region?.let {
                        val regionName = AppCompatTextView(mainActivity)
                        regionName.text = it.label
                        regionName.textSize = 16f
                        regionName.gravity = Gravity.CENTER
                        linearLayout.addView(regionName, ViewParams(mainActivity).margins(8).centerGravity().linear())
                        val map = AppCompatImageView(mainActivity)
                        map.setImageUrl(it.getMap())
                        linearLayout.addView(map, ViewParams(mainActivity, ViewParams.MATCH, ViewParams.MATCH).linear())
                    }
                }
            }
            container.addView(nestedScrollView)

            return nestedScrollView
        }
    }

    inner class CharacterAdapter constructor(context: Context, idItemLayout: Int) : Adapter<Character>(context, idItemLayout) {

        override fun onItemFill(itemView: View, datas: List<Character>, position: Int) {
            val character = datas[position]
            val image = itemView.findViewById<AppCompatImageView>(R.id.image)
            image.transitionName = character.id.toString()
            image.setImageUrl(character.getThumbnail())
            val badge = itemView.findViewById<AppCompatImageView>(R.id.badge)
            badge.visibility = if (character.id == house?.lord ?: 0) View.VISIBLE else View.GONE
            badge.setColorFilter(mainActivity.colorResToColorInt(R.color.yellow))
            badge.setImageResource(R.drawable.ic_lord)
            itemView.findViewById<AppCompatTextView>(R.id.name).text = character.name
        }

        override fun onItemClick(itemView: View, datas: List<Character>, position: Int) {
            val image = itemView.findViewById<AppCompatImageView>(R.id.image)
            mainActivity.setFragment(CharacterFragment.newInstance(datas[position].id, (image.drawable as BitmapDrawable).bitmap), image)
        }
    }
}