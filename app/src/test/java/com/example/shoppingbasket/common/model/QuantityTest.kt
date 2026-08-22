package com.example.shoppingbasket.common.model

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class QuantityTest {

    @Test
    @DisplayName("표시값으로 숫자 문자열을 반환한다")
    fun toDisplayValue() {
        assertThat(Quantity(3).toDisplayValue()).isEqualTo("3")
    }

    @Test
    @DisplayName("1 미만으로 생성할 수 없다")
    fun rejectsBelowOne() {
        assertThatThrownBy { Quantity(0) }
            .isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    @DisplayName("수량만큼 금액을 더한 값을 반환한다")
    fun multiply() {
        assertThat(Quantity(3).multiply(Money(1_000))).isEqualTo(Money(3_000))
    }

    @Test
    @DisplayName("다른 수량을 더한 값을 반환한다")
    fun increasedBy() {
        assertThat(Quantity(2).increasedBy(Quantity(3))).isEqualTo(Quantity(5))
    }
}
