package bzh.zelyon.listdetail.models

import android.arch.persistence.room.PrimaryKey
import bzh.zelyon.listdetail.BuildConfig
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.RealmClass

@RealmClass
open class House: RealmObject() {

    companion object {

        const val URL = "api/got/house/"
    }

    @PrimaryKey
    @SerializedName("id")
    @Expose
    var id: Long = 0

    @SerializedName("label")
    @Expose
    var label: String = ""

    @SerializedName("city")
    @Expose
    var city: String? = null

    @SerializedName("region")
    @Expose
    var region: Long = 0

    @SerializedName("devise")
    @Expose
    var devise: String? = null

    @SerializedName("proverb")
    @Expose
    var proverb: String? = null

    @SerializedName("lord")
    @Expose
    var lord: Long? = null

    @SerializedName("wrecked")
    @Expose
    var wrecked: Boolean = false

    @SerializedName("blason")
    @Expose
    var blason: String = ""
        get() = BuildConfig.baseUrl + URL + "image/" + field

    var thumbnail: String = ""
        get() = blason.replace("image", "thumbnail")
}