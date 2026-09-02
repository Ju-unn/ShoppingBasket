package com.example.shoppingbasket.productdetail.viewmodel

import com.example.shoppingbasket.common.model.Quantity
import com.example.shoppingbasket.productlist.model.Product

data class ProductDetailUiState(
    val product: Product? = null,
    val quantity: Quantity = Quantity.ONE,
    // 자기 자신을 제외한 마지막으로 본 상품. 없으면 카드를 그리지 않는다 (4단계 pen 스펙)
    val lastViewedProduct: Product? = null
) {

    // 상세 화면의 +/- 는 로컬 수량만 바꾼다. 장바구니 반영은 addToCart() 를 눌러야 일어난다 (3단계 결정 5번)
    fun totalPriceDisplayValue(): String {
        val selectedProduct = product ?: return ""
        return selectedProduct.priceFor(quantity).toDisplayValue()
    }

    fun quantityDisplayValue(): String = quantity.toDisplayValue()

    fun increasedQuantity(): ProductDetailUiState = copy(quantity = quantity.increasedBy(Quantity.ONE))

    fun decreasedQuantity(): ProductDetailUiState {
        if (quantity.isMinimum()) {
            return this
        }
        return copy(quantity = quantity.decreased())
    }
}
