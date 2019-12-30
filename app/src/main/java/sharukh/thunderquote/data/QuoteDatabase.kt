package sharukh.thunderquote.data

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase


@Database(entities = [Quote::class], version = 1)
abstract class QuoteDatabase : RoomDatabase() {
    abstract fun dao(): Quote.Db

    companion object {

        @Volatile
        private var instance: QuoteDatabase? = null

        operator fun invoke(context: Context) = instance ?: synchronized(Any()) {
            //Using Any() lock vs this lock, what's the difference, what to use?
            instance ?: Room.databaseBuilder(context, QuoteDatabase::class.java, "quotes.db")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomOnCreateCallback)
                    .build()
                    .also {
                        instance = it
                    }
        }

        private val roomOnCreateCallback = object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                Log.w("Room", "Database created")
            }
        }
    }
}