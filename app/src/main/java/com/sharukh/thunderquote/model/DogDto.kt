package com.sharukh.thunderquote.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class DogBreedsDto(
    val message: JsonObject,
    val status: String,
)

@Serializable
data class DogSubBreedsDto(
    val message: List<String>,
    val status: String,
)