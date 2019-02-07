package bzh.zelyon.listdetail.utils

import android.content.Context
import bzh.zelyon.listdetail.models.Character
import bzh.zelyon.listdetail.models.House
import bzh.zelyon.listdetail.models.Region
import io.realm.Case
import io.realm.Realm
import io.realm.RealmConfiguration

class DB {

    companion object {

        fun init(context: Context) {

            Realm.init(context)

            val realmConfiguration = RealmConfiguration.Builder()
                .name("GoT.realm")
                .schemaVersion(1)
                .deleteRealmIfMigrationNeeded()
                .build()

            Realm.setDefaultConfiguration(realmConfiguration)
            Realm.compactRealm(realmConfiguration)
        }

        private val realm: Realm
            get() {

                val realm = Realm.getDefaultInstance()

                if (!realm.isInTransaction) {

                    realm.refresh()
                }

                return realm
            }

        fun getCharacters(): List<Character> {

            return realm.copyFromRealm(realm.where(Character::class.java).sort("id").findAll())
        }

        fun saveCharacters(characters: List<Character>) {

            realm.executeTransaction {

                it.delete(Character::class.java)
                it.insertOrUpdate(characters)
            }
        }

        fun getCharacterById(id: Long): Character {

            return realm.copyFromRealm(realm.where(Character::class.java).equalTo("id", id).findFirst())!!
        }

        fun getCharactersByFilters(name: String?, houses: Array<Long?>?, others:  Array<String?>?): List<Character> {

            val realmQuery = realm.where(Character::class.java)

            name?.let {

                if (name.isNotBlank()) {

                    realmQuery.contains("name", name, Case.INSENSITIVE)
                }
            }

            houses?.let {

                if (houses.isNotEmpty()) {

                    realmQuery.`in`("house", houses)
                }
            }

            others?.let {

                if (others.isNotEmpty()) {

                    if (others.contains(Character.GENDER_MALE) != others.contains(Character.GENDER_FEMALE)) {

                        realmQuery.equalTo("man", others.contains(Character.GENDER_MALE))
                    }

                    if (others.contains(Character.DEAD) != others.contains(Character.ALIVE)) {

                        realmQuery.equalTo("dead", others.contains(Character.DEAD))
                    }
                }
            }

            return realm.copyFromRealm(realmQuery.sort("id").findAll())
        }

        fun getHouses(): List<House> {

            return realm.copyFromRealm(realm.where(House::class.java).sort("id").findAll())
        }

        fun saveHouses(houses: List<House>) {

            realm.executeTransaction {

                it.delete(House::class.java)
                it.insertOrUpdate(houses)
            }
        }

        fun getHouseById(id: Long): House {

            return realm.copyFromRealm(realm.where(House::class.java).equalTo("id", id).findFirst())!!
        }

        fun getRegions(): List<Region> {

            return realm.copyFromRealm(realm.where(Region::class.java).sort("id").findAll())
        }

        fun saveRegions(regions: List<Region>) {

            realm.executeTransaction {

                it.delete(Region::class.java)
                it.insertOrUpdate(regions)
            }
        }

        fun getRegionById(id: Long): Region {

            return realm.copyFromRealm(realm.where(Region::class.java).equalTo("id", id).findFirst())!!
        }
    }
}