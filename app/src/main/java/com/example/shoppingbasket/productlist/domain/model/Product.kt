package com.example.shoppingbasket.productlist.domain.model

import com.example.shoppingbasket.common.model.Money
import com.example.shoppingbasket.common.model.Quantity

data class Product(
    private val id: ProductId,
    private val name: ProductName,
    private val price: Money,
    private val imageUrl: ProductImageUrl
) {

    fun key(): String = id.toKeyValue()

    fun displayName(): String = name.toDisplayValue()

    fun displayPrice(): String = price.toDisplayValue()

    fun displayImageUrl(): String = imageUrl.toDisplayValue()

    fun priceFor(quantity: Quantity): Money = quantity.multiply(price)
}
