package com.sharukh.thunderquote.repo

import com.sharukh.thunderquote.di.ServiceLocator
import com.sharukh.thunderquote.model.DogBreedsDto
import com.sharukh.thunderquote.model.DogSubBreedsDto
import com.sharukh.thunderquote.network.DogApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DogRepo {
    private val dogApi = ServiceLocator
        .retrofit("https://dog.ceo/api/")
        .create(DogApi::class.java)

    fun getBreeds(): Flow<DogBreedsDto> {
        return flow { emit(dogApi.getDogBreeds()) }
    }

    fun getSubBreeds(breed: String): Flow<DogSubBreedsDto> {
        return flow { emit(dogApi.getDogSubBreeds(breed)) }
    }
}