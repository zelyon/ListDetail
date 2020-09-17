package bzh.zelyon.listdetail.view.ui

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import bzh.zelyon.lib.extension.showFragment
import bzh.zelyon.lib.ui.view.fragment.AbsFragment
import bzh.zelyon.listdetail.R
import bzh.zelyon.listdetail.db.DB
import bzh.zelyon.listdetail.util.loadImageUrl
import kotlinx.android.synthetic.main.fragment_load.*
import java.util.concurrent.Semaphore

class LoadFragment: AbsFragment() {

    private var animatedVectorDrawableCompat: AnimatedVectorDrawableCompat? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        animatedVectorDrawableCompat = AnimatedVectorDrawableCompat.create(absActivity, R.drawable.anim_vector_loader)?.apply {
            registerAnimationCallback(object : Animatable2Compat.AnimationCallback() {
                override fun onAnimationEnd(drawable: Drawable?) {
                    super.onAnimationEnd(drawable)
                    start()
                }
            })
        }
        loader.setImageDrawable(animatedVectorDrawableCompat)
        progress_bar.progress = 0
    }

    override fun getIdLayout() = R.layout.fragment_load

    override fun onIdClick(id: Int) {
        when(id) {
            R.id.skip -> absActivity.showFragment(MainFragment())
        }
    }

    fun loadImages() {
        val characters = DB.getCharacterDao().getAll()
        val houses = DB.getHouseDao().getAll()
        val regions = DB.getRegionDao().getAll()
        if (characters.isNotEmpty() && houses.isNotEmpty() && regions.isNotEmpty()) {
            animatedVectorDrawableCompat?.start()
            skip.visibility = View.VISIBLE
            val imagesUrlsMandatory = ArrayList<String>()
            val imagesUrls = ArrayList<String>()
            for (character in characters) {
                imagesUrlsMandatory.add(character.getThumbnail())
                imagesUrls.add(character.getPicture())
            }
            for (house in houses) {
                imagesUrlsMandatory.add(house.getThumbnail())
                imagesUrls.add(house.getImage())
            }
            for (region in regions) {
                imagesUrls.add(region.getMap())
            }
            progress_bar.max = imagesUrlsMandatory.size
            val semaphore = Semaphore(0)
            for (imageUrl in imagesUrlsMandatory) {
                imageUrl.loadImageUrl(Runnable {
                    semaphore.release()
                    if (isAdded) {
                        absActivity.runOnUiThread {
                            progress_bar.progress++
                            if (semaphore.tryAcquire(imagesUrlsMandatory.size)) {
                                absActivity.showFragment(MainFragment())
                            }
                        }
                    }
                })
            }
            for (imageUrl in imagesUrls) {
                imageUrl.loadImageUrl()
            }
        }
    }
}