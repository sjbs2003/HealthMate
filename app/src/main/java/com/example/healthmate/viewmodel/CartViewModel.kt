package com.example.healthmate.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthmate.model.CartItem
import com.example.healthmate.model.OrderProductItem
import com.example.healthmate.model.Product
import com.example.healthmate.model.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class CartUiState {
    data object Loading : CartUiState()
    data class Success(
        val items: List<CartItem> = emptyList(),
        val subtotal: Double = 0.0,
        val deliveryFee: Double = 5.0,
        val discount: Double = 0.0,
        val total: Double = 0.0,
        val promoCode: String? = null
    ) : CartUiState()
    data class Error(val message: String) : CartUiState()
}

class CartViewModel(private val repository: Repository) : ViewModel() {

    private val _uiState = MutableStateFlow<CartUiState>(CartUiState.Success())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    fun addToCart(product: Product) {
        val currentState = _uiState.value as? CartUiState.Success ?: return
        val currentItems = currentState.items.toMutableList()
        val existingItem = currentItems.find { it.product.id == product.id }

        if (existingItem != null) {
            val index = currentItems.indexOf(existingItem)
            currentItems[index] = existingItem.copy(quantity = existingItem.quantity + 1)
        } else {
            currentItems.add(CartItem(product, 1))
        }
        updateCart(currentItems)
    }

    fun removeFromCart(productId: String) {
        val currentState = _uiState.value as? CartUiState.Success ?: return
        val currentItems = currentState.items.toMutableList()
        currentItems.removeIf { it.product.id == productId }
        updateCart(currentItems)
    }

    fun updateQuantity(productId: String, newQuantity: Int) {
        if (newQuantity < 1) return
        val currentState = _uiState.value as? CartUiState.Success ?: return
        val currentItems = currentState.items.toMutableList()
        val existingItem = currentItems.find { it.product.id == productId } ?: return
        val index = currentItems.indexOf(existingItem)
        currentItems[index] = existingItem.copy(quantity = newQuantity)
        updateCart(currentItems)
    }

    private fun updateCart(items: List<CartItem>) {
        val currentState = _uiState.value as? CartUiState.Success ?: return

        val subTotal = items.sumOf { cartItem ->
            val product = cartItem.product
            val discountedPrice = if (product.discountPer > 0) {
                product.price * (100 - product.discountPer) / 100.0
            } else {
                product.price.toDouble()
            }
            discountedPrice * cartItem.quantity
        }

        val total = subTotal + currentState.deliveryFee

        _uiState.value = CartUiState.Success(
            items = items,
            subtotal = subTotal,
            deliveryFee = currentState.deliveryFee,
            total = total
        )
    }

    fun checkOut() {
        viewModelScope.launch {
            val currentState = _uiState.value as? CartUiState.Success ?: return@launch
            _uiState.value = CartUiState.Loading

            try {
                val orderItems = currentState.items.map {
                    OrderProductItem(it.product.id, it.quantity)
                }

                val result = repository.createOrder(orderItems)
                result.fold(
                    onSuccess = {
                        _uiState.value = CartUiState.Success() // Reset cart
                    },
                    onFailure = { exception ->
                        _uiState.value = CartUiState.Error(
                            exception.message ?: "Checkout failed"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = CartUiState.Error(
                    e.message ?: "Checkout failed"
                )
            }
        }
    }

    fun clearError() {
        if (_uiState.value is CartUiState.Error) {
            _uiState.value = CartUiState.Success()
        }
    }
}