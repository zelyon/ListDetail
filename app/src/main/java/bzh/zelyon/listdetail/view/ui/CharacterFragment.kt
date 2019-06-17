package bzh.zelyon.listdetail.view.ui

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.View
import bzh.zelyon.listdetail.R
import bzh.zelyon.listdetail.model.Character
import bzh.zelyon.listdetail.model.House
import bzh.zelyon.listdetail.util.setImageUrl
import bzh.zelyon.listdetail.util.share
import bzh.zelyon.listdetail.db.DB
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

    var character: Character? = null
    var house: House? = null
    var placeholder: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedElementEnterTransition = android.transition.TransitionInflater.from(mainActivity).inflateTransition(R.transition.enter_transition)
        exitTransition = android.transition.TransitionInflater.from(mainActivity).inflateTransition(R.transition.exit_transition)
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
            image.setImageUrl(it.getPicture(), if (placeholder != null) BitmapDrawable(mainActivity.resources, placeholder) else null)
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
            house_icon.setImageUrl(it.getThumbnail())
            house_label.text = it.label
        }
    }

    override fun getLayoutId() = R.layout.fragment_character

    override fun onIdClick(id: Int) {
        when(id) {
            R.id.share -> character?.let { arrayListOf(it).share(mainActivity) }
            R.id.house_layout -> house?.let { mainActivity.setFragment(HouseFragment.newInstance(it.id, (house_icon.drawable as BitmapDrawable).bitmap), house_icon) }
        }
    }

    override fun getTitle() = character?.name ?: ""

    override fun showBack() = true

    override fun getIdMenu() = R.menu.character

    override fun onMenuCreated() { }
}