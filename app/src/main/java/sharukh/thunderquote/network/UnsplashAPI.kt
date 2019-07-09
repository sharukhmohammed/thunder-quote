package sharukh.thunderquote.network

import io.reactivex.Observable
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface UnsplashAPI {

    @GET("photos/random")
    fun getRandomImage(
            @Query("query") query: String? = null
    ): Observable<Response<UnsplashResponse>>

    companion object {

        private const val BASE_URL = "https://api.unsplash.com/"

        operator fun invoke(): UnsplashAPI {

            val okHttpClient = OkHttpClient
                    .Builder()
                    .addInterceptor { chain ->
                        chain.proceed(
                                chain
                                        .request()
                                        .newBuilder()
                                        .addHeader("Accept-Version", "v1")
                                        .addHeader("Authorization", "Client-ID cd10ebdf2b53a17ad2fd13582f70a4c721118ed4322a20f5f54257bc77d58193")
                                        .build()
                        )
                    }
                    .build()

            return Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl(BASE_URL)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(UnsplashAPI::class.java)

        }
    }

}