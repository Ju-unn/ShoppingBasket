package com.example.shoppingbasket.basket.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.shoppingbasket.basket.model.CartItem
import com.example.shoppingbasket.basket.viewmodel.BasketUiState
import com.example.shoppingbasket.common.ext.noRippleClickable
import com.example.shoppingbasket.common.view.ShoppingTopBar

// 디자인(.pen) 기준 페이징 컨트롤은 화면 아래에서 50dp 띄워 가운데 놓는다
private val PAGING_BOTTOM_MARGIN = 50.dp

// 빈 장바구니 안내는 pen 에 대응 프레임이 없어 기존 타이포(상품명 18sp)와 gray_5 를 그대로 따랐다
private val EMPTY_MESSAGE_SIZE = 18.sp

@Composable
fun BasketScreen(
    uiState: BasketUiState,
    onBack: () -> Unit,
    onRemove: (String) -> Unit,
    onPreviousPage: () -> Unit,
    onNextPage: () -> Unit,
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
        CartContent(cartItems = uiState.cartItems, onRemove = onRemove, modifier = Modifier.weight(1f))
        PagingArea(uiState = uiState, onPreviousPage = onPreviousPage, onNextPage = onNextPage)
    }
}

// 페이지가 하나뿐이면(상품 5개 이하) 디자인에도 컨트롤이 없으므로 아예 그리지 않는다
@Composable
private fun ColumnScope.PagingArea(uiState: BasketUiState, onPreviousPage: () -> Unit, onNextPage: () -> Unit) {
    if (!uiState.canGoPrevious && !uiState.canGoNext) {
        return
    }
    CartPagingControl(
        pageNumber = uiState.pageNumber,
        canGoPrevious = uiState.canGoPrevious,
        canGoNext = uiState.canGoNext,
        onPrevious = onPreviousPage,
        onNext = onNextPage,
        modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .padding(bottom = PAGING_BOTTOM_MARGIN)
    )
}

// 담긴 상품이 없으면 목록 대신 안내 문구를 보여준다
@Composable
private fun CartContent(cartItems: List<CartItem>, onRemove: (String) -> Unit, modifier: Modifier = Modifier) {
    if (cartItems.isEmpty()) {
        EmptyCartMessage(modifier = modifier)
        return
    }
    CartItemList(cartItems = cartItems, onRemove = onRemove, modifier = modifier)
}

@Composable
private fun EmptyCartMessage(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = stringResource(R.string.empty_cart_message),
            color = colorResource(R.color.gray_5),
            fontSize = EMPTY_MESSAGE_SIZE,
            fontWeight = FontWeight.Medium
        )
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
