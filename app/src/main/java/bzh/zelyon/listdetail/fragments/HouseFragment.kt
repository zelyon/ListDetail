package bzh.zelyon.listdetail.fragments

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.view.PagerAdapter
import android.support.v4.widget.NestedScrollView
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.AppCompatTextView
import android.support.v7.widget.RecyclerView
import android.transition.TransitionInflater
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import bzh.zelyon.listdetail.*
import bzh.zelyon.listdetail.models.Character
import bzh.zelyon.listdetail.models.House
import bzh.zelyon.listdetail.models.Region
import bzh.zelyon.listdetail.utils.Adapter
import bzh.zelyon.listdetail.utils.DB
import bzh.zelyon.listdetail.utils.ViewParams
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

    lateinit var house: House
    lateinit var region: Region
    lateinit var placeholder: Drawable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedElementEnterTransition = TransitionInflater.from(mainActivity).inflateTransition(R.transition.enter_transition)
        exitTransition = TransitionInflater.from(mainActivity).inflateTransition(R.transition.exit_transition)
        postponeEnterTransition()
        startPostponedEnterTransition()

        arguments?.let {

            house = DB.getHouseDao().getById(it.getLong(ID))
            placeholder = BitmapDrawable(mainActivity.resources, it.getParcelable(PLACEHOLDER) as Bitmap)
        }

        region = DB.getRegionDao().getById(house.region)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        image.transitionName = house.id.toString()
        image.setImageUrl(house.getImage(), placeholder)

        view_pager.adapter = pagerAdapter
        view_pager.offscreenPageLimit = Integer.MAX_VALUE

        tab_layout.setupWithViewPager(view_pager)
    }

    override fun getLayoutId()= R.layout.fragment_house

    override fun onIdClick(id: Int) {

        when(id) {

            R.id.share -> {

                DB.getCharacterDao().getByHouse(arrayListOf(house.id)).share(mainActivity)
            }
        }
    }

    override fun getTitle() = house.label

    override fun showBack() = true

    override fun getIdMenu() = R.menu.character

    override fun onMenuCreated() {}

    private val pagerAdapter = object : PagerAdapter() {

        override fun isViewFromObject(view: View, `object`: Any): Boolean {

            return view === `object`
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {

            container.removeView(`object` as View)
        }

        override fun getItemPosition(`object`: Any): Int {

            return PagerAdapter.POSITION_NONE
        }

        override fun getCount(): Int {

            return 3
        }

        override fun getPageTitle(position: Int): CharSequence? {

            return getString(if (position == 0) R.string.fragment_house_tab_infos else if (position == 1) R.string.fragment_house_tab_characters else R.string.fragment_house_tab_region)
        }

        override fun instantiateItem(container: ViewGroup, position: Int): View {

            val nestedScrollView = NestedScrollView(mainActivity)

            val linearLayout = LinearLayout(mainActivity)
            linearLayout.orientation = LinearLayout.VERTICAL

            nestedScrollView.addView(linearLayout, ViewParams(mainActivity, ViewParams.MATCH, ViewParams.MATCH).linear())

            when (position) {
                0 -> {

                    val wrecked = AppCompatTextView(mainActivity)
                    wrecked.text = getString(R.string.fragment_house_wrecked)
                    wrecked.setTextColor(mainActivity.colorResToColorInt(R.color.white))
                    wrecked.visibility = if (house.wrecked) View.VISIBLE else View.GONE
                    linearLayout.addView(wrecked, ViewParams(mainActivity).margins(8).linear())

                    val city = AppCompatTextView(mainActivity)
                    city.text = getString(R.string.fragment_house_capital, house.city)
                    city.setTextColor(mainActivity.colorResToColorInt(R.color.white))
                    city.visibility = if (house.city.isNotBlank()) View.VISIBLE else View.GONE
                    linearLayout.addView(city, ViewParams(mainActivity).margins(8).linear())

                    val devise = AppCompatTextView(mainActivity)
                    devise.text = getString(R.string.fragment_house_devise, house.devise)
                    devise.setTextColor(mainActivity.colorResToColorInt(R.color.white))
                    devise.visibility = if (house.devise.isNotBlank()) View.VISIBLE else View.GONE
                    linearLayout.addView(devise, ViewParams(mainActivity).margins(8).linear())

                    val proverb = AppCompatTextView(mainActivity)
                    proverb.text = getString(R.string.fragment_house_proverb, house.proverb)
                    proverb.setTextColor(mainActivity.colorResToColorInt(R.color.white))
                    proverb.visibility = if (house.proverb != null) View.VISIBLE else View.GONE
                    linearLayout.addView(proverb, ViewParams(mainActivity).margins(8).linear())
                }
                1 -> {

                    val characterAdapter = CharacterAdapter(mainActivity, R.layout.item_module)

                    characterAdapter.datas = DB.getCharacterDao().getByHouse(arrayListOf(house.id))
                    val recyclerView = RecyclerView(mainActivity)
                    recyclerView.init(3)
                    recyclerView.adapter = characterAdapter

                    linearLayout.addView(recyclerView)
                }
                2 -> {

                    val regionName = AppCompatTextView(mainActivity)
                    regionName.text = region.label
                    regionName.setTextColor(mainActivity.colorResToColorInt(R.color.white))
                    regionName.textSize = 16f
                    regionName.gravity = Gravity.CENTER
                    linearLayout.addView(regionName, ViewParams(mainActivity).margins(8).centerGravity().linear())

                    val map = AppCompatImageView(mainActivity)
                    map.setImageUrl(region.getMap())
                    linearLayout.addView(map, ViewParams(mainActivity, ViewParams.MATCH, ViewParams.MATCH).linear())
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
            badge.visibility = if (character.id.equals(house.lord)) View.VISIBLE else View.GONE
            badge.setColorFilter(mainActivity.colorResToColorInt(R.color.yellow))
            badge.setImageResource(R.drawable.ic_lord)

            val name = itemView.findViewById<AppCompatTextView>(R.id.name)
            name.text = character.name
        }

        override fun onItemClick(itemView: View, datas: List<Character>, position: Int) {

            val character = datas[position]

            val image = itemView.findViewById<AppCompatImageView>(R.id.image)

            mainActivity.setFragment(CharacterFragment.newInstance(character.id, (image.drawable as BitmapDrawable).bitmap), image)
        }

        override fun onItemLongClick(itemView: View, datas: List<Character>, position: Int) {}
    }
}