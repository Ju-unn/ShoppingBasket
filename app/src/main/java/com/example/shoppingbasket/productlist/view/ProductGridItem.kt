package com.example.shoppingbasket.productlist.view

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
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
import com.example.shoppingbasket.common.view.Stepper
import com.example.shoppingbasket.productlist.viewmodel.ProductListItem

// 장바구니에 없을 때 사진 우하단에 뜨는 담기 버튼. pen 기준 48dp 흰 원 + gray_5 선 2개(굵기 3, round cap)
private val ADD_BUTTON_SIZE = 48.dp
private val ADD_BUTTON_LINE_LENGTH = 14.dp
private val ADD_BUTTON_LINE_WIDTH = 3.dp

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ProductGridItem(
    item: ProductListItem,
    onAdd: () -> Unit,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
        ) {
            ProductImage(item)
            CartControl(item, onAdd, onIncrease, onDecrease)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = item.displayName(),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = colorResource(R.color.black),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = item.displayPrice(),
            fontSize = 16.sp,
            color = colorResource(R.color.gray_5)
        )
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun ProductImage(item: ProductListItem) {
    val skeletonPlaceholder = placeholder(ColorPainter(colorResource(R.color.gray_skeleton)))
    GlideImage(
        model = item.displayImageUrl(),
        contentDescription = item.displayName(),
        contentScale = ContentScale.Crop,
        loading = skeletonPlaceholder,
        failure = skeletonPlaceholder,
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(8.dp))
    )
}

// 장바구니에 없으면 담기 버튼(사진 우하단), 담겨 있으면 Stepper(사진 하단 중앙)를 보여준다
@Composable
private fun BoxScope.CartControl(item: ProductListItem, onAdd: () -> Unit, onIncrease: () -> Unit, onDecrease: () -> Unit) {
    if (!item.isInCart()) {
        AddToCartButton(
            onClick = onAdd,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(8.dp)
        )
        return
    }
    Stepper(
        quantityText = item.cartQuantityDisplayValue(),
        onIncrease = onIncrease,
        onDecrease = onDecrease,
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(bottom = 8.dp)
    )
}

@Composable
private fun AddToCartButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    val lineColor = colorResource(R.color.gray_5)
    val description = stringResource(R.string.add_to_cart_icon_description)
    Canvas(
        modifier = modifier
            .size(ADD_BUTTON_SIZE)
            .background(colorResource(R.color.white), CircleShape)
            .semantics { contentDescription = description }
            .noRippleClickable(onClick = onClick)
    ) {
        drawPlusSign(lineColor)
    }
}

private fun DrawScope.drawPlusSign(color: Color) {
    val half = ADD_BUTTON_LINE_LENGTH.toPx() / 2
    val strokeWidth = ADD_BUTTON_LINE_WIDTH.toPx()
    drawLine(color, Offset(center.x - half, center.y), Offset(center.x + half, center.y), strokeWidth, StrokeCap.Round)
    drawLine(color, Offset(center.x, center.y - half), Offset(center.x, center.y + half), strokeWidth, StrokeCap.Round)
}
