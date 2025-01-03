package com.example.healthmate

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.healthmate.view.AuthScreen
import com.example.healthmate.view.productScreens.HomeScreen
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

    NavHost(
        navController = navController,
        startDestination = Screens.Auth.route
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
                onBrandClick = { brandName ->
                    navController.navigate(Screens.Brand.route.replace("{brandName}", brandName))

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

        composable(
            route = Screens.Category.route,
            arguments = listOf(navArgument("categoryName") { type = NavType.StringType })
        ) { navBackStackEntry ->
            val categoryName = navBackStackEntry.arguments?.getString("categoryName") ?: return@composable
            // load products by its category
            LaunchedEffect(categoryName) {
                productViewModel.loadByCategory(categoryName)
            }
            HomeScreen(
                onProductClick = { productId ->
                    navController.navigate(Screens.ProductDetail.route.replace("{productId}", productId))
                },
                onCategoryClick = { category ->
                    navController.navigate(Screens.Category.route.replace("{categoryName}", category))
                },
                onBrandClick = { brandName ->
                    navController.navigate(Screens.Brand.route.replace("{brandName}", brandName))

                }
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
            HomeScreen(
                onProductClick = { productId ->
                    navController.navigate(Screens.ProductDetail.route.replace("{productId}", productId))
                },
                onCategoryClick = { categoryName ->
                    navController.navigate(Screens.Category.route.replace("{categoryName}", categoryName))
                },
                onBrandClick = { brand ->
                    navController.navigate(Screens.Brand.route.replace("{brandName}", brand))

                }
            )
        }
    }
}