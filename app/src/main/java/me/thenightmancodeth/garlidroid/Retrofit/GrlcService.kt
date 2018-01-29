package me.thenightmancodeth.garlidroid.Retrofit

import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Created by joe on 1/28/18.
 */
interface GrlcService {
    @GET("ext/getaddress/{addr}")
    fun getAddressDetails(@Path("addr") addr: String): Observable<Address>

    companion object {
        fun create(): GrlcService {
            val retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl("https://explorer.grlc-bakery.fun/")
                    .build()

            return retrofit.create(GrlcService::class.java)
        }
    }
}