package com.example.shoppingbasket.productlist.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.shoppingbasket.R
import com.example.shoppingbasket.common.view.ShoppingTopBar
import com.example.shoppingbasket.productlist.model.Product
import com.example.shoppingbasket.productlist.viewmodel.ProductListUiState

@Composable
fun ProductListScreen(
    uiState: ProductListUiState,
    onProductClick: (String) -> Unit,
    onCartClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colorResource(R.color.white))
    ) {
        ShoppingTopBar(
            title = stringResource(R.string.product_list_title),
            trailingIcon = {
                Icon(
                    painter = painterResource(R.drawable.ic_shopping_cart),
                    contentDescription = stringResource(R.string.cart_icon_description),
                    tint = colorResource(R.color.white),
                    modifier = Modifier
                        .size(24.dp)
                        .clickable(onClick = onCartClick)
                )
            }
        )
        ProductGrid(products = uiState.products, onProductClick = onProductClick)
    }
}

@Composable
private fun ProductGrid(products: List<Product>, onProductClick: (String) -> Unit, modifier: Modifier = Modifier) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(products, key = { product -> product.key() }) { product ->
            ProductGridItem(product, modifier = Modifier.clickable { onProductClick(product.key()) })
        }
    }
}
