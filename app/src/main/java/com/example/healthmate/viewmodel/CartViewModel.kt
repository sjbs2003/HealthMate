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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CartViewModel(private val repository: Repository) : ViewModel() {

    private val _cartState = MutableStateFlow(CartState())
    val cartState: StateFlow<CartState> = _cartState.asStateFlow()

    fun addToCart(product: Product) {
        val currentItems = _cartState.value.items.toMutableList()
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
        _cartState.update { currentState ->
            val updatedItems = currentState.items.filterNot { it.product.id == productId }
            currentState.copy(items = updatedItems).also { calculateTotals(it) }
        }
    }

    fun updateQuantity(productId: String, newQuantity: Int) {
        if (newQuantity < 1) return

        _cartState.update { currentState ->
            val updatedItems = currentState.items.map { item ->
                if (item.product.id == productId) {
                    item.copy(quantity = newQuantity)
                } else {
                    item
                }
            }
            currentState.copy(items = updatedItems).also { calculateTotals(it) }
        }
    }

    private fun updateCart(items: List<CartItem>) {
        _cartState.update { currentState ->
            currentState.copy(items = items).also { calculateTotals(it) }
        }
    }

    private fun calculateTotals(state: CartState = _cartState.value) {
        val subTotal = state.items.sumOf {
            (it.product.price * it.quantity).toDouble()
        }

        val discount = subTotal * state.discount
        val total = subTotal - discount + state.deliveryFee

        _cartState.update { it.copy(
            subtotal = subTotal,
            total = total
        ) }
    }

    fun checkOut() {
        viewModelScope.launch {
            _cartState.update { it.copy(isLoading = true) }
            try {
                val orderItems = _cartState.value.items.map {
                    OrderProductItem(it.product.id, it.quantity)
                }

                val result = repository.createOrder(orderItems)
                result.fold(
                    onSuccess = {
                        // Clear cart after successful checkout
                        _cartState.value = CartState()
                    },
                    onFailure = { exception ->
                        _cartState.update { it.copy(
                            error = exception.message ?: "Checkout failed",
                            isLoading = false
                        ) }
                    }
                )
            } catch (e: Exception) {
                _cartState.update { it.copy(
                    error = e.message ?: "Checkout failed",
                    isLoading = false
                ) }
            }
        }
    }

    fun clearError() {
        _cartState.update { it.copy(error = null) }
    }
}