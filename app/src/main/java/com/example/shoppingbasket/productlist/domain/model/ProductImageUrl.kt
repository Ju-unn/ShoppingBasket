package com.example.shoppingbasket.productlist.domain.model

@JvmInline
value class ProductImageUrl(private val value: String) {

    init {
        require(value.isNotBlank()) { "이미지 URL은 비어있을 수 없다" }
    }

    fun toDisplayValue(): String = value
}
