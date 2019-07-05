package bzh.zelyon.listdetail.model

import androidx.room.*
import bzh.zelyon.listdetail.BuildConfig
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity(tableName = "region")
data class Region(
    @PrimaryKey @ColumnInfo(name = "id") @SerializedName("id") @Expose var id: Long = 0,
    @ColumnInfo(name = "label") @SerializedName("label") @Expose var label: String = "",
    @ColumnInfo(name = "image") @SerializedName("image") @Expose var image: String = ""
) {

    fun getMap() = BuildConfig.baseUrl + URL + "map/" + image

    companion object {
        const val URL = "api/got/region/"
    }

    @androidx.room.Dao
    interface Dao {

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        fun insert(regions: List<Region>)

        @Query("SELECT * " +
                "FROM region")
        fun getAll(): List<Region>

        @Query("SELECT * " +
                "FROM region " +
                "WHERE id = :id " +
                "LIMIT 1")
        fun getById(id: Long): Region
    }
}