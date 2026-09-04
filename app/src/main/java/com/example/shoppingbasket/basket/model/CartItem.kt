package com.example.shoppingbasket.basket.model

import com.example.shoppingbasket.common.model.Money
import com.example.shoppingbasket.common.model.Quantity
import com.example.shoppingbasket.productlist.domain.model.Product

data class CartItem(
    private val product: Product,
    private val quantity: Quantity
) {

    fun key(): String = product.key()

    fun displayName(): String = product.displayName()

    fun displayImageUrl(): String = product.displayImageUrl()

    fun displayQuantity(): String = quantity.toDisplayValue()

    fun displayTotalPrice(): String = product.priceFor(quantity).toDisplayValue()

    fun isSameProduct(other: Product): Boolean = product.key() == other.key()

    fun increasedBy(amount: Quantity): CartItem = CartItem(product, quantity.increasedBy(amount))

    fun decreased(): CartItem = CartItem(product, quantity.decreased())

    fun isMinimumQuantity(): Boolean = quantity.isMinimum()

    fun currentQuantity(): Quantity = quantity

    // Room 저장을 위해 상품 단가를 그대로 노출 (4단계 결정 10번, 영속화 전용)
    fun unitPrice(): Money = product.unitPrice()

    fun accumulate(total: Money): Money = total.plus(product.priceFor(quantity))
}
