package com.example.healthmate.view.productScreens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.healthmate.R
import com.example.healthmate.model.Product

@Composable
fun ProductCard(
    modifier: Modifier = Modifier,
    product: Product,
    onClick: () -> Unit
) {
   Card(
       modifier = modifier
           .fillMaxWidth()
           .clickable(onClick = onClick)
           .padding(8.dp),
       shape = RoundedCornerShape(12.dp),
       elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
   ) {
       Column {
           AsyncImage(
               model = ImageRequest.Builder(context = LocalContext.current)
                   .data(product.imageLinks.firstOrNull())
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
           Column(modifier = modifier.padding(12.dp)) {
               Text(
                   text = product.name,
                   style = MaterialTheme.typography.titleMedium,
                   maxLines = 2,
                   overflow = TextOverflow.Ellipsis
               )
               Spacer(modifier = modifier.height(4.dp))

               Text(
                   text = "${product.price}",
                   style = MaterialTheme.typography.titleLarge,
                   fontWeight = FontWeight.Bold,
                   color = MaterialTheme.colorScheme.primary
               )
               Spacer(modifier = modifier.height(4.dp))

               Text(
                   text = product.brand,
                   style = MaterialTheme.typography.bodyMedium,
                   color = MaterialTheme.colorScheme.onSurfaceVariant
               )
           }
       }
   }
}

@Composable
fun SortingBar(
    modifier: Modifier = Modifier,
    onSortChange: (String, String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        Row(modifier = modifier.clickable { expanded = !expanded },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Sort")
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Dropdown Icon"
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                onClick = {
                    onSortChange("asc","price")
                    expanded = false
                },
                text = { Text("Price: Low To High") }
            )

            DropdownMenuItem(
                onClick = {
                    onSortChange("desc","price")
                    expanded = false
                },
                text = { Text("Price: High To Low") }
            )

            DropdownMenuItem(
                onClick = {
                    onSortChange("asc","name")
                    expanded = false
                },
                text = { Text("Name: A_Z") }
            )
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        placeholder = { Text("Search Products") },
        trailingIcon = {
            IconButton(
                onClick = onSearch,
                enabled = query.length >= 3
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search"
                )
            }
        },
        singleLine = true
    )
}