package com.example.healthmate.viewmodel

import com.example.healthmate.model.Brand
import com.example.healthmate.model.CategoryItem
import com.example.healthmate.model.Product
import com.example.healthmate.model.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
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
) {

}