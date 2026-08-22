package com.example.shoppingbasket.common.ext

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

// 리플 등 클릭 애니메이션 없이 클릭 이벤트만 받는다.
// enabled 가 false 면 눌러도 onClick 이 호출되지 않는다 (비활성 버튼용)
@Composable
fun Modifier.noRippleClickable(enabled: Boolean = true, onClick: () -> Unit): Modifier {
    return this.clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null,
        enabled = enabled,
        onClick = onClick
    )
}
