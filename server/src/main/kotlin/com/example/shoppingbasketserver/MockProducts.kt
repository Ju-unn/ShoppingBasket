package com.example.shoppingbasketserver

import kotlinx.serialization.Serializable

// 안드로이드 앱 ProductRepository 의 목업 생성 규칙과 완전히 동일하게 맞춘다.
// 5단계에서 안드로이드가 이 응답으로 목업을 대체하므로 데이터가 하나라도 달라지면 안 된다.
@Serializable
data class ProductResponse(val id: String, val name: String, val price: Int, val imageUrl: String)

object MockProducts {

    private const val PRODUCT_COUNT = 50
    private val SHAPES = listOf("정사각", "밀크티병", "납작", "단지", "원형")
    private val VOLUMES = listOf("500ml", "300ml", "2000ml", "4000ml", "200ml", "260ml", "370ml")
    private val PRICES = listOf(10_000, 12_000)

    fun all(): List<ProductResponse> = (1..PRODUCT_COUNT).map { number -> product(number) }

    private fun product(number: Int): ProductResponse = ProductResponse(
        id = "p$number",
        name = "PET보틀-${pick(SHAPES, number)}(${pick(VOLUMES, number)})",
        price = pick(PRICES, number),
        imageUrl = "https://picsum.photos/seed/p$number/600"
    )

    private fun <T> pick(candidates: List<T>, number: Int): T = candidates[number % candidates.size]
}
