package bzh.zelyon.listdetail.models

import android.arch.persistence.room.*
import bzh.zelyon.listdetail.BuildConfig
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

    fun getPicture() = BuildConfig.baseUrl + URL + "image/" + image
    fun getThumbnail() = BuildConfig.baseUrl + URL + "thumbnail/" + image

    companion object {

        const val URL = "api/got/character/"
        const val GENDER_MALE = "MALE"
        const val GENDER_FEMALE = "FEMALE"
        const val DEAD = "DEAD"
        const val ALIVE = "ALIVE"
    }

    @android.arch.persistence.room.Dao
    interface Dao {

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        fun insert(characters: List<Character>)

        @Query("SELECT * FROM character")
        fun getAll(): List<Character>

        @Query("SELECT * FROM character WHERE id = :id LIMIT 1")
        fun getById(id: Long) : Character

        @Query("SELECT * FROM character WHERE name LIKE '%' || :name || '%'")
        fun getByName(name: String) : List<Character>

        @Query("SELECT * FROM character WHERE house IN (:houses)")
        fun getByHouse(houses: List<Long>) : List<Character>

        @Query("SELECT * FROM character WHERE dead = :dead")
        fun getByStatus(dead: Boolean) : List<Character>

        @Query("SELECT * FROM character WHERE man = :man")
        fun getByGender(man: Boolean) : List<Character>

        @Query("SELECT * FROM character WHERE name LIKE '%' || :name || '%' AND house IN (:houses)")
        fun getByNameAndHouse(name: String, houses: List<Long>) : List<Character>

        @Query("SELECT * FROM character WHERE name LIKE '%' || :name || '%' AND man = :man")
        fun getByNameAndGender(name: String, man: Boolean) : List<Character>

        @Query("SELECT * FROM character WHERE name LIKE '%' || :name || '%' AND dead = :dead")
        fun getByNameAndStatus(name: String, dead: Boolean) : List<Character>

        @Query("SELECT * FROM character WHERE house IN (:houses) AND man = :man")
        fun getByHouseAndGender(houses: List<Long>, man: Boolean) : List<Character>

        @Query("SELECT * FROM character WHERE house IN (:houses) AND dead = :dead")
        fun getByHouseAndStatus(houses: List<Long>, dead: Boolean) : List<Character>

        @Query("SELECT * FROM character WHERE  man = :man AND dead = :dead")
        fun getByGenderAndStatus(man: Boolean, dead: Boolean) : List<Character>

        @Query("SELECT * FROM character WHERE name LIKE '%' || :name || '%' AND house IN (:houses) AND man = :man")
        fun getByNameAndHouseAndGender(name: String, houses: List<Long>, man: Boolean) : List<Character>

        @Query("SELECT * FROM character WHERE name LIKE '%' || :name || '%' AND house IN (:houses) AND dead = :dead")
        fun getByNameAndHouseAndStatus(name: String, houses: List<Long>, dead: Boolean) : List<Character>

        @Query("SELECT * FROM character WHERE name LIKE '%' || :name || '%' AND man = :man AND dead = :dead")
        fun getByNameAndGenderAndStatus(name: String, man: Boolean, dead: Boolean) : List<Character>

        @Query("SELECT * FROM character WHERE house IN (:houses) AND man = :man AND dead = :dead")
        fun getByHouseAndGenderAndStatus(houses: List<Long>, man: Boolean, dead: Boolean) : List<Character>

        @Query("SELECT * FROM character WHERE name LIKE '%' || :name || '%' AND house IN (:houses) AND man = :man AND dead = :dead")
        fun getByNameAndHouseAndGenderAndStatus(name: String, houses: List<Long>, man: Boolean, dead: Boolean) : List<Character>
    }
}