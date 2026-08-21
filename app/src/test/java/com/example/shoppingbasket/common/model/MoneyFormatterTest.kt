package com.example.shoppingbasket.common.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class MoneyFormatterTest {

    @Test
    @DisplayName("천 단위 콤마와 원 단위를 붙여 표시한다")
    fun format() {
        assertThat(MoneyFormatter.format(10_000)).isEqualTo("10,000원")
    }

    @Test
    @DisplayName("0원도 정상적으로 표시한다")
    fun formatZero() {
        assertThat(MoneyFormatter.format(0)).isEqualTo("0원")
    }
}
