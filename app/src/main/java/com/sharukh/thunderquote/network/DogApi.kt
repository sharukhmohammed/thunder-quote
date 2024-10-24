package com.sharukh.thunderquote.network

import com.sharukh.thunderquote.model.DogBreedsDto
import com.sharukh.thunderquote.model.DogSubBreedsDto
import retrofit2.http.GET
import retrofit2.http.Path

interface DogApi {
    @GET("breeds/list/all")
    suspend fun getDogBreeds(): DogBreedsDto

    @GET("breed/{breed}/list")
    suspend fun getDogSubBreeds(@Path("breed") breed: String): DogSubBreedsDto
}