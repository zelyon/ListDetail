package bzh.zelyon.listdetail.view.ui

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.View
import bzh.zelyon.lib.extension.setImage
import bzh.zelyon.lib.extension.showFragment
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

        sharedElementEnterTransition = android.transition.TransitionInflater.from(absActivity).inflateTransition(R.transition.enter_transition)
        exitTransition = android.transition.TransitionInflater.from(absActivity).inflateTransition(R.transition.exit_transition)
        postponeEnterTransition()
        startPostponedEnterTransition()

        arguments?.let {
            character = DB.getCharacterDao().getById(it.getLong(ID))
            placeholder = it.getParcelable(PLACEHOLDER)
            house = DB.getHouseDao().getById(character?.house ?: 0)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        character?.let {
            image.transitionName = it.id.toString()
            image.setImage(it.getPicture(), if (placeholder != null) BitmapDrawable(absActivity.resources, placeholder) else null)
            description.visibility = if (it.description.isNotBlank()) View.VISIBLE else View.GONE
            description.text = it.description
            gender_icon.setImageResource(if (it.man) R.drawable.ic_male else R.drawable.ic_female)
            gender_label.text = getString(if (it.man) R.string.fragment_character_gender_man else R.string.fragment_character_gender_woman)
            status_icon.setImageResource(if (it.dead) R.drawable.ic_dead else R.drawable.ic_alive)
            status_label.text = getString(if (it.dead) if (it.man) R.string.fragment_character_dead_man else R.string.fragment_character_dead_woman else if (it.man) R.string.fragment_character_alive_man else R.string.fragment_character_alive_woman)
        }
        house?.let {
            house_layout.visibility = View.VISIBLE
            house_icon.transitionName = it.id.toString()
            house_icon.setImage(it.getThumbnail())
            house_label.text = it.label
        }
    }

    override fun onIdClick(id: Int) {
        when(id) {
            R.id.share -> character?.let { arrayListOf(it).share(absActivity) }
            R.id.house_layout, R.id.house_icon, R.id.house_label, R.id.house_open -> house?.let { absActivity.showFragment(HouseFragment.newInstance(it.id, (house_icon.drawable as BitmapDrawable).bitmap), transitionView =  house_icon) }
        }
    }

    override fun getTitleToolBar()= character?.name ?: ""

    override fun showBack() = true

    override fun getIdLayout() = R.layout.fragment_character

    override fun getIdMenu() = R.menu.character

    override fun getIdToolbar() = R.id.toolbar
}