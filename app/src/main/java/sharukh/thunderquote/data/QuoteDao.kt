package sharukh.thunderquote.data

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*

@Dao
interface QuoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(quote: Quote)

    @Update
    fun update(quote: Quote)

    @Delete
    fun delete(quote: Quote)

    @Query("DELETE FROM quote_table")
    fun deleteAllQuotes()

    @Query("SELECT * FROM quote_table ORDER BY id ASC")
    fun getAllQuotes(): LiveData<List<Quote>>

    @Query("SELECT * FROM quote_table WHERE id IN (SELECT id FROM quote_table ORDER BY RANDOM() LIMIT 1)")
    fun getRandomQuote(): Quote
}