package com.example.healthmate.view.product

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.healthmate.model.Product
import com.example.healthmate.viewmodel.AuthViewModel
import com.example.healthmate.viewmodel.ProductUiState
import com.example.healthmate.viewmodel.ProductViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onProductClick: (String) -> Unit,
    onCategoryClick: (String) -> Unit,
    viewModel: ProductViewModel = koinViewModel(),
    authViewModel: AuthViewModel= koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    var showSearchBar by remember { mutableStateOf(false) }
    val isSearchActive = searchQuery.isNotEmpty()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()


    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = modifier.width(300.dp)
            ) {
                Column(
                    modifier = modifier
                        .fillMaxHeight()
                        .padding(horizontal = 16.dp, vertical = 24.dp)
                ) {
                    // Drawer Header
                    Text(
                        text = "HealthMate",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(16.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider()

                    // Add your drawer items here if needed
                    Spacer(modifier = Modifier.weight(1f))

                    // Logout Button at bottom
                    HorizontalDivider()
                    Button(
                        onClick = {
                            scope.launch {
                                drawerState.close()
                                authViewModel.logout()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Logout",
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = "Logout",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    ) {
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
                    onMenuClick = {
                        scope.launch { drawerState.open() }
                    },
                    onSearchClick = { showSearchBar = !showSearchBar }
                )

                AnimatedVisibility(
                    visible = showSearchBar,
                    enter = fadeIn() + slideInVertically(),
                    exit = fadeOut() + slideOutVertically()
                ) {
                    SearchBar(
                        query = searchQuery,
                        onQueryChange = { viewModel.updateSearchQuery(it) },
                        onClose = {
                            showSearchBar = false
                            viewModel.clearSearchQuery()
                        },
                        modifier = modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                    )
                }
                Spacer(modifier = modifier.height(16.dp))

                when( val state = uiState ) {
                    is ProductUiState.Error -> ErrorMessage(state.message)
                    is ProductUiState.Loading -> LoadingIndicator()
                    is ProductUiState.Success -> {
                        if (isSearchActive) {
                            // Show only products when searching
                            Spacer(modifier = modifier.height(16.dp))
                            ProductGrid(
                                products = state.products,
                                onProductClick = onProductClick,
                                onAddToCart = { /* Handle add to cart */ }
                            )
                        } else {
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
                                            onClick = {
                                                viewModel.setSelectedCategoryOnly(category.name)
                                                onCategoryClick(category.name)
                                            }
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
                                    onProductClick = onProductClick,
                                    onAddToCart = {}
                                )
                            }
                        }
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
    onAddToCart: (String) -> Unit,
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
                onClick = { onProductClick(product.id) },
                onAddToCart = { onAddToCart(product.id) }
            )
        }
    }
}
