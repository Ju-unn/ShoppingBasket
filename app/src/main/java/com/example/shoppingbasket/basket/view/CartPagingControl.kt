package com.example.shoppingbasket.basket.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shoppingbasket.R
import com.example.shoppingbasket.common.ext.noRippleClickable

// 디자인(.pen)의 "장바구니 페이징" 기준 — 전체 129 x 42, 버튼 42dp 정사각, 색인 영역 45dp.
// 아이콘성 컨트롤이라 화면 폭에 비례시키지 않고 dp 를 고정한다.
private val PAGING_BUTTON_SIZE = 42.dp
private val PAGING_NUMBER_WIDTH = 45.dp
private val PAGING_CORNER_RADIUS = 4.dp
private val PAGING_LABEL_SIZE = 22.sp

@Composable
fun CartPagingControl(
    pageNumber: Int,
    canGoPrevious: Boolean,
    canGoNext: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        PagingButton(
            text = stringResource(R.string.paging_previous_button),
            enabled = canGoPrevious,
            shape = RoundedCornerShape(topStart = PAGING_CORNER_RADIUS, bottomStart = PAGING_CORNER_RADIUS),
            onClick = onPrevious
        )
        PageNumber(pageNumber = pageNumber)
        PagingButton(
            text = stringResource(R.string.paging_next_button),
            enabled = canGoNext,
            shape = RoundedCornerShape(topEnd = PAGING_CORNER_RADIUS, bottomEnd = PAGING_CORNER_RADIUS),
            onClick = onNext
        )
    }
}

@Composable
private fun PagingButton(text: String, enabled: Boolean, shape: Shape, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(PAGING_BUTTON_SIZE)
            .background(color = buttonColor(enabled), shape = shape)
            .noRippleClickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = colorResource(R.color.white),
            fontSize = PAGING_LABEL_SIZE,
            fontWeight = FontWeight.Medium
        )
    }
}

// 이동할 수 있으면 primary, 더 갈 곳이 없으면 회색으로 비활성 표시한다
@Composable
private fun buttonColor(enabled: Boolean) = when (enabled) {
    true -> colorResource(R.color.primary)
    false -> colorResource(R.color.gray_border)
}

@Composable
private fun PageNumber(pageNumber: Int) {
    Box(
        modifier = Modifier.width(PAGING_NUMBER_WIDTH),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = pageNumber.toString(),
            color = colorResource(R.color.gray_5),
            fontSize = PAGING_LABEL_SIZE,
            fontWeight = FontWeight.Medium
        )
    }
}
