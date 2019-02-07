package bzh.zelyon.listdetail

import android.support.multidex.MultiDexApplication
import bzh.zelyon.listdetail.utils.DB

class MainApplication : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()

        DB.init(this)
    }
}