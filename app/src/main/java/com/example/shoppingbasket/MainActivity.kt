package com.example.shoppingbasket

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.shoppingbasket.basket.model.CartRepository
import com.example.shoppingbasket.basket.view.BasketScreen
import com.example.shoppingbasket.basket.viewmodel.BasketViewModel
import com.example.shoppingbasket.productdetail.view.ProductDetailScreen
import com.example.shoppingbasket.productdetail.viewmodel.ProductDetailViewModel
import com.example.shoppingbasket.productlist.domain.repository.ProductRepository
import com.example.shoppingbasket.productlist.view.ProductListScreen
import com.example.shoppingbasket.productlist.viewmodel.ProductListViewModel
import com.example.shoppingbasket.recentlyviewed.model.RecentlyViewedRepository
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
private fun ProductListRoute(navController: NavHostController) {
    val viewModel: ProductListViewModel = viewModel(
        factory = viewModelFactory {
            initializer { ProductListViewModel(cartRepository(), recentlyViewedRepository(), productRepository()) }
        }
    )
    val uiState by viewModel.uiState
    ProductListScreen(
        uiState = uiState,
        onProductClick = { productId -> navController.navigate("productDetail/$productId") },
        onCartClick = { navController.navigate(ROUTE_BASKET) },
        onLoadMore = { viewModel.loadMore() },
        onAddToCart = { key -> viewModel.addToCart(key) },
        onIncrease = { key -> viewModel.increase(key) },
        onDecrease = { key -> viewModel.decrease(key) },
        onRetry = { viewModel.retry() }
    )
}

@Composable
private fun ProductDetailRoute(productId: String, navController: NavHostController) {
    val viewModel: ProductDetailViewModel = viewModel(
        factory = viewModelFactory {
            initializer { ProductDetailViewModel(productId, cartRepository(), recentlyViewedRepository(), productRepository()) }
        }
    )
    val uiState by viewModel.uiState
    ProductDetailScreen(
        uiState = uiState,
        onClose = { navController.popBackStack() },
        onAddToCart = { viewModel.addToCart() },
        onIncrease = { viewModel.increase() },
        onDecrease = { viewModel.decrease() },
        // "마지막으로 본 상품" 카드로 들어간 상세는 뒤로가기 한 번에 항상 상품 목록으로 가야 한다 (4단계 결정 8번)
        onLastViewedClick = { lastViewedProductId ->
            navController.navigate("productDetail/$lastViewedProductId") { popUpTo(ROUTE_PRODUCT_LIST) }
        }
    )
}

@Composable
private fun BasketRoute(navController: NavHostController) {
    val viewModel: BasketViewModel = viewModel(
        factory = viewModelFactory { initializer { BasketViewModel(cartRepository()) } }
    )
    val uiState by viewModel.uiState
    BasketScreen(
        uiState = uiState,
        onBack = { navController.popBackStack() },
        onRemove = { key -> viewModel.remove(key) },
        onIncrease = { key -> viewModel.increase(key) },
        onDecrease = { key -> viewModel.decrease(key) },
        onPreviousPage = { viewModel.previousPage() },
        onNextPage = { viewModel.nextPage() }
    )
}

// ViewModel 팩토리 안에서 Application 이 들고 있는 공용 장바구니 저장소를 꺼낸다
private fun CreationExtras.cartRepository(): CartRepository {
    val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]
    return (application as ShoppingBasketApplication).cartRepository
}

// ViewModel 팩토리 안에서 Application 이 들고 있는 공용 최근 본 상품 저장소를 꺼낸다
private fun CreationExtras.recentlyViewedRepository(): RecentlyViewedRepository {
    val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]
    return (application as ShoppingBasketApplication).recentlyViewedRepository
}

// ViewModel 팩토리 안에서 Application 이 들고 있는 공용 상품 저장소를 꺼낸다.
// 목록 화면이 받아온 캐시를 상세 화면이 그대로 재사용하려면 같은 인스턴스를 써야 한다 (4단계 결정 5번)
private fun CreationExtras.productRepository(): ProductRepository {
    val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]
    return (application as ShoppingBasketApplication).productRepository
}
