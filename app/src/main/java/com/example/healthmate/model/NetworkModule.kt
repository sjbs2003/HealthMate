package com.example.healthmate.model

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import org.koin.dsl.module
import retrofit2.Retrofit


val networkModule = module {

    single {
        Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
        }
    }
    single {
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request()
                println("Making Request: ${request.url}")
                val response = chain.proceed(request)
                println("Got Response: ${response.code}")
                response
            }
            .build()
    }
    single {
        val json: Json = get()
        val client: OkHttpClient = get()
        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:3001/")
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }
    single {
        val retrofit: Retrofit = get()
        retrofit.create(ApiService::class.java)
    }
}