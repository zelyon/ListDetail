package bzh.zelyon.listdetail.utils

import bzh.zelyon.listdetail.BuildConfig
import bzh.zelyon.listdetail.models.Character
import bzh.zelyon.listdetail.models.House
import bzh.zelyon.listdetail.models.Region
import com.google.gson.GsonBuilder
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface API {

    companion object {

        private val API = Retrofit.Builder()
            .baseUrl(BuildConfig.baseUrl)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .client(OkHttpClient.Builder().build())
            .build()
            .create(API::class.java)

        fun getCharacters(callBack: CallBack<List<Character>>) {
            API.getCharacters().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(object: DisposableSingleObserver<List<Character>>() {
                override fun onSuccess(characters: List<Character>) {
                    callBack.onResult(characters)
                }
                override fun onError(throwable: Throwable) {
                    callBack.onFail(throwable)
                }
            })
        }

        fun getHouses(callBack: CallBack<List<House>>) {
            API.getHouses().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(object: DisposableSingleObserver<List<House>>() {
                override fun onSuccess(houses: List<House>) {
                    callBack.onResult(houses)
                }
                override fun onError(throwable: Throwable) {
                    callBack.onFail(throwable)
                }
            })
        }

        fun getRegions(callBack: CallBack<List<Region>>) {
            API.getRegions().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(object: DisposableSingleObserver<List<Region>>() {
                override fun onSuccess(regions: List<Region>) {
                    callBack.onResult(regions)
                }
                override fun onError(throwable: Throwable) {
                    callBack.onFail(throwable)
                }
            })
        }
    }

    @GET(Character.URL)
    fun getCharacters(): Single<List<Character>>

    @GET(House.URL)
    fun getHouses(): Single<List<House>>

    @GET(Region.URL)
    fun getRegions(): Single<List<Region>>
}