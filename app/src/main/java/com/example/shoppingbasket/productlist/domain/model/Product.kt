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

    // Room 저장을 위해 단가를 그대로 노출 (4단계 결정 10번, 영속화 전용)
    fun unitPrice(): Money = price

    fun priceFor(quantity: Quantity): Money = quantity.multiply(price)
}
