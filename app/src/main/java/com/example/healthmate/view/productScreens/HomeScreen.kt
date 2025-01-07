package com.example.healthmate.view.productScreens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.healthmate.model.Product
import com.example.healthmate.viewmodel.ProductUiState
import com.example.healthmate.viewmodel.ProductViewModel

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onProductClick: (String) -> Unit,
    onCategoryClick: (String) -> Unit,
    viewModel: ProductViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showSearchBar by remember { mutableStateOf(false) }

    Surface (
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(top = 24.dp)
        ) {
            TopBar(
                onSearchClick = { showSearchBar = !showSearchBar },
                onCartClick = { TODO("implement cart nav") }
            )

            AnimatedVisibility(
                visible = showSearchBar,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                SearchBar(
                    query = viewModel.searchQuery.collectAsState().value,
                    onQueryChange = { viewModel.updateSearchQuery(it) },
                    onClose = { showSearchBar = false },
                    modifier = modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }
            Spacer(modifier = modifier.height(16.dp))

            when( val state = uiState ) {
                is ProductUiState.Error -> ErrorMessage(state.message)
                is ProductUiState.Loading -> LoadingIndicator()
                is ProductUiState.Success -> {
                    Column {
                        // Categories Section
                        Spacer(modifier = modifier.height(16.dp))
                        Text(
                            text = "Categories",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = modifier.padding(horizontal = 16.dp)
                        )
                        Spacer(modifier = modifier.height(8.dp))
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(state.categories) { category ->
                                CategoryCircle(
                                    category = category,
                                    isSelected = false,
                                    onClick = { onCategoryClick(category.name) }
                                )
                            }
                        }

                        // Products Section
                        Spacer(modifier = modifier.height(24.dp))
                        Text(
                            text = "Products",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = modifier.padding(horizontal = 16.dp)
                        )
                        Spacer(modifier = modifier.height(16.dp))

                        ProductGrid(
                            products = state.products,
                            onProductClick = onProductClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProductGrid(
    products: List<Product>,
    onProductClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        items(products) { product ->
            ProductGridItem(
                product = product,
                onClick = { onProductClick(product.id) }
            )
        }
    }
}
