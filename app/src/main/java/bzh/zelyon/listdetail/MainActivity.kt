package bzh.zelyon.listdetail

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.FrameLayout
import bzh.zelyon.listdetail.fragments.LoadFragment
import bzh.zelyon.listdetail.fragments.MainFragment
import bzh.zelyon.listdetail.models.Character
import bzh.zelyon.listdetail.models.House
import bzh.zelyon.listdetail.models.Region
import bzh.zelyon.listdetail.utils.API
import bzh.zelyon.listdetail.utils.CallBack
import bzh.zelyon.listdetail.utils.DB

class MainActivity : AppCompatActivity() {

    private var permissionRunnable: Runnable? = null
    private val fragmentDisplayed: Fragment?
        get() = supportFragmentManager.findFragmentById(R.id.content)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        DB.init(this)

        val needLoadData= DB.getCharacterDao().getAll().isEmpty() || DB.getHouseDao().getAll().isEmpty() || DB.getRegionDao().getAll().isEmpty()

        ConnectionLiveData(this).observe(this, Observer { isOnline ->
            isOnline?.let {
                if (it) {
                    loadDatas()
                }
                else if (needLoadData) {
                    snackBar(getString(R.string.need_connexion))
                }
            }
        })

        if (fragmentDisplayed == null) {
            setFragment(if (needLoadData) LoadFragment() else MainFragment())
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        var allGranted = true

        for (grantResult in grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) allGranted = false
        }

        if(allGranted) {
            permissionRunnable?.run()
            permissionRunnable = null
        }
    }

    fun checkPermissions(runnable: Runnable, vararg permissions: String) {

        var allGranted = true

        for (permission in permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) allGranted = false
        }

        if (allGranted) {
            runnable.run()
        }
        else {
            permissionRunnable = runnable
            ActivityCompat.requestPermissions(this, permissions, 0)
        }
    }

    fun setFragment(fragment: Fragment, view: View? = null) {

        val fragmentTransaction = supportFragmentManager.beginTransaction().replace(R.id.content, fragment)
        fragmentDisplayed?.let {
            fragmentTransaction.addToBackStack(fragment::class.java.simpleName)
        }
        view?.let {
            fragmentTransaction.setReorderingAllowed(true).addSharedElement(view, view.transitionName)
        }
        fragmentTransaction.commit()
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
                }
                else if (fragmentDisplayed is MainFragment) {
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
                }
                else if (fragmentDisplayed is MainFragment) {
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

    class ConnectionLiveData(activity: MainActivity) : LiveData<Boolean>() {

        private var connectivityManager: ConnectivityManager = activity.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager

        override fun onActive() {
            super.onActive()

            postValue(connectivityManager.activeNetworkInfo?.isConnected ?: false)

            if (isNougat()) {
                connectivityManager.registerDefaultNetworkCallback(connectivityManagerCallback)
            }
            else {
                connectivityManager.registerNetworkCallback(NetworkRequest.Builder().addTransportType(android.net.NetworkCapabilities.TRANSPORT_CELLULAR).addTransportType(android.net.NetworkCapabilities.TRANSPORT_WIFI).build(), connectivityManagerCallback)
            }
        }

        override fun onInactive() {
            super.onInactive()
            connectivityManager.unregisterNetworkCallback(connectivityManagerCallback)
        }

        private val connectivityManagerCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network?) {
                postValue(true)
            }
            override fun onLost(network: Network?) {
                postValue(false)
            }
        }
    }
}
