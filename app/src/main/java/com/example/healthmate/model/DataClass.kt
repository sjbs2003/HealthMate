package com.example.healthmate.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class ProductType {
    @SerialName("capsule")
    CAPSULE,
    @SerialName("tablet")
    TABLET,
    @SerialName("powder")
    POWDER,
    @SerialName("sachet")
    SACHET,
    @SerialName("other")
    OTHER
}

@Serializable
enum class MedsType {
    @SerialName("Meds")
    MEDS,
    @SerialName("Supplement")
    SUPPLEMENT,
    @SerialName("Cosmetic")
    COSMETIC,
    @SerialName("Food")
    FOOD
}

@Serializable
enum class OrderStatus {
    @SerialName("pending")
    PENDING,
    @SerialName("completed")
    COMPLETED,
    @SerialName("canceled")
    CANCELED,
    @SerialName("refunded")
    REFUNDED
}

@Serializable
enum class Category {
    @SerialName("Hair_Care")
    HAIR_CARE,
    @SerialName("Oral_Care")
    ORAL_CARE,
    @SerialName("Sexual_Wellness")
    SEXUAL_WELLNESS,
    @SerialName("Skin_Care")
    SKIN_CARE,
    @SerialName("Feminine_Care")
    FEMININE_CARE,
    @SerialName("Baby_Care")
    BABY_CARE,
    @SerialName("Elderly_Care")
    ELDERLY_CARE,
    @SerialName("Men_Grooming")
    MEN_GROOMING,
    @SerialName("Vitamin_And_Nutrition")
    VITAMIN_AND_NUTRITION,
    @SerialName("Fitness_Supplements")
    FITNESS_SUPPLEMENTS,
    @SerialName("Nutritional_Drinks")
    NUTRITIONAL_DRINKS,
    @SerialName("Healthy_Snacks")
    HEALTHY_SNACKS,
    @SerialName("Herbal_Juice")
    HERBAL_JUICE,
    @SerialName("Monitoring_Devices")
    MONITORING_DEVICES,
    @SerialName("Rehydration_Beverages")
    REHYDRATION_BEVERAGES,
    @SerialName("Immunity_Boosters")
    IMMUNITY_BOOSTERS,
    @SerialName("Medicine")
    MEDICINE,
    @SerialName("Stomach_Care")
    STOMACH_CARE,
    @SerialName("Cold_And_Cough")
    COLD_AND_COUGH,
    @SerialName("Pain_Relief")
    PAIN_RELIEF,
    @SerialName("First_Aid")
    FIRST_AID,
    @SerialName("Diabetes")
    DIABETES,
    @SerialName("Eye_And_Ear_Care")
    EYE_AND_EAR_CARE,
    @SerialName("Skin_Infection")
    SKIN_INFECTION,
    @SerialName("Supports_And_Braces")
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
data class SingleProductResponse(
    val success: Boolean,
    val data: Product? = null,
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
