package bzh.zelyon.listdetail.view.ui

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.View
import bzh.zelyon.lib.extension.actionFragment
import bzh.zelyon.lib.extension.setImage
import bzh.zelyon.lib.ui.view.fragment.AbsToolBarFragment
import bzh.zelyon.listdetail.R
import bzh.zelyon.listdetail.db.DB
import bzh.zelyon.listdetail.model.Character
import bzh.zelyon.listdetail.model.House
import bzh.zelyon.listdetail.util.share
import kotlinx.android.synthetic.main.fragment_character.*

class CharacterFragment: AbsToolBarFragment() {

    companion object {

        const val ID = "ID"
        const val PLACEHOLDER = "PLACEHOLDER"

        fun newInstance(id: Long, placeholder: Bitmap? = null) =
            CharacterFragment().apply {
                arguments = Bundle().apply {
                    putLong(ID, id)
                    putParcelable(PLACEHOLDER, placeholder)
                }
            }
    }

    private var character: Character? = null
    private var house: House? = null
    private var placeholder: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            character = DB.getCharacterDao().getById(it.getLong(ID))
            placeholder = it.getParcelable(PLACEHOLDER)
            house = DB.getHouseDao().getById(character?.house ?: 0)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        character?.let {
            fragment_character_image.transitionName = it.id.toString()
            fragment_character_image.setImage(it.getPicture(), if (placeholder != null) BitmapDrawable(absActivity.resources, placeholder) else null)
            fragment_character_description.visibility = if (it.description.isNotBlank()) View.VISIBLE else View.GONE
            fragment_character_description.text = it.description
            fragment_character_gender_icon.setImageResource(if (it.man) R.drawable.ic_male else R.drawable.ic_female)
            fragment_character_gender_label.text = getString(if (it.man) R.string.fragment_character_gender_man else R.string.fragment_character_gender_woman)
            fragment_character_status_icon.setImageResource(if (it.dead) R.drawable.ic_dead else R.drawable.ic_alive)
            fragment_character_status_label.text = getString(if (it.dead) if (it.man) R.string.fragment_character_dead_man else R.string.fragment_character_dead_woman else if (it.man) R.string.fragment_character_alive_man else R.string.fragment_character_alive_woman)
        }
        house?.let {
            fragment_character_house_layout.visibility = View.VISIBLE
            fragment_character_house_icon.transitionName = it.id.toString()
            fragment_character_house_icon.setImage(it.getThumbnail())
            fragment_character_house_label.text = it.label
        }
    }

    override fun onIdClick(id: Int) {
        super.onIdClick(id)
        when(id) {
            R.id.share -> character?.let { arrayListOf(it).share(absActivity) }
            R.id.fragment_character_house_layout, R.id.fragment_character_house_icon, R.id.fragment_character_house_label, R.id.fragment_character_house_open -> house?.let { absActivity.actionFragment(HouseFragment.newInstance(it.id, (fragment_character_house_icon.drawable as BitmapDrawable).bitmap), transitionView =  fragment_character_house_icon) }
        }
    }

    override fun getTitleToolBar()= character?.name.orEmpty()

    override fun showBack() = true

    override fun getIdLayout() = R.layout.fragment_character

    override fun getIdMenu() = R.menu.character

    override fun getIdToolbar() = R.id.fragment_character_toolbar
}