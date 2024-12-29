package com.example.healthmate.model

import javax.inject.Inject
import javax.inject.Singleton


//Singleton ->  the dependency injection framework will create only one instance and reuse it everywhere it's needed
//Inject -> when someone needs this class, here's how to construct it
@Singleton
class Repository @Inject constructor(
    private val apiService: ApiService
) {

    // PRODUCT RELATED OPERATIONS
    suspend fun getAllProducts(): Result<List<Product>> = try {
        val response = apiService.getAllProducts()
        if (response.success && response.data != null){
            Result.success(response.data)
        } else {
            Result.failure(Exception(response.message ?: "Unknown error occurred"))
        }
    } catch (e: Exception){
        Result.failure(e)
    }

    suspend fun getProduct(id: String): Result<List<Product>> = try {
        val response = apiService.getProduct(id)
        if (response.success && response.data != null){
            Result.success(response.data)
        } else {
            Result.failure(Exception(response.message ?: "Unknown error occurred"))
        }
    } catch (e: Exception){
        Result.failure(e)
    }

    suspend fun searchProducts(query: String): Result<List<Product>> = try {
        val response = apiService.searchProducts(query)
        if (response.success && response.data != null){
            Result.success(response.data)
        } else {
            Result.failure(Exception(response.message ?: "Unknown error occurred"))
        }
    } catch (e: Exception){
        Result.failure(e)
    }

    suspend fun getSortedProducts(order: String?, type: String?): Result<List<Product>> = try {
        val response = apiService.getSortedProducts(ProductSortRequest(order, type))
        if (response.success && response.data != null){
            Result.success(response.data)
        } else {
            Result.failure(Exception(response.message ?: "Unknown error occurred"))
        }
    } catch (e: Exception){
        Result.failure(e)
    }

    // CATEGORY RELATED OPERATIONS
    suspend fun getAllCategories(): Result<List<CategoryItem>> = try {
        val response = apiService.getAllCategories()
        if (response.success){
            Result.success(response.data)
        } else {
            Result.failure(Exception("Failed to fetch Categories"))
        }
    } catch (e: Exception){
        Result.failure(e)
    }
}