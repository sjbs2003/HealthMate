package com.example.healthmate


import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.healthmate.view.AuthScreen
import com.example.healthmate.view.cart.CartScreen
import com.example.healthmate.view.chat.ChatScreen
import com.example.healthmate.view.product.CategoryScreen
import com.example.healthmate.view.product.HomeScreen
import com.example.healthmate.view.product.ProductScreen
import com.example.healthmate.viewmodel.AuthState
import com.example.healthmate.viewmodel.AuthViewModel
import com.example.healthmate.viewmodel.CartViewModel
import com.example.healthmate.viewmodel.ChatViewModel
import com.example.healthmate.viewmodel.ProductViewModel
import org.koin.androidx.compose.koinViewModel


enum class Screens(val route: String){
    Auth("auth"),
    Home("home"),
    ProductDetail("product/{productId}"),
    Category("category/{categoryName}"),
    Brand("brand/{brandName}"),
    Cart("cart"),
    Chat("chat")
}


@Composable
fun HealthMateApp() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = koinViewModel()
    val productViewModel: ProductViewModel = koinViewModel()
    val cartViewModel: CartViewModel = koinViewModel()
    val chatViewModel: ChatViewModel = koinViewModel()
    val authState by authViewModel.authState.collectAsState()
    val uiState by authViewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isLoggedIn, authState) {
        when(authState) {
            is AuthState.LoggedOut -> {
                navController.navigate(Screens.Auth.route) {
                    popUpTo(0) { inclusive = true } // Clear entire back stack
                }
            }
            is AuthState.Success -> {
                if (uiState.isLoggedIn) {
                    navController.navigate(Screens.Home.route) {
                        popUpTo(Screens.Auth.route) { inclusive = true }
                    }
                }
            }
            else -> {}
        }
    }

    NavHost(
        navController = navController,
        startDestination = if (uiState.isLoggedIn) Screens.Home.route else Screens.Auth.route
    ) {
        composable(route = Screens.Auth.route) {
            AuthScreen(
                onLoginSuccess = {
                    navController.navigate(Screens.Home.route) {
                        // Clear the back stack so user can't go back to auth screen
                        popUpTo(Screens.Auth.route) { inclusive = true }
                    }
                }
            )
        }

        composable(route = Screens.Home.route) {
            HomeScreen(
                onProductClick = { productId ->
                    navController.navigate(Screens.ProductDetail.route.replace("{productId}", productId))
                },
                onCategoryClick = { categoryName ->
                    navController.navigate(Screens.Category.route.replace("{categoryName}", categoryName))
                },
                navController = navController
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
                onCartClick = { navController.navigate(Screens.Cart.route) }
            )
        }

        composable(
            route = Screens.Category.route,
            arguments = listOf(navArgument("categoryName") { type = NavType.StringType })
        ) { navBackStackEntry ->
            val categoryName = navBackStackEntry.arguments?.getString("categoryName") ?: return@composable
            // Set the category and load products immediately
            LaunchedEffect(categoryName) {
                productViewModel.loadByCategory(categoryName)
            }
            CategoryScreen(
                onProductClick = { productId ->
                    navController.navigate(Screens.ProductDetail.route.replace("{productId}", productId))
                },
                onBackClick = { navController.popBackStack() }
            )
        }

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
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(route = Screens.Cart.route) {
            CartScreen(
                onBackClick = { navController.popBackStack() },
                onCheckoutSuccess = {
                    // After successful checkout, navigate back to home
                    navController.navigate(Screens.Home.route) {
                        popUpTo(Screens.Cart.route) { inclusive = true }
                    }
                }
            )
        }

        composable(route = Screens.Chat.route) {
            ChatScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

    }
}