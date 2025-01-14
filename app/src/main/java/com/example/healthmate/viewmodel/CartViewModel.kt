package com.example.healthmate.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthmate.model.CartItem
import com.example.healthmate.model.CartState
import com.example.healthmate.model.OrderProductItem
import com.example.healthmate.model.Product
import com.example.healthmate.model.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CartViewModel(private val repository: Repository) : ViewModel() {

    private val _cartState = MutableStateFlow(CartState())
    val cartState: StateFlow<CartState> = _cartState.asStateFlow()

    fun addToCart(product: Product) {
        val currentItems = _cartState.value.items.toMutableList()
        val existingItems = currentItems.find { it.product.id == product.id }

        if (existingItems != null) {
            val index = currentItems.indexOf(existingItems)
            currentItems[index] = existingItems.copy(quantity = existingItems.quantity + 1)
        } else {
            currentItems.add(CartItem(product, 1))
        }
        updateCart(currentItems)
    }

    fun removeFromCart(productId: String) {
        val currentItems = _cartState.value.items.toMutableList()
        currentItems.removeIf { it.product.id == productId }
        updateCart(currentItems)
    }

    fun updateQuantity(productId: String, newQuantity: Int) {
        if (newQuantity < 1) return

        val currentItems = _cartState.value.items.toMutableList()
        val existingItems = currentItems.find { it.product.id == productId } ?: return
        val index = currentItems.indexOf(existingItems)
        currentItems[index] = existingItems.copy(quantity = newQuantity)

        updateCart(currentItems)
    }

    private fun updateCart(item: List<CartItem>) {
        _cartState.value = _cartState.value.copy(items = item)
        calculateTotals()
    }

    private fun calculateTotals() {
        val subTotal = _cartState.value.items.sumOf {
            it.product.price * it.quantity
        }.toDouble()

        val discount = subTotal * _cartState.value.discount
        val deliveryFee = _cartState.value.deliveryFee
        val total = subTotal - discount + deliveryFee

        _cartState.value = _cartState.value.copy(
            subtotal = subTotal,
            total = total
        )
    }

    fun checkOut() {
        viewModelScope.launch {
            _cartState.value = _cartState.value.copy(isLoading = true)
            try {
                // Convert cart items to OrderProductItem list
                val orderItems = _cartState.value.items.map {
                    OrderProductItem(it.product.id, it.quantity)
                }

                val result = repository.createOrder(orderItems)
                result.fold(
                    onSuccess = {
                        // Convert cart items to OrderProductItem list
                        _cartState.value = CartState()
                    },
                    onFailure = { exception ->
                        _cartState.value = _cartState.value.copy(
                            error = exception.message ?: "Checkout failed"
                        )
                    }
                )
            } catch (e: Exception) {
                _cartState.value = _cartState.value.copy(
                    error = e.message ?: "Checkout failed"
                )
            } finally {
                _cartState.value = _cartState.value.copy(isLoading = false)
            }
        }

        fun clearError() {
            _cartState.value = _cartState.value.copy(error = null)
        }
    }
}