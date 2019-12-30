package sharukh.thunderquote.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Entity()
data class Quote(
        val text: String,
        val author: String?,
        val createdAt: Long,
        val backgroundUrl: String?,
        val userFullName: String?,
        val userLinkUrl: String?,
        var isFavorite: Boolean = false
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    val unsplashLinkUrl: String
        get() = "https://unsplash.com/?utm_source=ThunderQuote&utm_medium=referral" //Replace ThunderQuote with Unsplash App name when required.


    //Db access object's abstract methods
    @Dao
    interface Db {

        @Insert
        fun insert(quote: Quote): Long

        @Update
        fun update(quote: Quote)

        @Delete
        fun delete(quote: Quote)

        @Query("DELETE FROM quote")
        fun deleteAllQuotes()

        @Query("SELECT * FROM quote ORDER BY id DESC")
        fun getAllQuotes(): LiveData<List<Quote>>

        @Query("SELECT * FROM quote WHERE id IN (SELECT id FROM quote ORDER BY RANDOM() LIMIT 1)")
        fun getRandomQuote(): Quote

        @Query("SELECT * FROM quote where id =:quoteId LIMIT 1")
        fun getQuote(quoteId: Long): Quote

        @Query("SELECT * FROM quote WHERE isFavorite = 1 ORDER BY id DESC")
        fun getFavorites(): LiveData<List<Quote>>

    }
}