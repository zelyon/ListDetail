package bzh.zelyon.listdetail.util

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.util.TypedValue
import android.widget.ProgressBar
import androidx.core.content.FileProvider
import bzh.zelyon.lib.extension.dpToPx
import bzh.zelyon.lib.extension.getImageAsBitmap
import bzh.zelyon.lib.ui.component.Popup
import bzh.zelyon.lib.ui.view.activity.AbsActivity
import bzh.zelyon.lib.util.Launch
import bzh.zelyon.listdetail.BuildConfig
import bzh.zelyon.listdetail.model.Character
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

internal fun List<Character>.share(absActivity: AbsActivity) {
    absActivity.launchPermissionFiles(BuildConfig.APPLICATION_ID) {
        if (it) {
            val padding = absActivity.dpToPx(64).toInt()
            val progressBar = ProgressBar(absActivity)
            progressBar.isIndeterminate = true
            progressBar.setPadding(0, padding, 0, padding)
            Popup(absActivity, customView = progressBar).show()

            val names = ArrayList<String>()
            val uris = ArrayList<Uri>()
            Observable.fromIterable(toMutableList())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(object : Observer<Character> {

                    override fun onSubscribe(d: Disposable) {}

                    override fun onNext(character: Character) {

                        names.add(character.name)
                        val file = File(absActivity.externalCacheDir, Uri.parse(character.getPicture()).lastPathSegment)
                        if (!file.exists()) {
                            absActivity.getImageAsBitmap(character.getPicture()).compress(Bitmap.CompressFormat.PNG, 100, FileOutputStream(file) as OutputStream)
                        }
                        uris.add(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) FileProvider.getUriForFile(absActivity.applicationContext, absActivity.applicationContext.packageName, file) else Uri.fromFile(file))
                    }

                    override fun onError(e: Throwable) {}

                    override fun onComplete() {

                        Popup.dismiss()

                        absActivity.startActivity(
                            Intent.createChooser(
                                Intent()
                                    .setAction(Intent.ACTION_SEND_MULTIPLE)
                                    .putExtra(Intent.EXTRA_TEXT, names.joinToString(separator = "\n"))
                                    .putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris)
                                    .setType("image/png")
                                    .setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION),
                                ""
                            )
                        )
                    }
                })
        }
    }
}

//day : black, night : white
internal fun Context.getTextColorPrimary(): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(android.R.attr.textColorPrimary, typedValue, true)
    return typedValue.resourceId
}