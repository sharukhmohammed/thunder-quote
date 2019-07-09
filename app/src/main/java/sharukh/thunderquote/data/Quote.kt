package sharukh.thunderquote.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity()
data class Quote(
        val text: String,
        val author: String?,
        val createdAt: Long,
        val backgroundUrl: String?
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}