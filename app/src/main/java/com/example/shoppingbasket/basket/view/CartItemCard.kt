package com.example.shoppingbasket.basket.view

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.shoppingbasket.R
import com.example.shoppingbasket.basket.model.CartItem
import com.example.shoppingbasket.common.ext.noRippleClickable
import com.example.shoppingbasket.common.view.Stepper

// pen 카드(k0IHi)의 Stepper 하단(y=102)과 금액 상단(y=108) 사이 간격
private val STEPPER_PRICE_GAP = 6.dp

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun CartItemCard(
    cartItem: CartItem,
    onRemove: () -> Unit,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, colorResource(R.color.gray_border), RoundedCornerShape(4.dp))
            .padding(12.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = cartItem.displayName(),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.gray_5)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.fillMaxWidth()) {
                GlideImage(
                    model = cartItem.displayImageUrl(),
                    contentDescription = cartItem.displayName(),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .width(136.dp)
                        .height(72.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
                Spacer(modifier = Modifier.weight(1f))
                Column(horizontalAlignment = Alignment.End) {
                    Stepper(
                        quantityText = cartItem.displayQuantity(),
                        onIncrease = onIncrease,
                        onDecrease = onDecrease
                    )
                    Spacer(modifier = Modifier.height(STEPPER_PRICE_GAP))
                    Text(
                        text = cartItem.displayTotalPrice(),
                        fontSize = 16.sp,
                        color = colorResource(R.color.gray_5)
                    )
                }
            }
        }
        Icon(
            painter = painterResource(R.drawable.ic_close),
            contentDescription = stringResource(R.string.remove_item_description),
            tint = colorResource(R.color.gray_border),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(16.dp)
                .noRippleClickable(onClick = onRemove)
        )
    }
}
