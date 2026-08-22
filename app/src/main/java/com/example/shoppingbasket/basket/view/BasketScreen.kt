package com.example.shoppingbasket.basket.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.shoppingbasket.R
import com.example.shoppingbasket.basket.model.CartItem
import com.example.shoppingbasket.basket.viewmodel.BasketUiState
import com.example.shoppingbasket.common.ext.noRippleClickable
import com.example.shoppingbasket.common.view.ShoppingTopBar

@Composable
fun BasketScreen(
    uiState: BasketUiState,
    onBack: () -> Unit,
    onRemove: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize().background(colorResource(R.color.white))) {
        ShoppingTopBar(
            title = stringResource(R.string.cart_title),
            leadingIcon = {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .noRippleClickable(onClick = onBack),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_arrow_back),
                        contentDescription = stringResource(R.string.back_icon_description),
                        tint = colorResource(R.color.white),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        )
        CartItemList(cartItems = uiState.cartItems, onRemove = onRemove)
    }
}

@Composable
private fun CartItemList(cartItems: List<CartItem>, onRemove: (String) -> Unit, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 24.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        items(cartItems, key = { cartItem -> cartItem.key() }) { cartItem ->
            CartItemCard(cartItem = cartItem, onRemove = { onRemove(cartItem.key()) })
        }
    }
}
