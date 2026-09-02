package com.example.shoppingbasket.productlist.model

import com.example.shoppingbasket.common.model.Money
import kotlinx.serialization.Serializable

// 서버(GET /products) 응답을 그대로 옮겨 담는 DTO.
// UiState 와 마찬가지로 객체지향 생활 체조 7·9번 예외로 둔다 — JSON 구조를 그대로 표현하는 것이 목적이라
// 필드를 감추면 오히려 역직렬화 의도를 흐린다. 도메인 객체 변환은 toProduct() 하나로 즉시 끝낸다.
@Serializable
data class ProductResponse(val id: String, val name: String, val price: Int, val imageUrl: String)

fun ProductResponse.toProduct(): Product = Product(
    ProductId(id),
    ProductName(name),
    Money(price),
    ProductImageUrl(imageUrl)
)
