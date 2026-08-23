package com.example.shoppingbasket.productlist.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shoppingbasket.R
import com.example.shoppingbasket.common.ext.noRippleClickable
import com.example.shoppingbasket.common.view.ShoppingButton
import com.example.shoppingbasket.common.view.ShoppingTopBar
import com.example.shoppingbasket.productlist.viewmodel.ProductListItem
import com.example.shoppingbasket.productlist.viewmodel.ProductListUiState

// 배지 없이 보여줄 총 수량 문자열. 장바구니가 비어 있으면 배지 자체를 그리지 않는다
private const val EMPTY_CART_BADGE_TEXT = "0"
private val CART_BADGE_SIZE = 20.dp
private val CART_BADGE_TEXT_SIZE = 14.sp
private val CART_BADGE_OFFSET_X = 6.dp
private val CART_BADGE_OFFSET_Y = (-6).dp

@Composable
fun ProductListScreen(
    uiState: ProductListUiState,
    onProductClick: (String) -> Unit,
    onCartClick: () -> Unit,
    onLoadMore: () -> Unit,
    onAddToCart: (String) -> Unit,
    onIncrease: (String) -> Unit,
    onDecrease: (String) -> Unit,
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
                CartIconWithBadge(badgeText = uiState.cartBadgeText, onClick = onCartClick)
            }
        )
        ProductGrid(
            products = uiState.products,
            canLoadMore = uiState.canLoadMore,
            onProductClick = onProductClick,
            onLoadMore = onLoadMore,
            onAddToCart = onAddToCart,
            onIncrease = onIncrease,
            onDecrease = onDecrease
        )
    }
}

@Composable
private fun CartIconWithBadge(badgeText: String, onClick: () -> Unit) {
    Box {
        Icon(
            painter = painterResource(R.drawable.ic_shopping_cart),
            contentDescription = stringResource(R.string.cart_icon_description),
            tint = colorResource(R.color.white),
            modifier = Modifier
                .size(24.dp)
                .noRippleClickable(onClick = onClick)
        )
        if (badgeText != EMPTY_CART_BADGE_TEXT) {
            CartBadge(
                text = badgeText,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = CART_BADGE_OFFSET_X, y = CART_BADGE_OFFSET_Y)
            )
        }
    }
}

@Composable
private fun CartBadge(text: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(CART_BADGE_SIZE)
            .background(colorResource(R.color.primary), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = colorResource(R.color.white),
            fontSize = CART_BADGE_TEXT_SIZE,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun ProductGrid(
    products: List<ProductListItem>,
    canLoadMore: Boolean,
    onProductClick: (String) -> Unit,
    onLoadMore: () -> Unit,
    onAddToCart: (String) -> Unit,
    onIncrease: (String) -> Unit,
    onDecrease: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(products, key = { item -> item.key() }) { item ->
            ProductGridItem(
                item = item,
                onAdd = { onAddToCart(item.key()) },
                onIncrease = { onIncrease(item.key()) },
                onDecrease = { onDecrease(item.key()) },
                modifier = Modifier.noRippleClickable { onProductClick(item.key()) }
            )
        }
        // 마지막 페이지까지 읽었으면 더보기 버튼 자체를 두지 않는다
        if (canLoadMore) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                ShoppingButton(text = stringResource(R.string.load_more_button), onClick = onLoadMore)
            }
        }
    }
}
