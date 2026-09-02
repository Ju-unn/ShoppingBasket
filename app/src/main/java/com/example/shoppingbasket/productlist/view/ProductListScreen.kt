package com.example.shoppingbasket.productlist.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.example.shoppingbasket.R
import com.example.shoppingbasket.common.ext.noRippleClickable
import com.example.shoppingbasket.common.view.ShoppingButton
import com.example.shoppingbasket.common.view.ShoppingTopBar
import com.example.shoppingbasket.productlist.domain.model.Product
import com.example.shoppingbasket.productlist.viewmodel.ProductListItem
import com.example.shoppingbasket.productlist.viewmodel.ProductListUiState

private val RECENTLY_VIEWED_ITEM_SIZE = 98.dp

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
        // 서버 응답을 받아오는 중이면 목록 대신 로딩 인디케이터만 보여준다 (5단계 확정 사항)
        if (uiState.isLoading) {
            LoadingIndicator()
            return@Column
        }
        // 최근 본 상품이 없으면 섹션 자체를 그리지 않는다 (4단계 pen 스펙)
        if (uiState.recentlyViewed.isNotEmpty()) {
            RecentlyViewedSection(products = uiState.recentlyViewed, onProductClick = onProductClick)
        }
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
private fun LoadingIndicator(modifier: Modifier = Modifier) {
    val description = stringResource(R.string.loading_products_description)
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            color = colorResource(R.color.primary),
            modifier = Modifier.semantics { contentDescription = description }
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

@Composable
private fun RecentlyViewedSection(products: List<Product>, onProductClick: (String) -> Unit) {
    Column {
        Text(
            text = stringResource(R.string.recently_viewed_section_title),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = colorResource(R.color.black),
            modifier = Modifier.padding(start = 20.dp, top = 20.dp, bottom = 12.dp)
        )
        // 4번째 아이템이 프레임 폭을 넘어 잘려 보이는 pen 레이아웃 → 가로 스크롤 목록
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(products, key = { product -> product.key() }) { product ->
                RecentlyViewedItem(product = product, onClick = { onProductClick(product.key()) })
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        HorizontalDivider(thickness = 7.dp, color = colorResource(R.color.gray_divider))
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun RecentlyViewedItem(product: Product, onClick: () -> Unit) {
    val skeletonPlaceholder = placeholder(ColorPainter(colorResource(R.color.gray_skeleton)))
    Column(modifier = Modifier.width(RECENTLY_VIEWED_ITEM_SIZE).noRippleClickable(onClick = onClick)) {
        GlideImage(
            model = product.displayImageUrl(),
            contentDescription = product.displayName(),
            contentScale = ContentScale.Crop,
            loading = skeletonPlaceholder,
            failure = skeletonPlaceholder,
            modifier = Modifier.size(RECENTLY_VIEWED_ITEM_SIZE)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = product.displayName(),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = colorResource(R.color.gray_3),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
