package com.example.shoppingbasket.productdetail.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.shoppingbasket.R
import com.example.shoppingbasket.common.ext.noRippleClickable
import com.example.shoppingbasket.common.model.Money
import com.example.shoppingbasket.common.view.ShoppingButton
import com.example.shoppingbasket.common.view.ShoppingTopBar
import com.example.shoppingbasket.common.view.Stepper
import com.example.shoppingbasket.productdetail.viewmodel.ProductDetailUiState
import com.example.shoppingbasket.productlist.domain.model.Product
import com.example.shoppingbasket.productlist.domain.model.ProductId
import com.example.shoppingbasket.productlist.domain.model.ProductImageUrl
import com.example.shoppingbasket.productlist.domain.model.ProductName
import com.example.shoppingbasket.ui.theme.ShoppingBasketTheme

@Composable
fun ProductDetailScreen(
    uiState: ProductDetailUiState,
    onClose: () -> Unit,
    onAddToCart: () -> Unit,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onLastViewedClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val product = uiState.product ?: return
    Column(modifier = modifier.fillMaxSize().background(colorResource(R.color.white))) {
        ShoppingTopBar(
            trailingIcon = {
                Icon(
                    painter = painterResource(R.drawable.ic_close),
                    contentDescription = stringResource(R.string.close_icon_description),
                    tint = colorResource(R.color.white),
                    modifier = Modifier
                        .size(16.dp)
                        .noRippleClickable(onClick = onClose)
                )
            }
        )
        Column(modifier = Modifier.weight(1f)) {
            ProductDetailImage(imageUrl = product.displayImageUrl())
            ProductDetailInfo(
                product = product,
                uiState = uiState,
                onIncrease = onIncrease,
                onDecrease = onDecrease,
                onLastViewedClick = onLastViewedClick
            )
        }
        AddToCartButton(onClick = onAddToCart)
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun ProductDetailImage(imageUrl: String, modifier: Modifier = Modifier) {
    GlideImage(
        model = imageUrl,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
    )
}

@Composable
private fun ProductDetailInfo(
    product: Product,
    uiState: ProductDetailUiState,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onLastViewedClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(horizontal = 18.dp)) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = product.displayName(),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = colorResource(R.color.black)
        )
        Spacer(modifier = Modifier.height(17.dp))
        HorizontalDivider(color = colorResource(R.color.gray_border), thickness = 1.dp)
        Spacer(modifier = Modifier.height(16.dp))
        PriceAndQuantityRow(uiState = uiState, onIncrease = onIncrease, onDecrease = onDecrease)
        LastViewedSection(product = uiState.lastViewedProduct, onClick = onLastViewedClick)
    }
}

@Composable
private fun LastViewedSection(product: Product?, onClick: (String) -> Unit, modifier: Modifier = Modifier) {
    // 마지막으로 본 상품이 없으면(자기 자신 제외 후 없음) 카드를 그리지 않는다 (4단계 pen 스펙)
    if (product == null) {
        return
    }
    Spacer(modifier = modifier.height(20.dp))
    LastViewedProductCard(product = product, onClick = { onClick(product.key()) })
}

@Composable
private fun LastViewedProductCard(product: Product, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
            .border(1.dp, colorResource(R.color.gray_border), RoundedCornerShape(4.dp))
            .noRippleClickable(onClick = onClick)
            .padding(start = 18.dp, top = 16.dp)
    ) {
        Text(
            text = stringResource(R.string.last_viewed_section_title),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = colorResource(R.color.primary)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = product.displayName(), fontSize = 18.sp, color = colorResource(R.color.gray_5))
    }
}

@Composable
private fun PriceAndQuantityRow(
    uiState: ProductDetailUiState,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = uiState.totalPriceDisplayValue(),
            fontSize = 20.sp,
            color = colorResource(R.color.black),
            modifier = Modifier.weight(1f)
        )
        Stepper(
            quantityText = uiState.quantityDisplayValue(),
            onIncrease = onIncrease,
            onDecrease = onDecrease
        )
    }
}

@Composable
private fun AddToCartButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    ShoppingButton(
        text = stringResource(R.string.add_to_cart_button),
        onClick = onClick,
        modifier = modifier
    )
}

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
private fun ProductDetailScreenWithLastViewedPreview() {
    ShoppingBasketTheme {
        ProductDetailScreen(
            uiState = ProductDetailUiState(
                product = previewProduct("메인 상품", "https://picsum.photos/seed/main/600"),
                lastViewedProduct = previewProduct("PET보틀-정사각(500ml)", "https://picsum.photos/seed/p1/600")
            ),
            onClose = {},
            onAddToCart = {},
            onIncrease = {},
            onDecrease = {},
            onLastViewedClick = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
private fun ProductDetailScreenWithoutLastViewedPreview() {
    ShoppingBasketTheme {
        ProductDetailScreen(
            uiState = ProductDetailUiState(product = previewProduct("메인 상품", "https://picsum.photos/seed/main/600")),
            onClose = {},
            onAddToCart = {},
            onIncrease = {},
            onDecrease = {},
            onLastViewedClick = {}
        )
    }
}

private fun previewProduct(name: String, imageUrl: String): Product {
    return Product(ProductId("preview"), ProductName(name), Money(10_000), ProductImageUrl(imageUrl))
}
