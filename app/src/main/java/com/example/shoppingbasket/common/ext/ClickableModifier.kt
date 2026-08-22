package com.example.shoppingbasket.common.ext

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

// 리플 등 클릭 애니메이션 없이 클릭 이벤트만 받는다
@Composable
fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier {
    return this.clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null,
        onClick = onClick
    )
}
