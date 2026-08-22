package com.example.shoppingbasket.common.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shoppingbasket.R
import com.example.shoppingbasket.common.ext.noRippleClickable

// 디자인(.pen)의 재사용 컴포넌트 Button 에 대응한다.
// 높이 48dp·primary 배경·흰색 20sp Bold 라벨은 고정이고, 가로는 놓이는 자리에 맞춰 늘어난다.
private val BUTTON_HEIGHT = 48.dp
private val BUTTON_LABEL_SIZE = 20.sp

@Composable
fun ShoppingButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(BUTTON_HEIGHT)
            .background(colorResource(R.color.primary))
            .noRippleClickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = colorResource(R.color.white),
            fontSize = BUTTON_LABEL_SIZE,
            fontWeight = FontWeight.Bold
        )
    }
}
