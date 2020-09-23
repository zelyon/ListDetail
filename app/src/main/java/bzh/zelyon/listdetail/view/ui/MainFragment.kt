package bzh.zelyon.listdetail.view.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.SharedPreferences
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.preference.PreferenceManager
import android.transition.TransitionInflater
import android.view.View
import android.view.ViewAnimationUtils
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import bzh.zelyon.lib.extension.*
import bzh.zelyon.lib.ui.component.CollectionsView
import bzh.zelyon.lib.ui.component.FilterView
import bzh.zelyon.lib.ui.view.fragment.AbsToolBarFragment
import bzh.zelyon.listdetail.R
import bzh.zelyon.listdetail.db.DB
import bzh.zelyon.listdetail.model.Character
import bzh.zelyon.listdetail.model.House
import bzh.zelyon.listdetail.util.getTextColorPrimary
import bzh.zelyon.listdetail.util.share
import com.bumptech.glide.Glide
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_main.*
import kotlin.math.sqrt

class MainFragment: AbsToolBarFragment() {

    companion object {
        const val LIST = "LIST"
        const val SEARCH_APPLY_SAVE = "SEARCH_APPLY_SAVE"
        const val HOUSES_APPLY_SAVE = "HOUSES_APPLY_SAVE"
        const val OTHERS_APPLY_SAVE = "OTHERS_APPLY_SAVE"
    }

    private var sharedPreferences: SharedPreferences? = null
    private var modeList = true

    private var selectedCharacters: ArrayList<Character> = ArrayList()
    private var searchApply = ""
    private var housesApply = listOf<Long>()
    private var othersApply = listOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedElementEnterTransition = TransitionInflater.from(absActivity).inflateTransition(R.transition.enter_transition)
        exitTransition = TransitionInflater.from(absActivity).inflateTransition(R.transition.exit_transition)
        postponeEnterTransition()
        startPostponedEnterTransition()

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(absActivity)
        modeList = sharedPreferences?.getBoolean(LIST, true) ?: true

        savedInstanceState?.let {
            searchApply = it.getString(SEARCH_APPLY_SAVE) ?: ""
            housesApply = it.getLongArray(HOUSES_APPLY_SAVE)?.toList() ?: listOf()
            othersApply = it.getStringArray(OTHERS_APPLY_SAVE)?.toList() ?: listOf()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(SEARCH_APPLY_SAVE, searchApply)
        outState.putLongArray(HOUSES_APPLY_SAVE, housesApply.toLongArray())
        outState.putStringArray(OTHERS_APPLY_SAVE, othersApply.toTypedArray())
        super.onSaveInstanceState(outState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        items_view.nbColumns = if (modeList) 1 else 3
        items_view.idLayoutItem = if (modeList) R.layout.item_list else R.layout.item_module
        items_view.dragNDropEnable = false
        items_view.helper = object : CollectionsView.Helper() {
            override fun onBindItem(itemView: View, items: MutableList<*>, position: Int) {
                val character = items[position]
                if (character is Character) {
                    val characterIsSelected = selectedCharacters.contains(character)
                    val image= itemView.findViewById<AppCompatImageView>(R.id.image)
                    image.visibility = if (modeList && characterIsSelected) View.GONE else View.VISIBLE
                    image.scaleX = if (!modeList && characterIsSelected) .8f else 1f
                    image.scaleY = if (!modeList && characterIsSelected) .8f else 1f
                    image.transitionName = character.id.toString()
                    image.setImage(character.getThumbnail())
                    val badge = itemView.findViewById<AppCompatImageView>(R.id.badge)
                    badge.visibility = if (modeList && characterIsSelected || !modeList && !selectedCharacters.isEmpty()) View.VISIBLE else View.GONE
                    badge.setImageResource(if (characterIsSelected) R.drawable.ic_check else R.drawable.ic_uncheck)
                    badge.setColorFilter(absActivity.colorResToColorInt(if (characterIsSelected) R.color.green else if (modeList) R.color.black else R.color.white))
                    itemView.findViewById<AppCompatTextView>(R.id.name).text = character.name
                }
            }

            override fun onItemClick(itemView: View, items: MutableList<*>, position: Int) {
                val character= items[position]
                if (character is Character) {
                    if (action_mode_toolbar.visibility == View.GONE) {
                        val image= itemView.findViewById<AppCompatImageView>(R.id.image)
                        absActivity.showFragment(CharacterFragment.newInstance(character.id, (image.drawable as BitmapDrawable).bitmap), transitionView = image)
                    } else {
                        selectCharacter(itemView, character)
                    }
                }
            }

            override fun onItemLongClick(itemView: View, items: MutableList<*>, position: Int) {
                val character= items[position]
                if (character is Character) {
                    selectCharacter(itemView, character)
                }
            }

            override fun onItemStartDrag(itemView: View, items: MutableList<*>, position: Int) {
                itemView.animate().scaleY(0.8f).scaleX(0.8f).alpha(0.8f).duration = 200L
            }

            override fun onItemEndDrag(itemView: View, items: MutableList<*>, position: Int) {
                itemView.animate().scaleY(1f).scaleX(1f).alpha(1f).duration = 200L
            }

            override fun onItemSwipe(itemView: View, items: MutableList<*>, position: Int) {
                for (i in items.indices) {
                    val character = items[i]
                    if (character is Character) {
                        character.position = i.toLong()
                    }
                }
                DB.getCharacterDao().update(items as List<Character>)
            }

            private fun selectCharacter(itemView: View, character: Character) {
                if (selectedCharacters.contains(character)) {
                    selectedCharacters.remove(character)
                } else {
                    selectedCharacters.add(character)
                }
                if (modeList) {
                    showActionMode(selectedCharacters.isNotEmpty())
                } else {
                    val scale = if (selectedCharacters.contains(character)) .8f else 1f
                    itemView.findViewById<AppCompatImageView>(R.id.image).animate().scaleY(scale).scaleX(scale).setDuration(200L).withEndAction {
                        showActionMode(selectedCharacters.isNotEmpty())
                    }
                }
            }
        }
        action_mode_toolbar.setNavigationIcon(R.drawable.ic_close)
        action_mode_toolbar.inflateMenu(R.menu.character)
        action_mode_toolbar.setNavigationOnClickListener {
            showActionMode(false)
        }
        action_mode_toolbar.setOnMenuItemClickListener {
            selectedCharacters.share(absActivity)
            showActionMode(false)
            false
        }
        filter_toolbar.setNavigationIcon(R.drawable.ic_close)
        filter_toolbar.inflateMenu(R.menu.filter)
        filter_toolbar.setNavigationOnClickListener {
            showSearchAndFilter(false)
        }
        filter_toolbar.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.restore -> {
                    search_view.setQuery(searchApply, false)
                    house_filter_view.restore()
                    other_filter_view.restore()
                }
                R.id.clear -> {
                    search_view.setQuery("", false)
                    house_filter_view.clear()
                    other_filter_view.clear()
                }
            }
            false
        }
        search_view.setQuery(searchApply, false)
        loadCharacters()
        loadHouses()
        loadOthers()
    }

    override fun onIdClick(id: Int) {
        when (id) {
            R.id.close -> {
                search_view.setQuery(searchApply, false)
                house_filter_view.restore()
                other_filter_view.restore()
                showSearchAndFilter(false)
            }
            R.id.valid -> {
                searchApply = search_view.query.toString()
                housesApply = (house_filter_view as FilterView<Long>).getSelectedAndApply()
                othersApply = (other_filter_view as FilterView<String>).getSelectedAndApply()
                loadCharacters()
                showSearchAndFilter(false)
            }
            R.id.mode -> {
                modeList = !modeList
                sharedPreferences?.edit()?.putBoolean(LIST, modeList)?.apply()
                items_view.nbColumns = if (modeList) 1 else 3
                items_view.idLayoutItem = if (modeList) R.layout.item_list else R.layout.item_module
                onUpdateMenu()
            }
            R.id.search -> showSearchAndFilter(true)
        }
    }

    override fun getTitleToolBar()= getString(R.string.fragment_main_title)

    override fun getIdLayout() = R.layout.fragment_main

    override fun getIdMenu() = R.menu.main

    override fun getIdToolbar() = R.id.toolbar

    override fun onUpdateMenu() {
        menu?.findItem(R.id.mode)?.setIcon(if (modeList) R.drawable.ic_module else R.drawable.ic_list)
    }

    fun loadCharacters() {
        val man = othersApply.contains(Character.GENDER_MALE)
        val female = othersApply.contains(Character.GENDER_FEMALE)
        val dead = othersApply.contains(Character.DEAD)
        val alive = othersApply.contains(Character.ALIVE)
        items_view.items = Character.getByFilters(searchApply, housesApply, if (man != female) man else null, if (dead != alive) dead else null).toMutableList()
    }

    fun loadHouses() {

        val items = mutableListOf<FilterView.Item<Long>>()

        Observable.fromIterable(DB.getHouseDao().getAll())
            .subscribeOn(AndroidSchedulers.mainThread())
            .observeOn(Schedulers.io())
            .subscribe(object : Observer<House> {

                override fun onSubscribe(d: Disposable) {}

                override fun onNext(house: House) {
                    items.add(
                        FilterView.Item(
                            house.id,
                            house.label,
                            BitmapDrawable(resources, Glide.with(absActivity)
                                .asBitmap()
                                .load(house.getThumbnail())
                                .submit()
                                .get())
                        )
                    )
                }

                override fun onError(e: Throwable) {}

                override fun onComplete() {
                    absActivity.runOnUiThread {
                        (house_filter_view as FilterView<Long>).load(items, housesApply)
                    }
                }
            })
    }

    private fun loadOthers() {
        (other_filter_view as FilterView<String>).load(listOf(
            FilterView.Item(Character.GENDER_MALE, getString(R.string.fragment_character_gender_man), absActivity.drawableResToDrawable(R.drawable.ic_male, absActivity.getTextColorPrimary())),
            FilterView.Item(Character.GENDER_FEMALE, getString(R.string.fragment_character_gender_woman), absActivity.drawableResToDrawable( R.drawable.ic_female, absActivity.getTextColorPrimary())),
            FilterView.Item(Character.ALIVE, getString(R.string.fragment_character_alive_man), absActivity.drawableResToDrawable(R.drawable.ic_alive, absActivity.getTextColorPrimary())),
            FilterView.Item(Character.DEAD, getString(R.string.fragment_character_dead_man), absActivity.drawableResToDrawable(R.drawable.ic_dead, absActivity.getTextColorPrimary()))
        ), othersApply)
    }

    private fun showSearchAndFilter(show: Boolean) {
        filter_layout.visibility = View.VISIBLE
        val radius = sqrt((filter_layout.height * filter_layout.height + filter_layout.height * filter_layout.height).toDouble()).toFloat()
        val circularReveal = ViewAnimationUtils.createCircularReveal(filter_layout, filter_layout.height,0, if (show) 0f else radius, if (show) radius else 0f).setDuration(600)
        circularReveal.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                filter_layout.visibility = if (show) View.VISIBLE else View.INVISIBLE
            }
        })
        circularReveal.start()
    }

    private fun showActionMode(show: Boolean) {
        action_mode_toolbar.visibility = if (show) View.VISIBLE else View.GONE
        if (show) {
            action_mode_toolbar.title = selectedCharacters.size.toString()
        } else {
            selectedCharacters.clear()
        }
        items_view.refresh()
    }
}