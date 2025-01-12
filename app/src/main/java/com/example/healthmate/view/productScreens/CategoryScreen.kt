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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.healthmate.R
import com.example.healthmate.model.CategoryItem
import com.example.healthmate.model.Product
import com.example.healthmate.viewmodel.ProductUiState
import com.example.healthmate.viewmodel.ProductViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext

@Composable
fun CategoryScreen(
    modifier: Modifier = Modifier,
    onProductClick: (String) -> Unit,
    onBackClick: () -> Unit,
    viewModel: ProductViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var showSearchbar by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(top = 24.dp)
    ) {
        TopBar(
            onMenuClick = { scope.launch { drawerState.open() } },
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
                    CategoriesRow(
                        categories = state.categories,
                        selectedCategory = selectedCategory,
                        onCategoryClick = { category ->
                            selectedCategory = category
                            viewModel.loadByCategory(category)
                        },
                        modifier = modifier.padding(vertical = 16.dp)
                    )
                }
                Spacer(modifier = modifier.height(32.dp))

                Box(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    // shows filtered products
                    ProductsRow(
                        products = state.products,
                        onProductClick = onProductClick
                    )
                }
            }
        }
    }
}

@Composable
fun CategoriesRow(
    categories: List<CategoryItem>,
    selectedCategory: String?,
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
                isSelected = category.name == selectedCategory,
                onClick = { onCategoryClick(category.name) }
            )
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
            shape = CircleShape,
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(category.image)
                    .crossfade(true)
                    .build(),
                contentDescription = category.name,
                modifier = Modifier.fillMaxSize(),
                error = painterResource(R.drawable.ic_broken_image),
                placeholder = painterResource(R.drawable.loading_img),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = category.name.replace("_", " "),
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )

        // indicator for category selected
        if (isSelected) {
            Spacer(modifier = modifier.height(4.dp))
            Box(
                modifier = modifier
                    .width(40.dp)
                    .height(2.dp)
                    .background(
                        color = Color(0xFFFF5722),
                        shape = RoundedCornerShape(1.dp)
                    )
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
)   {
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
            AsyncImage(
                model = ImageRequest.Builder(context = LocalContext.current)
                    .data(product.imageLinks.firstOrNull())
                    .crossfade(true)
                    .build(),
                contentDescription = product.name,
                error = painterResource(R.drawable.ic_broken_image),
                placeholder = painterResource(R.drawable.loading_img),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
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
