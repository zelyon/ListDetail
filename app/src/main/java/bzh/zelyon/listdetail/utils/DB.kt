package bzh.zelyon.listdetail.utils

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import bzh.zelyon.listdetail.models.Character
import bzh.zelyon.listdetail.models.House
import bzh.zelyon.listdetail.models.Region

@Database(entities = [Character::class, House::class, Region::class], version = 1)
abstract class DB: RoomDatabase() {

    companion object {

        fun init(context: Context) {

            INSTANCE = Room.databaseBuilder(context, DB::class.java, "listdetail").allowMainThreadQueries().build()
        }

        private lateinit var INSTANCE: DB

        fun getCharacterDao(): Character.Dao {

            return INSTANCE.characterDao()
        }

        fun getHouseDao(): House.Dao {

            return INSTANCE.houseDao()
        }

        fun getRegionDao(): Region.Dao {

            return INSTANCE.regionDao()
        }
    }

    abstract fun characterDao(): Character.Dao
    abstract fun houseDao(): House.Dao
    abstract fun regionDao(): Region.Dao
}