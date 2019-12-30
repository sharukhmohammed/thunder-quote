package sharukh.thunderquote.base

import android.app.Application

class App : Application() {

    init {
        context = this
    }

    companion object {
        @JvmStatic
        internal lateinit var context: App
    }
}