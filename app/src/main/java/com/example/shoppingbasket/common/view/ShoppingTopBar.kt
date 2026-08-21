package com.example.shoppingbasket.common.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shoppingbasket.R

@Composable
fun ShoppingTopBar(
    modifier: Modifier = Modifier,
    title: String? = null,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: @Composable () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(colorResource(R.color.gray_5))
            .padding(start = if (leadingIcon != null) 11.dp else 26.dp, end = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        leadingIcon?.invoke()
        if (title != null) {
            Text(
                text = title,
                modifier = Modifier.weight(1f),
                color = colorResource(R.color.white),
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )
        } else {
            Spacer(modifier = Modifier.weight(1f))
        }
        trailingIcon()
    }
}
