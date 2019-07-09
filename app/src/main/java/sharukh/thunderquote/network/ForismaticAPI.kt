package sharukh.thunderquote.network

import io.reactivex.Observable
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface ForismaticAPI {

    @GET("api/1.0/")
    fun getQuote(
            @Query("method") method: String = "getQuote",
            @Query("lang") language: String = "en",
            @Query("format") format: String = "json"
    ): Observable<Response<ForismaticResponse>>

    companion object {

        private const val BASE_URL = "https://api.forismatic.com/"

        operator fun invoke(): ForismaticAPI {

            return Retrofit.Builder()
                    .client(OkHttpClient())
                    .baseUrl(BASE_URL)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(ForismaticAPI::class.java)

        }
    }

}