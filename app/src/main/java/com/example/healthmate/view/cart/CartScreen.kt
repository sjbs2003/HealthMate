package com.example.healthmate.view.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import com.example.healthmate.model.CartItem


@Composable
fun EmptyCardMessage(modifier: Modifier = Modifier) {
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
                    .data(cartItem.product.imageLinks.firstOrNull())
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
        }

        Spacer(modifier = modifier.width(12.dp))

        // productDetails
        Column(modifier = modifier.weight(1f)) {

        }
    }
}