package com.example.healthmate.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthmate.model.Brand
import com.example.healthmate.model.CategoryItem
import com.example.healthmate.model.Product
import com.example.healthmate.model.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


sealed class ProductUiState {
    data object Loading : ProductUiState()
    data class Success(
        val products: List<Product> = emptyList(),
        val categories: List<CategoryItem> = emptyList(),
        val brands: List<Brand> = emptyList()
    ) : ProductUiState()
    data class Error(val message: String) : ProductUiState()
}


@HiltViewModel
class ProductViewModel @Inject constructor(
    private val repository: Repository
): ViewModel() {

    private val _uiState = MutableStateFlow<ProductUiState>(ProductUiState.Loading)
    val uiState: StateFlow<ProductUiState> = _uiState.asStateFlow()

    private val _selectedProduct = MutableStateFlow<Product?>(null)
    val selectedProduct: StateFlow<Product?> = _selectedProduct.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            try {
                // Load Brands, Products, Categories concurrently
                val productResult = repository.getAllProducts()
                val categoryResult = repository.getAllCategories()
                val brandResult = repository.getAllBrands()

                // check if all requests are successful
                if (productResult.isSuccess && categoryResult.isSuccess && brandResult.isSuccess) {
                    _uiState.value = ProductUiState.Success(
                        products = productResult.getOrNull() ?: emptyList(),
                        categories = categoryResult.getOrNull() ?: emptyList(),
                        brands = brandResult.getOrNull() ?: emptyList()
                    )
                } else {
                    _uiState.value = ProductUiState.Error("Failed To Load Initial Data!")
                }
            } catch (e:Exception) {
                _uiState.value = ProductUiState.Error(e.message ?: "Unknown error occurred!")
            }
        }
    }

    fun refreshData() {
        loadInitialData()
    }

    fun setSelectedProduct(product: Product) {
        _selectedProduct.value = product
    }

    fun updateSearchQuery(query:String) {
        _searchQuery.value = query
        if (query.length >= 3){
            searchProducts(query)
        }
    }

    fun clearSearchQuery() {
        _searchQuery.value =""
        loadInitialData()
    }

    private fun searchProducts(query: String) {
        viewModelScope.launch {
            _uiState.value = ProductUiState.Loading
            try {
                val result = repository.searchProducts(query)
                if (result.isSuccess) {
                    val currentState = _uiState.value
                    if (currentState is ProductUiState.Success) {
                        _uiState.value = currentState.copy(products = result.getOrNull() ?: emptyList())
                    } else {
                        _uiState.value = ProductUiState.Success(products = result.getOrNull() ?: emptyList())
                    }
                } else {
                    _uiState.value = ProductUiState.Error("Search Failed")
                }
            } catch (e: Exception) {
                _uiState.value = ProductUiState.Error(e.message ?: "Search Failed")
            }
        }
    }

    fun loadByCategory(category: String) {
        viewModelScope.launch {
            _uiState.value = ProductUiState.Loading
            try {
                val result = repository.getProductsByCategory(category)
                if (result.isSuccess) {
                    val currentState = _uiState.value
                    if (currentState is ProductUiState.Success) {
                        _uiState.value = currentState.copy(products = result.getOrNull() ?: emptyList())
                    } else {
                        _uiState.value = ProductUiState.Success(products = result.getOrNull() ?: emptyList())
                    }
                } else {
                    _uiState.value = ProductUiState.Error("Failed to load for this category")
                }
            } catch (e: Exception) {
                _uiState.value = ProductUiState.Error(e.message ?: "Failed to load for this category")
            }
        }
    }

    fun loadProductsByBrand(brand: String) {
        viewModelScope.launch {
            _uiState.value = ProductUiState.Loading
            try {
                val result = repository.getProductsByBrand(brand)
                if (result.isSuccess) {
                    val currentState = _uiState.value
                    if (currentState is ProductUiState.Success) {
                        _uiState.value = currentState.copy(brands = result.getOrNull() ?: emptyList())
                    } else {
                        _uiState.value = ProductUiState.Success(brands = result.getOrNull() ?: emptyList())
                    }
                } else {
                    _uiState.value = ProductUiState.Error("Failed to load for this category")
                }
            } catch (e: Exception) {
                _uiState.value = ProductUiState.Error(e.message ?: "Failed to load for this category")
            }
        }
    }

    private fun sortProducts(order: String?, type: String?) {
        viewModelScope.launch {
            _uiState.value = ProductUiState.Loading
            try {
                val result = repository.getSortedProducts(order, type)
                if (result.isSuccess) {
                    val currentState = _uiState.value
                    if (currentState is ProductUiState.Success) {
                        _uiState.value = currentState.copy(products = result.getOrNull() ?: emptyList())
                    } else {
                        _uiState.value = ProductUiState.Success(products = result.getOrNull() ?: emptyList())
                    }
                } else {
                    _uiState.value = ProductUiState.Error("Failed to sort products")
                }
            } catch (e: Exception) {
                _uiState.value = ProductUiState.Error(e.message ?: "Failed to sort products")
            }
        }
    }
}