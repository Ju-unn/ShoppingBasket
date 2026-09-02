package com.example.shoppingbasket.productlist.domain.model

import com.example.shoppingbasket.common.model.Money
import com.example.shoppingbasket.common.model.Quantity
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class ProductTest {

    @Test
    @DisplayName("id/이름/가격/이미지 URL을 표시용 메서드로 노출한다")
    fun exposesDisplayValues() {
        val product = Product(
            ProductId("p1"),
            ProductName("PET보틀-정사각(500ml)"),
            Money(10_000),
            ProductImageUrl("https://picsum.photos/seed/p1/600")
        )

        assertThat(product.key()).isEqualTo("p1")
        assertThat(product.displayName()).isEqualTo("PET보틀-정사각(500ml)")
        assertThat(product.displayPrice()).isEqualTo("10,000원")
        assertThat(product.displayImageUrl()).isEqualTo("https://picsum.photos/seed/p1/600")
    }

    @Test
    @DisplayName("수량만큼의 총 가격을 계산한다")
    fun priceFor() {
        val product = Product(
            ProductId("p1"),
            ProductName("PET보틀-정사각(500ml)"),
            Money(10_000),
            ProductImageUrl("https://picsum.photos/seed/p1/600")
        )

        assertThat(product.priceFor(Quantity(3))).isEqualTo(Money(30_000))
    }
}
