package bzh.zelyon.listdetail.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import bzh.zelyon.listdetail.model.Character
import bzh.zelyon.listdetail.model.House
import bzh.zelyon.listdetail.model.Region

@Database(entities = [Character::class, House::class, Region::class], version = 1)
abstract class DB: RoomDatabase() {

    companion object {

        private lateinit var db: DB

        fun init(context: Context) {
            db = Room.databaseBuilder(context, DB::class.java, "listdetail").allowMainThreadQueries().build()
        }

        fun getCharacterDao() = db.characterDao()

        fun getHouseDao() = db.houseDao()

        fun getRegionDao() = db.regionDao()
    }

    abstract fun characterDao(): Character.Dao
    abstract fun houseDao(): House.Dao
    abstract fun regionDao(): Region.Dao
}