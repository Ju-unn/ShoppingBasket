package com.example.shoppingbasket.common.model

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class MoneyTest {

    @Test
    @DisplayName("표시값은 MoneyFormatter 형식을 따른다")
    fun toDisplayValue() {
        val money = Money(10_000)

        assertThat(money.toDisplayValue()).isEqualTo("10,000원")
    }

    @Test
    @DisplayName("음수 금액은 생성할 수 없다")
    fun rejectsNegativeAmount() {
        assertThatThrownBy { Money(-1) }
            .isInstanceOf(IllegalArgumentException::class.java)
    }
}
