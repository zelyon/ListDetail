package bzh.zelyon.listdetail

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Build
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v4.graphics.ColorUtils
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.ImageView
import bzh.zelyon.listdetail.models.Character
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.io.FileOutputStream

internal fun isNougat(): Boolean {

    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
}

internal fun List<Character>.share(mainActivity: MainActivity) {

    mainActivity.checkPermissions(Runnable {

        val names = ArrayList<String>()
        val uris = ArrayList<Uri>()

        Observable.fromIterable(ArrayList<Character>(this))
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe(object : Observer<Character> {

                override fun onSubscribe(d: Disposable) {}

                override fun onNext(character: Character) {

                    names.add(character.name)

                    val file = File(mainActivity.externalCacheDir, Uri.parse(character.getPicture()).lastPathSegment)

                    if(!file.exists()) {

                        Picasso.get().load(character.getPicture()).placeholder(GradientDrawable()).get().compress(Bitmap.CompressFormat.PNG, 100, FileOutputStream(file))
                    }

                    if(isNougat()) {

                        uris.add(FileProvider.getUriForFile(mainActivity.applicationContext, mainActivity.applicationContext.packageName, file))
                    }
                    else {

                        uris.add(Uri.fromFile(file))
                    }
                }

                override fun onError(e: Throwable) {}

                override fun onComplete() {

                    mainActivity.startActivity(
                        Intent.createChooser(
                            Intent()
                                .setAction(Intent.ACTION_SEND_MULTIPLE)
                                .putExtra(Intent.EXTRA_TEXT, names.joinToString(separator = "\n"))
                                .putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris)
                                .setType("image/png")
                                .setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION),
                            mainActivity.getString(R.string.popup_share, "")
                        )
                    )
                }
            })
    }, Manifest.permission.WRITE_EXTERNAL_STORAGE)
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