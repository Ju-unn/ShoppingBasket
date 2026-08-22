package com.example.shoppingbasket

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.shoppingbasket.basket.view.BasketScreen
import com.example.shoppingbasket.basket.viewmodel.BasketViewModel
import com.example.shoppingbasket.productdetail.view.ProductDetailScreen
import com.example.shoppingbasket.productdetail.viewmodel.ProductDetailViewModel
import com.example.shoppingbasket.productlist.view.ProductListScreen
import com.example.shoppingbasket.productlist.viewmodel.ProductListViewModel
import com.example.shoppingbasket.ui.theme.ShoppingBasketTheme

private const val ROUTE_PRODUCT_LIST = "productList"
private const val ROUTE_PRODUCT_DETAIL = "productDetail/{productId}"
private const val ROUTE_BASKET = "basket"
private const val ARG_PRODUCT_ID = "productId"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ShoppingBasketTheme {
                ShoppingNavHost()
            }
        }
    }
}

@Composable
private fun ShoppingNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = ROUTE_PRODUCT_LIST) {
        composable(ROUTE_PRODUCT_LIST) {
            ProductListRoute(navController = navController)
        }
        composable(
            route = ROUTE_PRODUCT_DETAIL,
            arguments = listOf(navArgument(ARG_PRODUCT_ID) { type = NavType.StringType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString(ARG_PRODUCT_ID) ?: return@composable
            ProductDetailRoute(productId = productId, navController = navController)
        }
        composable(ROUTE_BASKET) {
            BasketRoute(navController = navController)
        }
    }
}

@Composable
private fun ProductListRoute(navController: NavHostController, viewModel: ProductListViewModel = viewModel()) {
    val uiState by viewModel.uiState
    ProductListScreen(
        uiState = uiState,
        onProductClick = { productId -> navController.navigate("productDetail/$productId") },
        onCartClick = { navController.navigate(ROUTE_BASKET) },
        onLoadMore = { viewModel.loadMore() }
    )
}

@Composable
private fun ProductDetailRoute(productId: String, navController: NavHostController) {
    val viewModel: ProductDetailViewModel = viewModel(
        factory = viewModelFactory { initializer { ProductDetailViewModel(productId) } }
    )
    val uiState by viewModel.uiState
    ProductDetailScreen(
        uiState = uiState,
        onClose = { navController.popBackStack() },
        onAddToCart = { viewModel.addToCart() }
    )
}

@Composable
private fun BasketRoute(navController: NavHostController, viewModel: BasketViewModel = viewModel()) {
    val uiState by viewModel.uiState
    BasketScreen(
        uiState = uiState,
        onBack = { navController.popBackStack() },
        onRemove = { key -> viewModel.remove(key) },
        onPreviousPage = { viewModel.previousPage() },
        onNextPage = { viewModel.nextPage() }
    )
}
