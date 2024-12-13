package com.example.healthmate.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class ProductType {
    CAPSULE,
    TABLET,
    POWDER,
    SACHET,
    OTHER
}

enum class MedsType {
    MEDS,
    SUPPLEMENT,
    COSMETIC,
    FOOD
}

enum class OrderStatus {
    PENDING,
    COMPLETED,
    CANCELED,
    REFUNDED
}

enum class Category {
    HAIR_CARE,
    ORAL_CARE,
    SEXUAL_WELLNESS,
    SKIN_CARE,
    FEMININE_CARE,
    BABY_CARE,
    ELDERLY_CARE,
    MEN_GROOMING,
    VITAMIN_AND_NUTRITION,
    FITNESS_SUPPLEMENTS,
    NUTRITIONAL_DRINKS,
    HEALTHY_SNACKS,
    HERBAL_JUICE,
    MONITORING_DEVICES,
    REHYDRATION_BEVERAGES,
    IMMUNITY_BOOSTERS,
    MEDICINE,
    STOMACH_CARE,
    COLD_AND_COUGH,
    PAIN_RELIEF,
    FIRST_AID,
    DIABETES,
    EYE_AND_EAR_CARE,
    SKIN_INFECTION,
    SUPPORTS_AND_BRACES
}


@Serializable
data class Product(
    val id: String,
    val name: String,
    val description: String,
    @SerialName("imageLink") val imageLinks: List<String>,
    val type: MedsType,
    val productType: ProductType,
    val quantity: Int,
    val categories: List<Category>,
    val brand: String,
    val price: Int
)

@Serializable
data class ProductResponse(
    val success: Boolean,
    val data: List<Product>? = null,
    val message: String? = null
)

@Serializable
data class CategoryItem(
    val id: Int,
    val name: String,
    val description: String,
    val image: String? = null
)

@Serializable
data class CategoryResponse(
    val success: Boolean,
    val count: Int,
    val data: List<CategoryItem>
)

@Serializable
data class OrderItem(
    val id: String,
    val productId: String,
    val quantity: Int
)

@Serializable
data class Order(
    val id: String,
    val userId: String,
    val totalPrice: Int,
    val status: OrderStatus,
    @SerialName("orderItems") val items: List<OrderItem>,
    // Add timestamp fields
    @SerialName("createdAt") val createdAt: String,
    @SerialName("updatedAt") val updatedAt: String,
    // ... existing fields

)

@Serializable
data class OrderResponse(
    val success: Boolean,
    val data: Order? = null,
    val message: String? = null
)

@Serializable
data class User(
    val id: String,
    val name: String,
    val email: String? = null,
    val phone: String
)

@Serializable
data class UserResponse(
    val success: Boolean,
    val token: String? = null,
    val error: String? = null
)

@Serializable
data class Brand(
    val id: Int,
    val name: String,
    val image: String
)

@Serializable
data class BrandResponse(
    val success: Boolean,
    val count: Int,
    val data: List<Brand>
)

// Request classes
@Serializable
data class SignupRequest(
    val name: String,
    val phone: String,
    val email: String? = null
)

@Serializable
data class LoginRequest(
    val phone: String
)

@Serializable
data class OtpVerificationRequest(
    val phone: String,
    val otp: String
)

// Response classes
@Serializable
data class AuthResponse(
    val success: Boolean,
    val token: String? = null,
    val error: String? = null,
    val message: String? = null
)

@Serializable
data class ChatRequest(
    val message: String
)

@Serializable
data class ChatResponse(
    val success: Boolean,
    val reply: String
)

@Serializable
data class ProductSortRequest(
    val order: String? = null, // "asc" | "desc"
    val type: String? = null   // "name" | "price"
)

@Serializable
data class ProductSearchRequest(
    val query: String
)

@Serializable
data class OrderCreateRequest(
    val products: List<OrderProductItem>
)

@Serializable
data class OrderProductItem(
    val id: String,
    val quantity: Int
)

@Serializable
data class AdminLoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class AdminResponse(
    val success: Boolean,
    val message: String? = null
)

@Serializable
data class BaseResponse(
    val success: Boolean,
    val message: String? = null
)

@Serializable
data class NewProductRequest(
    val name: String,
    val description: String,
    val imageLink: List<String>,
    val type: MedsType,
    val quantity: Int,
    val productType: ProductType,
    val categories: List<Category>,
    val brand: String,
    val price: Int
)

@Serializable
data class OrderStatusUpdateRequest(
    val status: OrderStatus
)

@Serializable
data class ProductWithOrderDetails(
    // Product details shown in orders
    val id: String,
    val name: String,
    val price: Int
    // Add other fields you need when showing products in orders
)
