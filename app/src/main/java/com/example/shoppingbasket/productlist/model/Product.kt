package com.example.shoppingbasket.productlist.model

import com.example.shoppingbasket.common.model.Money

data class Product(
    private val id: ProductId,
    private val name: ProductName,
    private val price: Money
) {

    fun key(): String = id.toKeyValue()

    fun displayName(): String = name.toDisplayValue()

    fun displayPrice(): String = price.toDisplayValue()
}
