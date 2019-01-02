package sharukh.thunderquote

import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import sharukh.thunderquote.data.Quote

const val BASE_URL = "https://api.forismatic.com/api/1.0/"

interface QuoteAPI {

    @GET(" ")
    fun getQuote(
            @Query("method") method: String = "getQuote",
            @Query("lang") language: String = "en",
            @Query("format") format: String = "json"
    ): Call<Quote>

    companion object {
        operator fun invoke(): QuoteAPI {

            return Retrofit.Builder()
                    .client(OkHttpClient())
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(QuoteAPI::class.java)

        }
    }

}