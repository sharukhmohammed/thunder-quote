package sharukh.thunderquote.data

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "quote_table")
data class Quote(
        @SerializedName("quoteText")
        val text: String,
        @SerializedName("quoteAuthor")
        val author: String?
) {

    @PrimaryKey(autoGenerate = true)
    val id: Int =0
}