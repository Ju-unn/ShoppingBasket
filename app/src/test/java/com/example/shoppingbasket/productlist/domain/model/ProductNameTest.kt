package com.example.shoppingbasket.productlist.domain.model

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class ProductNameTest {

    @Test
    @DisplayName("표시값으로 원본 문자열을 반환한다")
    fun toDisplayValue() {
        val name = ProductName("PET보틀-정사각(500ml)")

        assertThat(name.toDisplayValue()).isEqualTo("PET보틀-정사각(500ml)")
    }

    @Test
    @DisplayName("빈 값으로 생성할 수 없다")
    fun rejectsBlankValue() {
        assertThatThrownBy { ProductName(" ") }
            .isInstanceOf(IllegalArgumentException::class.java)
    }
}
