package com.example.healthmate.view.productScreens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import com.example.healthmate.model.CategoryItem
import com.example.healthmate.model.Product
import com.example.healthmate.viewmodel.ProductUiState
import com.example.healthmate.viewmodel.ProductViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun CategoryScreen(
    modifier: Modifier = Modifier,
    onProductClick: (String) -> Unit,
    onBackClick: () -> Unit,
    viewModel: ProductViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    var showSearchbar by remember { mutableStateOf(false) }
    val selectedCategory by viewModel.selectedCategory.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(top = 24.dp)
    ) {
        CategoryTopBar(
            onBackClick = onBackClick,
            onSearchClick = { showSearchbar = !showSearchbar },
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
                modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        // Main Content
        when (val state = uiState) {
            is ProductUiState.Loading -> LoadingIndicator()
            is ProductUiState.Error -> ErrorMessage(state.message)
            is ProductUiState.Success -> {
                Spacer(modifier = modifier.height(12.dp))

                Column(modifier = modifier.padding(start = 16.dp)) {
                    Text(
                        text = "Categories",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = modifier.height(12.dp))

                    // Categories row with state preservation
                    val listState = rememberLazyListState()
                    LazyRow(
                        state = listState,
                        modifier = modifier.padding(vertical = 16.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.categories) { category ->
                            CategoryCircle(
                                category = category,
                                isSelected = category.name == selectedCategory,
                                onClick = {} // we don't want to do anything here
                            )
                        }
                    }
                }
                Spacer(modifier = modifier.height(32.dp))
                LaunchedEffect(selectedCategory) {
                    selectedCategory?.let { category ->
                        // Only load data if we need to
                        if (state.products.isEmpty()) {
                            viewModel.loadByCategory(category)
                        }
                    }
                }
                Box(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    // Filter products to only show products from selected category
                    val filteredProducts = selectedCategory?.let { selectedCat ->
                        state.products.filter { product ->
                            product.categories.any { it.name == selectedCat }
                        }
                    } ?: emptyList()

                    ProductsRow(
                        products = filteredProducts,
                        onProductClick = onProductClick
                    )
                }
            }
        }
    }
}


@Composable
fun CategoryCircle(
    category: CategoryItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .width(80.dp)
            .clickable(onClick = onClick)
    ) {
        Card(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape),
            shape = CircleShape
        ) {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(category.image)
                    .crossfade(true)
                    .build(),
                contentDescription = category.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            ) {
                when(painter.state) {
                    is AsyncImagePainter.State.Loading -> {
                        Box(
                            modifier = modifier
                                .fillMaxSize()
                                .background(Color(0xFF0A1929)),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = Color(0xFF2196F3)
                            )
                        }
                    }
                    is AsyncImagePainter.State.Error -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0xFF0A1929)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Failed to load image",
                                color = Color.Gray
                            )
                        }
                    }
                    else -> {
                        SubcomposeAsyncImageContent()
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = category.name.replace("_", " "),
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            color = if (isSelected) Color(0xFFFF5722) else Color.White
        )
    }
}

@Composable
fun ProductsRow(
    modifier: Modifier = Modifier,
    products: List<Product>,
    onProductClick: (String) -> Unit
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 32.dp),
        horizontalArrangement = Arrangement.spacedBy(40.dp)
    ) {
        items(products) { product ->
            ProductCircle(
                product = product,
                onClick = { onProductClick(product.id) }
            )
        }
    }
}

@Composable
fun ProductCircle(
    modifier: Modifier = Modifier,
    product: Product,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .width(300.dp)
            .clickable(onClick = onClick)
    ) {
        Card(modifier = modifier
            .size(300.dp)
            .clip(CircleShape),
            shape = CircleShape,
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(context = LocalContext.current)
                    .data(product.imageLinks.firstOrNull())
                    .crossfade(true)
                    .build(),
                contentDescription = product.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            ) {
                when (painter.state) {
                    is AsyncImagePainter.State.Loading -> {
                        Box(
                            modifier = modifier
                                .fillMaxSize()
                                .background(Color(0xFF0A1929)),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = Color(0xFF2196F3)
                            )
                        }
                    }

                    is AsyncImagePainter.State.Error -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0xFF0A1929)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Failed to load image",
                                color = Color.Gray
                            )
                        }
                    }

                    else -> {
                        SubcomposeAsyncImageContent()
                    }
                }
            }
        }
        Spacer(modifier = modifier.height(16.dp))

        Text(
            text = product.name,
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = modifier.height(8.dp))
        Text(
            text = "₹${product.price}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = colorScheme.primary
        )
    }
}

@Composable
private fun CategoryTopBar(
    modifier: Modifier = Modifier,
    onSearchClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.Transparent
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack,"back", tint = Color.White)
            }
            Text(
                text = "HealthMate",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            IconButton(onClick = onSearchClick) {
                Icon(Icons.Default.Search,"Search", tint = Color.White)
            }
        }
    }
}
