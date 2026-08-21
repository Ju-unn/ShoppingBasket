package com.example.shoppingbasket.productlist.model

import com.example.shoppingbasket.common.model.Money
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class ProductTest {

    @Test
    @DisplayName("id/이름/가격을 표시용 메서드로 노출한다")
    fun exposesDisplayValues() {
        val product = Product(ProductId("p1"), ProductName("PET보틀-정사각(500ml)"), Money(10_000))

        assertThat(product.key()).isEqualTo("p1")
        assertThat(product.displayName()).isEqualTo("PET보틀-정사각(500ml)")
        assertThat(product.displayPrice()).isEqualTo("10,000원")
    }
}
