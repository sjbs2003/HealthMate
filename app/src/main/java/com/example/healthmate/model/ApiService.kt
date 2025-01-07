package com.example.healthmate.model

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    // product endpoints
    @GET("api/product/all")
    suspend fun getAllProducts(): ProductResponse

    @GET("api/product/{id}")
    suspend fun getProduct(@Path("id") id: String): SingleProductResponse

    @GET("api/product/search")
    suspend fun searchProducts(@Query("query") query: String): ProductResponse

    @POST("api/product/all")
    suspend fun getSortedProducts(@Body request: ProductSortRequest): ProductResponse

    // category endpoints
    @GET("api/category/all")
    suspend fun getAllCategories(): CategoryResponse

    @GET("api/category/{categoryName}")
    suspend fun getProductByCategory(@Path("categoryName") category: String): ProductResponse

    // brand endpoints
    @GET("api/brand/all")
    suspend fun getAllBrands(): BrandResponse

    @GET("api/brand/{brandName}")
    suspend fun getProductsByBrand(@Path("brandName") brand: String): BrandResponse

    // auth endpoints
    @POST("api/user/signup")
    suspend fun signup(@Body request: SignupRequest): AuthResponse

    @POST("api/user/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("api/user/verifyOtp")
    suspend fun verifyOTP(@Body request: OtpVerificationRequest): AuthResponse

    @GET("api/user/verifyLoggedIn")
    suspend fun verifyLoggedIn(): BaseResponse

    // Order endpoints
    @POST("api/order/new")
    suspend fun createOrder(@Body request: OrderCreateRequest): OrderResponse

    @GET("api/order/all")
    suspend fun getUserOrders(): OrderResponse

    // Chat endpoints
    @POST("api/chat/reply")
    suspend fun getChatReply(@Body request: ChatRequest): ChatResponse

    // Admin endpoints
    @POST("api/admin/login")
    suspend fun adminLogin(@Body request: AdminLoginRequest): AdminResponse

    @POST("api/admin/new")
    suspend fun addNewProduct(@Body request: NewProductRequest): BaseResponse

    @GET("api/admin/orders")
    suspend fun getAllOrders(): OrderResponse

    @GET("api/admin/pendingOrders")
    suspend fun getPendingOrders(): OrderResponse

    @GET("api/admin/order/{id}")
    suspend fun getOrderDetails(@Path("id") orderId: String): OrderResponse

    @POST("api/admin/order/{id}")
    suspend fun updateOrderStatus(
        @Path("id") orderId: String,
        @Body request: OrderStatusUpdateRequest
    ): OrderResponse

}