package com.example.shoppingbasket.productlist.data.remote

import com.example.shoppingbasket.common.model.Money
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class ProductResponseTest {

    @Test
    @DisplayName("서버 응답을 도메인 Product 로 변환한다")
    fun toProduct() {
        val response = ProductResponse("p1", "PET보틀-밀크티병(300ml)", 12_000, "https://picsum.photos/seed/p1/600")

        val product = response.toProduct()

        assertThat(product.key()).isEqualTo("p1")
        assertThat(product.displayName()).isEqualTo("PET보틀-밀크티병(300ml)")
        assertThat(product.displayPrice()).isEqualTo(Money(12_000).toDisplayValue())
        assertThat(product.displayImageUrl()).isEqualTo("https://picsum.photos/seed/p1/600")
    }
}
