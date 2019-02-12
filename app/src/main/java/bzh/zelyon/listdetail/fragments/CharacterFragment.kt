package bzh.zelyon.listdetail.fragments

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Menu
import android.view.View
import bzh.zelyon.listdetail.R
import bzh.zelyon.listdetail.models.Character
import bzh.zelyon.listdetail.models.House
import bzh.zelyon.listdetail.setImageUrl
import bzh.zelyon.listdetail.share
import bzh.zelyon.listdetail.utils.DB
import kotlinx.android.synthetic.main.fragment_character.*

class CharacterFragment: AbsToolBarFragment() {

    companion object {

        const val ID  = "ID"
        const val PLACEHOLDER  = "PLACEHOLDER"

        fun newInstance(id: Long, placeholder: BitmapDrawable) =

            CharacterFragment().apply {
                arguments = Bundle().apply {
                    putLong(ID, id)
                    putParcelable(PLACEHOLDER, placeholder.bitmap)
                }
            }
    }

    lateinit var character: Character
    lateinit var placeholder: Drawable

    var house: House? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedElementEnterTransition = android.transition.TransitionInflater.from(mainActivity).inflateTransition(R.transition.enter_transition)
        exitTransition = android.transition.TransitionInflater.from(mainActivity).inflateTransition(R.transition.exit_transition)
        postponeEnterTransition()
        startPostponedEnterTransition()

        arguments?.let {

            character = DB.getCharacterDao().getById(it.getLong(ID))
            placeholder = BitmapDrawable(mainActivity.resources, it.getParcelable(PLACEHOLDER) as Bitmap)
        }

        character.house?.let {

            house = DB.getHouseDao().getById(it)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        image.transitionName = character.id.toString()
        image.setImageUrl(character.getPicture(), placeholder)

        description.visibility = if (character.description.isNotBlank()) View.VISIBLE else View.GONE
        description.text = character.description

        gender_icon.setImageResource(if (character.man) R.drawable.ic_male else R.drawable.ic_female)

        gender_label.text = getString(if (character.man) R.string.fragment_character_gender_man else R.string.fragment_character_gender_woman)

        status_icon.setImageResource(if (character.dead) R.drawable.ic_dead else R.drawable.ic_alive)

        status_label.text = getString(if (character.dead) if (character.man) R.string.fragment_character_dead_man else R.string.fragment_character_dead_woman else if (character.man) R.string.fragment_character_alive_man else R.string.fragment_character_alive_woman)

        house?.let {

            house_layout.visibility = View.VISIBLE

            house_icon.transitionName = it.id.toString()
            house_icon.setImageUrl(it.getThumbnail())

            house_label.text = it.label
        }
    }

    override fun getLayoutId(): Int = R.layout.fragment_character

    override fun onIdClick(id: Int) {

        when(id) {

            R.id.share -> {

                arrayListOf(character).share(mainActivity)
            }

            R.id.house_layout -> {

                house?.let {

                    mainActivity.setFragment(HouseFragment.newInstance(it.id, house_icon.drawable as BitmapDrawable), house_icon)
                }
            }
        }
    }

    override fun getTitle(): String = character.name

    override fun showBack(): Boolean = true

    override fun getIdMenu(): Int = R.menu.character

    override fun onMenuCreated(menu: Menu?) { }
}