package com.example.healthmate.view.productScreens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.healthmate.R
import com.example.healthmate.model.Product
import com.example.healthmate.viewmodel.ProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductScreen(
    modifier: Modifier = Modifier,
    productID: String,
    onBackClick: () -> Unit,
    onAddToCart: (Product) -> Unit,
    viewModel: ProductViewModel = hiltViewModel()
) {
    val selectedProduct  by viewModel.selectedProduct.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Product Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        selectedProduct?.let { product ->
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                // Image Carousel
                val pagerState = rememberPagerState(pageCount = {product.imageLinks.size})

                HorizontalPager(
                    state = pagerState,
                    modifier = modifier
                        .fillMaxWidth()
                        .height(300.dp)
                ) { page ->
                    AsyncImage(
                        model = ImageRequest.Builder(context = LocalContext.current)
                            .data(product.imageLinks[page])
                            .crossfade(true)
                            .build(),
                        contentDescription = product.name,
                        error = painterResource(R.drawable.ic_broken_image),
                        placeholder = painterResource(R.drawable.loading_img),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                        contentScale = ContentScale.Crop
                    )
                }

                // Product Details
                Column(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = modifier.height(8.dp))

                    Text(
                        text = "${product.price}",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = modifier.height(16.dp))

                    Text(
                        text = "Brand: ${product.brand}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = modifier.height(8.dp))

                    Text(
                        text = product.description,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = modifier.height(16.dp))

                    // Product Details
                    Card(
                        modifier = modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(modifier = modifier.padding(16.dp)) {
                            Text(
                                text = "Product Details",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = modifier.height(8.dp))

                            Text("Type: ${product.type.name}")
                            Text("Form: ${product.productType.name}")
                            Text("Quantity: ${product.quantity}")
                            Text("Categories: ${product.categories.joinToString { "," }}")
                        }
                    }
                    Spacer(modifier = modifier.height(24.dp))

                    Button(
                        onClick = { onAddToCart(product) },
                        modifier = modifier.fillMaxWidth()
                    ) {
                        Text("Add To Cart")
                    }
                }
            }
        } ?: run {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }

}