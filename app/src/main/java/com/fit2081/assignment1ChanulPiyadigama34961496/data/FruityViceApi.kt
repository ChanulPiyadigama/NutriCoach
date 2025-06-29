package com.fit2081.assignment1ChanulPiyadigama34961496.data

import com.fit2081.assignment1ChanulPiyadigama34961496.data.models.FruitModel
import retrofit2.http.GET
import retrofit2.http.Path

//endpoints/requests to the fruityvice API, which will be passed to retrofit to create the API service.
interface FruityViceApi {
    @GET("api/fruit/{name}")
    suspend fun getFruitInfo(@Path("name") name: String): FruitModel
}