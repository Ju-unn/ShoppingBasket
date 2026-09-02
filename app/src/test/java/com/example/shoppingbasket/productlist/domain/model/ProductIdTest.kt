package com.example.shoppingbasket.productlist.domain.model

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class ProductIdTest {

    @Test
    @DisplayName("키 값으로 원본 문자열을 반환한다")
    fun toKeyValue() {
        val id = ProductId("p1")

        assertThat(id.toKeyValue()).isEqualTo("p1")
    }

    @Test
    @DisplayName("빈 값으로 생성할 수 없다")
    fun rejectsBlankValue() {
        assertThatThrownBy { ProductId(" ") }
            .isInstanceOf(IllegalArgumentException::class.java)
    }
}
