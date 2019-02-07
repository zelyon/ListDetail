package bzh.zelyon.listdetail.models

import android.arch.persistence.room.PrimaryKey
import bzh.zelyon.listdetail.BuildConfig
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.RealmClass

@RealmClass
open class Region: RealmObject(){

    companion object {

        const val URL = "api/got/region/"
    }

    @PrimaryKey
    @SerializedName("id")
    @Expose
    var id: Long = 0

    @SerializedName("label")
    @Expose
    var label: String = ""

    @SerializedName("image")
    @Expose
    var image: String = ""
        get() = BuildConfig.baseUrl + URL + "map/" + field
}