package com.example.shoppingbasket.productdetail.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.shoppingbasket.R
import com.example.shoppingbasket.productdetail.viewmodel.ProductDetailUiState
import com.example.shoppingbasket.productlist.model.Product

@Composable
fun ProductDetailScreen(
    uiState: ProductDetailUiState,
    onClose: () -> Unit,
    onAddToCart: () -> Unit,
    modifier: Modifier = Modifier
) {
    val product = uiState.product ?: return
    Column(modifier = modifier.fillMaxSize().background(colorResource(R.color.white))) {
        ProductDetailTopBar(onClose = onClose)
        Column(modifier = Modifier.weight(1f)) {
            ProductDetailImage(imageUrl = product.displayImageUrl())
            ProductDetailInfo(product = product)
        }
        AddToCartButton(onClick = onAddToCart)
    }
}

@Composable
private fun ProductDetailTopBar(onClose: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(colorResource(R.color.gray_5))
            .padding(end = 26.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_close),
            contentDescription = stringResource(R.string.close_icon_description),
            tint = colorResource(R.color.white),
            modifier = Modifier
                .size(16.dp)
                .clickable(onClick = onClose)
        )
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
private fun ProductDetailInfo(product: Product, modifier: Modifier = Modifier) {
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
        PriceRow(displayPrice = product.displayPrice())
    }
}

@Composable
private fun PriceRow(displayPrice: String, modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.price_label),
            fontSize = 20.sp,
            color = colorResource(R.color.black),
            modifier = Modifier.weight(1f)
        )
        Text(
            text = displayPrice,
            fontSize = 20.sp,
            color = colorResource(R.color.black),
            textAlign = TextAlign.End
        )
    }
}

@Composable
private fun AddToCartButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(colorResource(R.color.primary))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.add_to_cart_button),
            color = colorResource(R.color.white),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
