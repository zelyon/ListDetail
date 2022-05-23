package bzh.zelyon.listdetail.model

import androidx.room.*
import bzh.zelyon.listdetail.BuildConfig
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity(tableName = "house")
data class House(
    @PrimaryKey @ColumnInfo(name = "id") @SerializedName("id") @Expose var id: Long = 0,
    @ColumnInfo(name = "label") @SerializedName("label") @Expose var label: String = "",
    @ColumnInfo(name = "city") @SerializedName("city") @Expose var city: String = "",
    @ColumnInfo(name = "region") @SerializedName("region") @Expose var region: Long = 0,
    @ColumnInfo(name = "devise") @SerializedName("devise") @Expose var devise: String = "",
    @ColumnInfo(name = "proverb") @SerializedName("proverb") @Expose var proverb: String? = null,
    @ColumnInfo(name = "lord") @SerializedName("lord") @Expose var lord: Long? = null,
    @ColumnInfo(name = "wrecked") @SerializedName("wrecked") @Expose var wrecked: Boolean = false,
    @ColumnInfo(name = "blason") @SerializedName("blason") @Expose var blason: String = ""
) {

    fun getImage() = BuildConfig.baseUrl + URL + "image/" + blason
    fun getThumbnail() = BuildConfig.baseUrl + URL + "thumbnail/" + blason

    companion object {
        const val URL = "api/got/house/"
    }

    @androidx.room.Dao
    interface Dao {

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        fun insert(houses: List<House>)

        @Query("SELECT * FROM house")
        fun getAll(): List<House>

        @Query("SELECT * FROM house WHERE id = :id LIMIT 1")
        fun getById(id: Long): House
    }
}