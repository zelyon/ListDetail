package bzh.zelyon.listdetail

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.graphics.Bitmap
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Vibrator
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v4.graphics.ColorUtils
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.webkit.MimeTypeMap
import android.widget.ImageView
import bzh.zelyon.listdetail.models.Character
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.net.URL

internal fun isNougat() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N

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
                    if (!file.exists()) {
                        Picasso.get().load(character.getPicture()).placeholder(GradientDrawable()).get().compress(Bitmap.CompressFormat.PNG, 100, FileOutputStream(file))
                    }
                    uris.add(if (isNougat()) FileProvider.getUriForFile(mainActivity.applicationContext, mainActivity.applicationContext.packageName, file) else Uri.fromFile(file))
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

internal fun Context.dpToPixel(int: Int) = (int * this.resources.displayMetrics.density).toInt()

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

internal fun Context.openKeyBoard(view: View? = null) {
    view?.requestFocus()
    (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
}

internal fun Activity.closeKeyBoard(view: View? = null) {
    (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow((view ?: window.decorView.rootView).windowToken, 0)
}

internal fun Context.vibrate() {
    (getSystemService(Context.VIBRATOR_SERVICE) as Vibrator).vibrate(300L)
}

internal fun View.vibrate(step: Int = 0) {
    val translations = arrayOf(0F, 25F, -25F, 25F, -25F, 15F, -15F, 5F, -5F, 0F)
    animate().translationX(translations[step]).setDuration(100L).withEndAction {
        if (step < translations.size-2) {
            vibrate(step + 1)
        }
    }.start()
}

internal fun Activity.goStore() {
    startActivity(Intent(ACTION_VIEW, Uri.parse("market://details?id="+BuildConfig.APPLICATION_ID)))
}

internal fun MainActivity.openCamera() {
    checkPermissions(Runnable {
        startActivityForResult(Intent(MediaStore.ACTION_IMAGE_CAPTURE), 0)
    }, Manifest.permission.CAMERA)
}

internal fun MainActivity.openGallery(multiple: Boolean = false) {
    checkPermissions(Runnable {
        startActivityForResult(Intent(Intent.ACTION_GET_CONTENT).setType("image/*").putExtra(Intent.EXTRA_ALLOW_MULTIPLE, multiple), 0)
    }, Manifest.permission.WRITE_EXTERNAL_STORAGE)
}

internal fun Context.getBitmapFromCameraUri(uri: Uri) = MediaStore.Images.Media.getBitmap(contentResolver, uri)

internal fun Context.getLocalFileFromGalleryUri(uri: Uri): File {

    val isDocumentUri = DocumentsContract.isDocumentUri(this, uri)
    val isExternalStorage = isDocumentUri && uri.authority == "com.android.externalstorage.documents" && DocumentsContract.getDocumentId(uri).split(":")[0] == "primary"
    val isDownloadStorage = isDocumentUri && uri.authority == "com.android.providers.downloads.documents"
    val isImageStorage = isDocumentUri && uri.authority == "com.android.providers.media.documents" && DocumentsContract.getDocumentId(uri).split(":")[0] == "image"
    val isFile = uri.scheme == ContentResolver.SCHEME_FILE
    val isContent = uri.scheme == ContentResolver.SCHEME_CONTENT
    val isGooglePhotos = isContent && uri.authority == "com.google.android.apps.photos.content"
    val isContentStorage = isContent && uri.path?.contains("/storage") ?: false

    val path = when {
        isExternalStorage -> Environment.getExternalStorageDirectory().absolutePath + "/" + DocumentsContract.getDocumentId(uri).split(":")[1]
        isDownloadStorage -> getPathFromCursor(ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), DocumentsContract.getDocumentId(uri).toLong()), arrayOf(MediaStore.Images.Media.DATA))
        isImageStorage -> getPathFromCursor(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, arrayOf(MediaStore.Images.Media.DATA), MediaStore.Images.Media._ID + "=?", arrayOf(DocumentsContract.getDocumentId(uri).split(":")[1]))
        isFile -> uri.path
        isGooglePhotos -> uri.lastPathSegment
        isContentStorage -> uri.path?.substring(uri.path?.indexOf("/storage") ?: 0)
        isContent -> getPathFromCursor(uri, arrayOf(MediaStore.Images.Media.DATA))
        else -> null
    }
    var name = System.currentTimeMillis().toString().plus(".").plus(getExtension(if (path != null) Uri.parse(path) else uri))
    val cursor = contentResolver.query(uri, null, null, null, null)
    if (cursor?.moveToFirst() == true) {
        name = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)) ?: name
    }
    cursor?.close()

    /*
    if (DocumentsContract.isDocumentUri(this, uri)) {
        val documentId = DocumentsContract.getDocumentId(uri)
        val documentIdTab = documentId.split(":")
        if (uri.authority == "com.android.externalstorage.documents" && documentIdTab[0] == "primary") {
            path = Environment.getExternalStorageDirectory().absolutePath + "/" + documentIdTab[1]
        } else if (uri.authority == "com.android.providers.downloads.documents") {
            contentResolver.query(ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), documentId.toLong()), arrayOf("_data"), null, null, null)?.let {
                it.moveToFirst()
                path = it.getString(it.getColumnIndexOrThrow("_data"))
                it.close()
            }
        } else if (uri.authority == "com.android.providers.media.documents" && "image" == documentIdTab[0]) {
            contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, arrayOf("_data"), "_id=?", arrayOf(documentIdTab[1]), null)?.let {
                it.moveToFirst()
                path = it.getString(it.getColumnIndexOrThrow("_data"))
                it.close()
            }
        }
    }
    if (path == null) {
        if (uri.scheme == ContentResolver.SCHEME_FILE) {
            path = uri.path
            name = uri.lastPathSegment
        } else if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            if (uri.authority == "com.google.android.apps.photos.content") {
                path = uri.lastPathSegment
            } else if (uri.path.contains("/storage")) {
                path = uri.path.substring(uri.path.indexOf("/storage"))
            }

            if (path == null) {
                contentResolver.query(uri, arrayOf("_data"), null, null, null)?.let {
                    it.moveToFirst()
                    path = it.getString(it.getColumnIndexOrThrow("_data"))
                    it.close()
                }
            }
        }
    }
    contentResolver.query(uri, null, null, null, null)?.let {
        it.moveToFirst()
        name = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
        it.close()
    }
    */

    val outputFile = File(externalCacheDir, name)
    when {
        path != null -> FileInputStream(File(path))
        uri.toString().startsWith("http") -> URL(uri.toString()).openStream()
        else -> contentResolver.openInputStream(uri)
    }.use {input ->
        FileOutputStream(outputFile).use { output ->
            input.copyTo(output)
        }
    }

    return File(outputFile.absolutePath)
}

internal fun Context.getPathFromCursor(uri: Uri, projection: Array<String>? = null, selection: String? = null, selectionArgs: Array<String>? = null): String? {
    var result:String? = null
    val cursor = contentResolver.query(uri, projection, selection, selectionArgs, null)
    if (cursor?.moveToFirst() == true) {
        result = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
    }
    cursor?.close()
    return result
}

internal fun Context.getExtension(uri: Uri) = if (uri.scheme == ContentResolver.SCHEME_CONTENT) MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri)) else MimeTypeMap.getFileExtensionFromUrl(uri.path)