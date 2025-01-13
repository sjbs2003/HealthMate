package com.example.healthmate.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthmate.model.Brand
import com.example.healthmate.model.CategoryItem
import com.example.healthmate.model.Product
import com.example.healthmate.model.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


sealed class ProductUiState {
    data object Loading : ProductUiState()
    data class Success(
        val products: List<Product> = emptyList(),
        val categories: List<CategoryItem> = emptyList(),
        val brands: List<Brand> = emptyList()
    ) : ProductUiState()
    data class Error(val message: String) : ProductUiState()
}


class ProductViewModel (private val repository: Repository): ViewModel() {

    private val _uiState = MutableStateFlow<ProductUiState>(ProductUiState.Loading)
    val uiState: StateFlow<ProductUiState> = _uiState.asStateFlow()

    private val _selectedProduct = MutableStateFlow<Product?>(null)
    val selectedProduct: StateFlow<Product?> = _selectedProduct.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            try {
                _uiState.value = ProductUiState.Loading
                println("Loading initial data...")  // Debug log

                val productResult = repository.getAllProducts()
                println("Products result: $productResult")  // Debug log

                val categoryResult = repository.getAllCategories()
                println("Categories result: $categoryResult")  // Debug log

                val brandResult = repository.getAllBrands()
                println("Brands result: $brandResult")  // Debug log

                if (productResult.isSuccess && categoryResult.isSuccess && brandResult.isSuccess) {
                    val products = productResult.getOrNull() ?: emptyList()
                    println("Products size: ${products.size}")  // Debug log

                    _uiState.value = ProductUiState.Success(
                        products = products,
                        categories = categoryResult.getOrNull() ?: emptyList(),
                        brands = brandResult.getOrNull() ?: emptyList()
                    )
                } else {
                    // Log the specific failures
                    if (!productResult.isSuccess) println("Product fetch failed: ${productResult.exceptionOrNull()?.message}")
                    if (!categoryResult.isSuccess) println("Category fetch failed: ${categoryResult.exceptionOrNull()?.message}")
                    if (!brandResult.isSuccess) println("Brand fetch failed: ${brandResult.exceptionOrNull()?.message}")

                    _uiState.value = ProductUiState.Error("Failed To Load Initial Data!")
                }
            } catch (e: Exception) {
                println("Exception in loadInitialData: ${e.message}")  // Debug log
                e.printStackTrace()
                _uiState.value = ProductUiState.Error(e.message ?: "Unknown error occurred!")
            }
        }
    }

    fun loadProductById(id: String) {
        viewModelScope.launch {
            _uiState.value = ProductUiState.Loading
            try {
                val result = repository.getProduct(id)
                result.fold(
                    onSuccess = { product ->
                        _selectedProduct.value = product
                        _uiState.value = ProductUiState.Success()
                    },
                    onFailure = { exception ->
                        _uiState.value = ProductUiState.Error(exception.message ?: "Failed to load product details")
                    }
                )
            } catch (e: Exception) {
                _uiState.value = ProductUiState.Error(e.message ?: "Failed to load product details")
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
                val productResult = repository.getProductsByCategory(category)
                val categoryResult = repository.getAllCategories()

                if (productResult.isSuccess && categoryResult.isSuccess) {
                    _selectedCategory.value = category // Set the selected category first
                    _uiState.value = ProductUiState.Success(
                        products = productResult.getOrNull() ?: emptyList(),
                        categories = categoryResult.getOrNull() ?: emptyList()
                    )
                } else {
                    _uiState.value = ProductUiState.Error("Failed to load category data")
                }
            } catch (e: Exception) {
                _uiState.value = ProductUiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }


    fun setSelectedCategoryOnly(category: String) {
        _selectedCategory.value = category
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

    fun sortProducts(order: String?, type: String?) {
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