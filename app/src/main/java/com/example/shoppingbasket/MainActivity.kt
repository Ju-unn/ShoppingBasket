package com.example.shoppingbasket

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.shoppingbasket.productlist.view.ProductListScreen
import com.example.shoppingbasket.productlist.viewmodel.ProductListViewModel
import com.example.shoppingbasket.ui.theme.ShoppingBasketTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ShoppingBasketTheme {
                ProductListRoute()
            }
        }
    }
}

@Composable
private fun ProductListRoute(viewModel: ProductListViewModel = viewModel()) {
    val uiState by viewModel.uiState
    ProductListScreen(uiState = uiState)
}
