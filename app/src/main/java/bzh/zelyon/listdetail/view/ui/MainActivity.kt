package bzh.zelyon.listdetail.view.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.os.Build
import android.os.Bundle
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import bzh.zelyon.lib.extension.isNougat
import bzh.zelyon.lib.extension.showFragment
import bzh.zelyon.lib.ui.view.activity.AbsActivity
import bzh.zelyon.listdetail.R
import bzh.zelyon.listdetail.api.API
import bzh.zelyon.listdetail.db.DB
import bzh.zelyon.listdetail.model.Character
import bzh.zelyon.listdetail.model.House
import bzh.zelyon.listdetail.model.Region
import bzh.zelyon.listdetail.view.callback.CallBack
import com.google.android.material.snackbar.Snackbar

class MainActivity : AbsActivity() {

    private var permissionRunnable: Runnable? = null
    private val fragmentDisplayed: Fragment?
        get() = supportFragmentManager.findFragmentById(R.id.content)

    override fun getLayoutId() = R.layout.activity_main

    override fun getFragmentContainerId() = R.id.content

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DB.init(this)

        val needLoadData= DB.getCharacterDao().getAll().isEmpty() || DB.getHouseDao().getAll().isEmpty() || DB.getRegionDao().getAll().isEmpty()

        ConnectionLiveData(this).observe(this, Observer { isOnline ->
            isOnline?.let {
                if (it) {
                    loadDatas()
                } else if (needLoadData) {
                    snackBar(getString(R.string.need_connexion))
                }
            }
        })

        if (fragmentDisplayed == null) {
            showFragment(if (needLoadData) LoadFragment() else MainFragment())
        }

        intent?.let {
            parseIntent(it)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let {
            parseIntent(it)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        var allGranted = true

        for (grantResult in grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) allGranted = false
        }

        if (allGranted) {
            permissionRunnable?.run()
            permissionRunnable = null
        }
    }

    fun snackBar(text: String) {
        runOnUiThread {
            Snackbar.make(findViewById<FrameLayout>(android.R.id.content), text, Snackbar.LENGTH_LONG).show()
        }
    }

    private fun loadDatas() {

        API.getCharacters(object : CallBack<List<Character>> {

            override fun onResult(result: List<Character>) {

                DB.getCharacterDao().insert(result)

                if (fragmentDisplayed is LoadFragment) {
                    (fragmentDisplayed as LoadFragment).loadImages()
                } else if (fragmentDisplayed is MainFragment) {
                    (fragmentDisplayed as MainFragment).loadCharacters()
                }
            }

            override fun onFail(throwable: Throwable) {
                snackBar(throwable.localizedMessage)
            }
        })

        API.getHouses(object : CallBack<List<House>> {

            override fun onResult(result: List<House>) {

                DB.getHouseDao().insert(result)

                if (fragmentDisplayed is LoadFragment) {
                    (fragmentDisplayed as LoadFragment).loadImages()
                } else if (fragmentDisplayed is MainFragment) {
                    (fragmentDisplayed as MainFragment).loadHouses()
                }
            }

            override fun onFail(throwable: Throwable) {
                snackBar(throwable.localizedMessage)
            }
        })

        API.getRegions(object : CallBack<List<Region>> {

            override fun onResult(result: List<Region>) {

                DB.getRegionDao().insert(result)

                if (fragmentDisplayed is LoadFragment) {
                    (fragmentDisplayed as LoadFragment).loadImages()
                }
            }

            override fun onFail(throwable: Throwable) {
                snackBar(throwable.localizedMessage)
            }
        })
    }

    private fun parseIntent(intent: Intent) {
        intent.data?.lastPathSegment?.toLong()?.let {
            showFragment(CharacterFragment.newInstance(it))
        }
    }

    class ConnectionLiveData(activity: MainActivity): LiveData<Boolean>() {

        private var connectivityManager: ConnectivityManager = activity.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager

        @RequiresApi(Build.VERSION_CODES.N)
        override fun onActive() {
            super.onActive()

            postValue(connectivityManager.activeNetworkInfo?.isConnected ?: false)

            if (isNougat()) {
                connectivityManager.registerDefaultNetworkCallback(connectivityManagerCallback)
            } else {
                connectivityManager.registerNetworkCallback(NetworkRequest.Builder().addTransportType(android.net.NetworkCapabilities.TRANSPORT_CELLULAR).addTransportType(android.net.NetworkCapabilities.TRANSPORT_WIFI).build(), connectivityManagerCallback)
            }
        }

        override fun onInactive() {
            super.onInactive()
            connectivityManager.unregisterNetworkCallback(connectivityManagerCallback)
        }

        private val connectivityManagerCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                postValue(true)
            }
            override fun onLost(network: Network) {
                postValue(false)
            }
        }
    }
}
