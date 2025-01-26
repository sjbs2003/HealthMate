package com.example.healthmate.view.cart

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import com.example.healthmate.R
import com.example.healthmate.model.CartItem
import com.example.healthmate.model.OrderStatus
import com.example.healthmate.viewmodel.CartUiState
import com.example.healthmate.viewmodel.CartViewModel
import org.koin.androidx.compose.koinViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    onCheckoutSuccess: () -> Unit,
    viewModel: CartViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "My Cart",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (uiState) {
                is CartUiState.Loading -> {
                    LoadingIndicator()
                }
                is CartUiState.Error -> {
                    ErrorDialog(
                        message = (uiState as CartUiState.Error).message,
                        onDismiss = { viewModel.clearError() }
                    )
                }
                is CartUiState.Success -> {
                    val state = uiState as CartUiState.Success
                    if (state.items.isEmpty()) {
                        EmptyCartMessage(modifier = modifier)
                    } else {
                        CartContent(
                            state = state,
                            onQuantityChange = { productId, quantity ->
                                viewModel.updateQuantity(productId, quantity)
                            },
                            onRemoveItem = { productId ->
                                viewModel.removeFromCart(productId)
                            },
                            onCheckout = {
                                viewModel.checkOut()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyCartMessage(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.ShoppingCart,
            contentDescription = null,
            modifier = modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = modifier.height(16.dp))
        Text(
            text = "Your Cart is Empty",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = modifier.height(8.dp))
        Text(
            text = "Add items to start shopping",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun CartItemCard(
    cartItem: CartItem,
    onQuantityChange: (Int) -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // productImage
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(cartItem.product.thumbnail)
                    .crossfade(true)
                    .build(),
                contentDescription = cartItem.product.name,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
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
            Spacer(modifier = modifier.width(12.dp))

            // productDetails
            Column(modifier = modifier.weight(1f)) {
                Text(
                    text = cartItem.product.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val discountedPrice = cartItem.product.price
                    Text(
                        text = "₹$discountedPrice",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    if (cartItem.product.discountPer > 0) {
                        val originalPrice = cartItem.product.price * 100 / (100 - cartItem.product.discountPer)
                        Text(
                            text = "₹$originalPrice",
                            style = MaterialTheme.typography.bodyMedium,
                            textDecoration = TextDecoration.LineThrough,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }
            Spacer(modifier = modifier.width(12.dp))

            // Quantity Controls
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = { onQuantityChange(cartItem.quantity - 1) },
                    modifier = modifier
                        .size(32.dp)
                        .background(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            CircleShape
                        )
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_minus),
                        contentDescription = "Decrease quantity",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Text(
                    text = cartItem.quantity.toString(),
                    style = MaterialTheme.typography.titleMedium
                )

                IconButton(
                    onClick = { onQuantityChange(cartItem.quantity + 1) },
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Increase quantity",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            IconButton(onClick = onRemove) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remove item",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun LoadingIndicator() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorDialog(
    message: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Error") },
        text = { Text(message) },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}

@Composable
fun CartContent(
    state: CartUiState.Success,
    onQuantityChange: (String, Int) -> Unit,
    onRemoveItem: (String) -> Unit,
    onCheckout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(state.items) { cartItem ->
                CartItemCard(
                    cartItem = cartItem,
                    onQuantityChange = { quantity ->
                        onQuantityChange(cartItem.product.id, quantity)
                    },
                    onRemove = { onRemoveItem(cartItem.product.id) }
                )
            }
        }

        OrderSummaryCard(
            state = state,
            onCheckout = onCheckout
        )
    }
}

@Composable
fun OrderSummaryCard(
    state: CartUiState.Success,
    onCheckout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OrderSummaryRow("Subtotal", "₹${state.subtotal}")
                OrderSummaryRow("Delivery Fee", "₹${state.deliveryFee}")
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))
                OrderSummaryRow(
                    "Total",
                    "₹${state.total}",
                    MaterialTheme.colorScheme.primary,
                    MaterialTheme.typography.titleLarge
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onCheckout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text(
                    text = "Checkout",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun OrderSummaryRow(
    title: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = textStyle
        )
        Text(
            text = value,
            style = textStyle,
            color = valueColor
        )
    }
}

@Composable
fun OrderStatusBadge(status: OrderStatus) {
    val (backgroundColor, textColor, text) = when (status) {
        OrderStatus.PENDING -> Triple(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
            MaterialTheme.colorScheme.primary,
            "Pending"
        )
        OrderStatus.COMPLETED -> Triple(
            Color.Green.copy(alpha = 0.1f),
            Color.Green,
            "Completed"
        )
        OrderStatus.CANCELLED -> Triple(
            MaterialTheme.colorScheme.error.copy(alpha = 0.1f),
            MaterialTheme.colorScheme.error,
            "Cancelled"
        )
    }

    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            color = textColor,
            style = MaterialTheme.typography.labelMedium
        )
    }
}