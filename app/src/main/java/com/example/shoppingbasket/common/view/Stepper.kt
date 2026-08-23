package com.example.shoppingbasket.common.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shoppingbasket.R
import com.example.shoppingbasket.common.ext.noRippleClickable

// 디자인(.pen)의 재사용 컴포넌트 Stepper(jxIE5) 기준 — 126 x 42, gray-border 1px, corner 4.
// 아이콘성 컨트롤이라 화면 폭에 비례시키지 않고 dp 를 고정한다.
private val STEPPER_WIDTH = 126.dp
private val STEPPER_HEIGHT = 42.dp
private val STEPPER_CORNER_RADIUS = 4.dp
private val STEPPER_BORDER_WIDTH = 1.dp
private val STEPPER_ICON_SIZE = 20.dp
private val STEPPER_HORIZONTAL_PADDING = 10.dp
private val STEPPER_QUANTITY_SIZE = 16.sp

@Composable
fun Stepper(
    quantityText: String,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .size(STEPPER_WIDTH, STEPPER_HEIGHT)
            .background(colorResource(R.color.white), RoundedCornerShape(STEPPER_CORNER_RADIUS))
            .border(STEPPER_BORDER_WIDTH, colorResource(R.color.gray_border), RoundedCornerShape(STEPPER_CORNER_RADIUS))
            .padding(horizontal = STEPPER_HORIZONTAL_PADDING),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_remove),
            contentDescription = stringResource(R.string.decrease_quantity_description),
            tint = colorResource(R.color.gray_5),
            modifier = Modifier
                .size(STEPPER_ICON_SIZE)
                .noRippleClickable(onClick = onDecrease)
        )
        Text(
            text = quantityText,
            color = colorResource(R.color.black),
            fontSize = STEPPER_QUANTITY_SIZE,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f)
        )
        Icon(
            painter = painterResource(R.drawable.ic_add),
            contentDescription = stringResource(R.string.increase_quantity_description),
            tint = colorResource(R.color.primary),
            modifier = Modifier
                .size(STEPPER_ICON_SIZE)
                .noRippleClickable(onClick = onIncrease)
        )
    }
}
