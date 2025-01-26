package com.example.healthmate.model

class Repository (private val apiService: ApiService) {

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

    suspend fun getProduct(id: String): Result<Product> = try {
        val response = apiService.getProduct(id)
        if (response.success && response.data != null) {
            // Since we're expecting a single product, take first item
                Result.success(response.data)
        } else {
            Result.failure(Exception(response.message ?: "Unknown error occurred"))
        }
    } catch (e: Exception) {
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
        if (response.success && response.data != null) {
            Result.success(response.data)
        } else {
            Result.failure(Exception(response.message ?: "Failed to sort products"))
        }
    } catch (e: Exception) {
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

    suspend fun getProductsByCategory(category: String): Result<List<Product>> = try {
        val response = apiService.getProductByCategory(category)
        if (response.success && response.data != null){
            Result.success(response.data)
        } else {
            Result.failure(Exception(response.message ?: "Unknown error occurred"))
        }
    } catch (e: Exception){
        Result.failure(e)
    }

    // BRAND RELATED OPERATIONS
    suspend fun getAllBrands(): Result<List<Brand>> = try {
        val response = apiService.getAllBrands()
        if (response.success){
            Result.success(response.data)
        } else {
            Result.failure(Exception("Failed to fetch brands"))
        }
    } catch (e:Exception) {
        Result.failure(e)
    }

    suspend fun getProductsByBrand(brand: String): Result<List<Brand>> = try {
        val response = apiService.getProductsByBrand(brand)
        if (response.success){
            Result.success(response.data)
        } else {
            Result.failure(Exception("Failed to fetch brands"))
        }
    } catch (e: Exception){
        Result.failure(e)
    }

    // Auth Related Operations
    suspend fun signup(name: String, phone: String, email: String? = null): Result<String> = try {
        val response = apiService.signup(SignupRequest(name, phone, email))
        if (response.success && response.data?.token != null) {
            Result.success(response.data.token)
        } else {
            Result.failure(Exception(response.message ?: "SignUp Failed"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun login(phone: String): Result<String> = try {
        val response = apiService.login(LoginRequest(phone))
        if (response.success) {
            Result.success(response.message ?: "OTP sent successfully")
        } else {
            Result.failure(Exception(response.message ?: "Login Failed"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun verifyOTP(phone: String, otp: String): Result<String> = try {
        val response = apiService.verifyOTP(OtpVerificationRequest(phone, otp))
        if (response.success && response.data?.token != null) {
            Result.success(response.data.token)
        } else {
            Result.failure(Exception(response.message ?: "OTP Verification Failed"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    // ORDER RELATED OPERATIONS
    suspend fun createOrder(items: List<OrderProductItem>): Result<Order> = try {
        val response = apiService.createOrder(OrderCreateRequest(items))
        if (response.success && response.data != null) {
            Result.success(response.data)
        } else {
            Result.failure(Exception(response.message ?: "Failed to create order"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun getUserOrders(): Result<Order> = try {
        val response = apiService.getUserOrders()
        if (response.success && response.data != null) {
            Result.success(response.data)
        } else {
            Result.failure(Exception(response.message ?: "Failed to fetch orders"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    // CHAT RELATED OPERATIONS
    suspend fun getChatReply(message: String): Result<String> = try {
        val response = apiService.getChatReply(ChatRequest(message))
        if (response.success) {
            Result.success(response.reply)
        } else {
            Result.failure(Exception("Failed to get chat reply"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }


    // ADMIN RELATED OPERATIONS
    suspend fun adminLogin(email: String, password: String): Result<Boolean> = try {
        val response = apiService.adminLogin(AdminLoginRequest(email, password))
        if (response.success) {
            Result.success(true)
        } else {
            Result.failure(Exception(response.message ?: "Admin login failed"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun addNewProduct(
        name: String,
        description: String,
        imageLink: List<String>,
        type: MedsType,
        quantity: Int,
        productType: ProductType,
        categories: List<Category>,
        brand: String,
        price: Int
    ): Result<Boolean> = try {
        val response = apiService.addNewProduct(
            NewProductRequest(
                name, description, imageLink, type, quantity,
                productType, categories, brand, price
            )
        )
        if (response.success) {
            Result.success(true)
        } else {
            Result.failure(Exception(response.message ?: "Failed to add product"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun getAllOrders(): Result<Order> = try {
        val response = apiService.getAllOrders()
        if (response.success && response.data != null) {
            Result.success(response.data)
        } else {
            Result.failure(Exception(response.message ?: "Failed to fetch all orders"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun getPendingOrders(): Result<Order> = try {
        val response = apiService.getPendingOrders()
        if (response.success && response.data != null) {
            Result.success(response.data)
        } else {
            Result.failure(Exception(response.message ?: "Failed to fetch pending orders"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun updateOrderStatus(orderId: String, status: OrderStatus): Result<Order> = try {
        val response = apiService.updateOrderStatus(orderId, OrderStatusUpdateRequest(status))
        if (response.success && response.data != null) {
            Result.success(response.data)
        } else {
            Result.failure(Exception(response.message ?: "Failed to update order status"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}