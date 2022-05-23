package bzh.zelyon.listdetail.model

import androidx.room.*
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import bzh.zelyon.listdetail.BuildConfig
import bzh.zelyon.listdetail.db.DB
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity(tableName = "character")
data class Character(
    @PrimaryKey @ColumnInfo(name = "id") @SerializedName("id") @Expose var id: Long = 0,
    @ColumnInfo(name = "house") @SerializedName("house") @Expose var house: Long? = null,
    @ColumnInfo(name = "name") @SerializedName("name") @Expose var name: String = "",
    @ColumnInfo(name = "description") @SerializedName("description") @Expose var description: String = "",
    @ColumnInfo(name = "dead") @SerializedName("dead") @Expose var dead: Boolean = false,
    @ColumnInfo(name = "man") @SerializedName("man") @Expose var man: Boolean = false,
    @ColumnInfo(name = "image") @SerializedName("image") @Expose var image: String = ""
) {

    @ColumnInfo(name = "position") var position: Long? = null

    fun getPicture() = BuildConfig.baseUrl + URL + "image/" + image
    fun getThumbnail() = BuildConfig.baseUrl + URL + "thumbnail/" + image

    companion object {
        const val URL = "api/got/character/"
        const val GENDER_MALE = "MALE"
        const val GENDER_FEMALE = "FEMALE"
        const val DEAD = "DEAD"
        const val ALIVE = "ALIVE"

        fun getByFilters(name: String? = null, houses: List<Long>? = null, man: Boolean? = null, dead: Boolean? = null): List<Character> {
            var query = "SELECT * FROM character "
            val args = mutableListOf<String>()
            val conditions= mutableListOf<String>()
            if (!name.isNullOrBlank()) {
                conditions.add("name LIKE '%' || ? || '%' ")
                args.add(name)
            }
            if (!houses.isNullOrEmpty()) {
                var housesCondition = "house IN ("
                houses.forEachIndexed { index, house ->
                    housesCondition += "?"
                    if (index < houses.size-1) {
                        housesCondition +=","
                    }
                    args.add(house.toString())
                }
                housesCondition += ") "
                conditions.add(housesCondition)
            }
            man?.let {
                conditions.add("man = ? ")
                args.add(if (it) "1" else "0")
            }
            dead?.let {
                conditions.add("dead = ? ")
                args.add(if (it) "1" else "0")
            }
            if (conditions.isNotEmpty()) {
                query += "WHERE "
                conditions.forEachIndexed { index, condition ->
                    query += condition
                    if (index < conditions.size-1) {
                        query +="AND "
                    }
                }
            }
            query += "ORDER BY position, id"
            return DB.getCharacterDao().getByFilters(SimpleSQLiteQuery(query, args.toTypedArray()))
        }
    }

    @androidx.room.Dao
    interface Dao {

        @Insert(onConflict = OnConflictStrategy.IGNORE)
        fun insert(characters: List<Character>)

        @Update
        fun update(characters: List<Character>)

        @Query("SELECT * FROM character ORDER BY position, id")
        fun getAll(): List<Character>

        @Query("SELECT * FROM character WHERE id = :id LIMIT 1")
        fun getById(id: Long): Character

        @RawQuery
        fun getByFilters(supportSQLiteQuery: SupportSQLiteQuery): List<Character>
    }
}