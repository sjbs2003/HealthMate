package com.example.healthmate.model

import retrofit2.http.GET

interface ApiService {

    // product endpoints
    @GET("api/product/all")
    suspend fun getAllProducts(): ProductResponse
}