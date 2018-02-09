package me.thenightmancodeth.garlidroid.Retrofit

import io.reactivex.Observable
import me.thenightmancodeth.garlidroid.Model.Address
import me.thenightmancodeth.garlidroid.Model.CoinMarketCap
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

/**
 * Created by joe on 2/9/18.
 */
interface CoinMktCapService {
    @GET("ticker/garlicoin")
    fun getUSDPrice(): Observable<List<CoinMarketCap>>

    companion object {
        fun create(): CoinMktCapService {
            val retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl("https://api.coinmarketcap.com/v1/")
                    .build()

            return retrofit.create(CoinMktCapService::class.java)
        }
    }
}