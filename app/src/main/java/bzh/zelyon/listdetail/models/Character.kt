package bzh.zelyon.listdetail.models

import android.arch.persistence.room.PrimaryKey
import bzh.zelyon.listdetail.BuildConfig
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.RealmClass

@RealmClass
open class Character: RealmObject() {

    companion object {

        const val URL = "api/got/character/"
        const val GENDER_MALE = "MALE"
        const val GENDER_FEMALE = "FEMALE"
        const val DEAD = "DEAD"
        const val ALIVE = "ALIVE"
    }

    @PrimaryKey
    @SerializedName("id")
    @Expose
    var id: Long = 0

    @SerializedName("house")
    @Expose
    var house: Long? = null

    @SerializedName("name")
    @Expose
    var name: String = ""

    @SerializedName("description")
    @Expose
    var description: String? = null

    @SerializedName("dead")
    @Expose
    var dead: Boolean = false

    @SerializedName("man")
    @Expose
    var man: Boolean = false

    @SerializedName("image")
    @Expose
    var image: String = ""
        get() = BuildConfig.baseUrl + URL + "image/" + field

    var thumbnail: String = ""
        get() = image.replace("image", "thumbnail")
}