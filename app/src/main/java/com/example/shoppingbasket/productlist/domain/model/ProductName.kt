package com.example.shoppingbasket.productlist.domain.model

@JvmInline
value class ProductName(private val value: String) {

    init {
        require(value.isNotBlank()) { "상품명은 비어있을 수 없다" }
    }

    fun toDisplayValue(): String = value
}
