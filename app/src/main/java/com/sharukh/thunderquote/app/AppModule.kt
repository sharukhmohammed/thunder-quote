package com.sharukh.thunderquote.app

import kotlinx.serialization.json.Json

object AppModule {
    val json
        get() = Json {
            encodeDefaults = true
            ignoreUnknownKeys = true
        }
}