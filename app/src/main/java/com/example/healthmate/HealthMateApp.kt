package com.example.healthmate

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.healthmate.view.AuthScreen
import com.example.healthmate.view.productScreens.CategoryScreen
import com.example.healthmate.view.productScreens.ProductScreen
import com.example.healthmate.viewmodel.AuthViewModel
import com.example.healthmate.viewmodel.ProductViewModel


enum class Screens(val route: String){
    Auth("auth"),
    Home("home"),
    ProductDetail("product/{productId}"),
    Category("category/{categoryName}"),
    Brand("brand/{brandName}")
}


@Composable
fun HealthMateApp() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()
    val productViewModel: ProductViewModel = hiltViewModel()
    val authState by authViewModel.authState.collectAsState()
    val uiState by authViewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn) {
            navController.navigate(Screens.Category.route) {
                popUpTo(Screens.Auth.route) { inclusive = true }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = if (uiState.isLoggedIn) Screens.Category.route else Screens.Auth.route
    ) {
        composable(route = Screens.Auth.route) {
            AuthScreen(
                onLoginSuccess = {
                    navController.navigate(Screens.Category.route) {
                        // Clear the back stack so user can't go back to auth screen
                        popUpTo(Screens.Auth.route) { inclusive = true }
                    }
                }
            )
        }

        composable(route = Screens.Category.route) {
            CategoryScreen(
                onProductClick = { productId ->
                    navController.navigate(Screens.ProductDetail.route.replace("{productId}", productId))
                }
            )
        }

        composable(
            route = Screens.ProductDetail.route,
            arguments = listOf(navArgument("productId") { type = NavType.StringType })
        ) { navBackStackEntry ->
            val productId = navBackStackEntry.arguments?.getString("productId") ?: return@composable
            ProductScreen(
                productID = productId,
                onBackClick = { navController.popBackStack() },
                onAddToCart = { TODO("implement cart functionality") }
            )
        }

//        composable(
//            route = Screens.Category.route,
//            arguments = listOf(navArgument("categoryName") { type = NavType.StringType })
//        ) { navBackStackEntry ->
//            val categoryName = navBackStackEntry.arguments?.getString("categoryName") ?: return@composable
//            // load products by its category
//            LaunchedEffect(categoryName) {
//                productViewModel.loadByCategory(categoryName)
//            }
//            CategoryScreen(
//                onProductClick = { productId ->
//                    navController.navigate(Screens.ProductDetail.route.replace("{productId}", productId))
//                }
//            )
//        }

        composable(
            route = Screens.Brand.route,
            arguments = listOf(navArgument("brandName") { type = NavType.StringType })
        ) { navBackStackEntry ->
            val brandName = navBackStackEntry.arguments?.getString("brandName") ?: return@composable
            // load products by its category
            LaunchedEffect(brandName) {
                productViewModel.loadProductsByBrand(brandName)
            }
            CategoryScreen(
                onProductClick = { productId ->
                    navController.navigate(Screens.ProductDetail.route.replace("{productId}", productId))
                }
            )
        }
    }
}