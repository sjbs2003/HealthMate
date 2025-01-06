package com.example.healthmate.view.productScreens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.healthmate.model.CategoryItem
import com.example.healthmate.model.Product
import com.example.healthmate.viewmodel.ProductUiState
import com.example.healthmate.viewmodel.ProductViewModel

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onProductClick: (String) -> Unit,
    onCategoryClick: (String) -> Unit,
    onBrandClick: (String) -> Unit,
    viewModel: ProductViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    var showSearchbar by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        TopBar(
            onSearchClick = { showSearchbar = !showSearchbar },
            onCartClick = {}
        )

        AnimatedVisibility(
            visible = showSearchbar,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut() + slideOutVertically()
        ) {
            SearchBar(
                query = searchQuery,
                onQueryChange = { viewModel.updateSearchQuery(it) },
                onClose = { showSearchbar = false },
                modifier = modifier.padding(16.dp)
            )
        }

        // Main Content
        when (val state = uiState) {
            is ProductUiState.Loading -> LoadingIndicator()
            is ProductUiState.Error -> ErrorMessage(state.message)
            is ProductUiState.Success -> {
                CategoriesRow(
                    categories = state.categories,
                    onCategoryClick = onCategoryClick,
                    modifier = modifier.padding(vertical = 16.dp)
                )

                ProductsRow(
                    products = state.products,
                    onProductClick = onProductClick
                )
            }
        }
    }
}

@Composable
fun CategoriesRow(
    categories: List<CategoryItem>,
    onCategoryClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(categories) { category ->
            CategoryCircle(
                category = category,
                onClick = { onCategoryClick(category.name) }
            )
        }
    }
}

@Composable
fun ProductsRow(
    modifier: Modifier = Modifier,
    products: List<Product>,
    onProductClick: (String) -> Unit
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(products) { product ->
            ProductCircle(
                product = product,
                onClick = { onProductClick(product.id) }
            )
        }
    }
}