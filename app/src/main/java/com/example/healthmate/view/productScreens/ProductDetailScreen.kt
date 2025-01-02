package com.example.healthmate.view.productScreens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.healthmate.viewmodel.ProductViewModel

@Composable
fun ProductScreen(
    modifier: Modifier = Modifier,
    productID: String,
    onBackClick: () -> Unit,
    onAddToCart: () -> Unit,
    viewModel: ProductViewModel = viewModel()
) {

    val selectedProduct  by viewModel.selectedProduct.collectAsState()
}