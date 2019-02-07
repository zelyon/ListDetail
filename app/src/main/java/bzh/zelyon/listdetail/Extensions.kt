package bzh.zelyon.listdetail

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.ColorUtils
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.ImageView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

internal fun isNougat(): Boolean {

    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
}

internal fun RecyclerView.init(nbColumns: Int = 1) {

    setHasFixedSize(false)
    isNestedScrollingEnabled = false
    layoutManager = GridLayoutManager(context, nbColumns)
}

internal fun ImageView.setImageUrl(url: String, placeholder: Drawable? = null) {

    var requestCreator = Picasso.get().load(url)

    placeholder?.let {

        requestCreator = requestCreator.placeholder(placeholder)
    }

    requestCreator.into(this)
}

internal fun String.loadImageUrl(runnable: Runnable? = null) {

    Picasso.get().load(this).fetch(object : Callback {

        override fun onSuccess() {

            runnable?.run()
        }

        override fun onError(e: Exception) {

            runnable?.run()
        }
    })
}

internal fun Context.dpToPixel(int: Int): Int {

    return (int * this.resources.displayMetrics.density).toInt()
}

internal fun Context.drawableResToDrawable(@DrawableRes drawableRes: Int, @ColorRes colorRes: Int? = null): Drawable {

    val result = ContextCompat.getDrawable(this, drawableRes) ?: ColorDrawable()

    colorRes?.let {

        result.mutate().setColorFilter(this.colorResToColorInt(colorRes), PorterDuff.Mode.SRC_IN)
    }

    return result
}

internal fun Context.colorResToColorInt(@ColorRes colorRes: Int, alpha: Float? = null): Int {

    var result = ContextCompat.getColor(this, colorRes)

    alpha?.let {

        result = ColorUtils.setAlphaComponent(result, 255*alpha.toInt())
    }

    return result
}