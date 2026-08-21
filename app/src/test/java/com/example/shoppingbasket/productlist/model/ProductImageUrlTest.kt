package com.example.shoppingbasket.productlist.model

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class ProductImageUrlTest {

    @Test
    @DisplayName("표시값으로 원본 URL 문자열을 반환한다")
    fun toDisplayValue() {
        val imageUrl = ProductImageUrl("https://picsum.photos/seed/p1/600")

        assertThat(imageUrl.toDisplayValue()).isEqualTo("https://picsum.photos/seed/p1/600")
    }

    @Test
    @DisplayName("빈 값으로 생성할 수 없다")
    fun rejectsBlankValue() {
        assertThatThrownBy { ProductImageUrl(" ") }
            .isInstanceOf(IllegalArgumentException::class.java)
    }
}
