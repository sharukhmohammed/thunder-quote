package sharukh.thunderquote.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface QuoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(quote: Quote)

    @Update
    fun update(quote: Quote)

    @Delete
    fun delete(quote: Quote)

    @Query("DELETE FROM quote")
    fun deleteAllQuotes()

    @Query("SELECT * FROM quote ORDER BY id ASC")
    fun getAllQuotes(): LiveData<List<Quote>>

    @Query("SELECT * FROM quote WHERE id IN (SELECT id FROM quote ORDER BY RANDOM() LIMIT 1)")
    fun getRandomQuote(): Quote
}