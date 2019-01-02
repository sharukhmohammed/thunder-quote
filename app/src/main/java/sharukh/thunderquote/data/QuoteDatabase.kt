package sharukh.thunderquote.data

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import android.os.AsyncTask


@Database(entities = [Quote::class], version = 1)
abstract class QuoteDatabase : RoomDatabase() {
    abstract fun quoteDao(): QuoteDao

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
                PopulateDbAsyncTask(instance).execute()
            }
        }

        class PopulateDbAsyncTask(private val quoteDatabase: QuoteDatabase?) : AsyncTask<Unit, Unit, Unit>() {
            override fun doInBackground(vararg params: Unit?) {
                quoteDatabase?.quoteDao()?.insert(Quote("Something", "Someone"))
                quoteDatabase?.quoteDao()?.insert(Quote("A Quote", "A Man"))
                quoteDatabase?.quoteDao()?.insert(Quote("Whatever", "Myself"))
            }

        }
    }
}