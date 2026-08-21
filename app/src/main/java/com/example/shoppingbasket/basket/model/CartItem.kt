package com.example.shoppingbasket.basket.model

import com.example.shoppingbasket.common.model.Money
import com.example.shoppingbasket.common.model.Quantity
import com.example.shoppingbasket.productlist.model.Product

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

    fun accumulate(total: Money): Money = total.plus(product.priceFor(quantity))
}
