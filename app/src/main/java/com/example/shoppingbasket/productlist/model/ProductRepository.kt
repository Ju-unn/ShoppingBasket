package com.example.shoppingbasket.productlist.model

import com.example.shoppingbasket.common.model.Money
import com.example.shoppingbasket.common.model.Page

// 아직 백엔드가 없어 목업 데이터를 반환한다. 실제 데이터 소스가 준비되면 이 클래스만 교체한다.
// 페이지 크기를 밖으로 노출하지 않고 firstPage() 로 첫 페이지를 만들어 준다.
class ProductRepository {

    fun findAll(): ProductList = ProductList(MOCK_PRODUCTS)

    fun firstPage(): Page = Page.first(PAGE_SIZE)

    fun findPage(page: Page): ProductList = findAll().sliceBy(page)

    fun hasPageAfter(page: Page): Boolean = findAll().hasPageAfter(page)

    fun findById(id: String): Product? = findAll().findByKey(id)

    private companion object {
        // 더보기 한 번에 불러올 상품 수 (2단계 기능 요구사항의 "20개 기준")
        const val PAGE_SIZE = 20
        const val MOCK_PRODUCT_COUNT = 50

        // 디자인(.pen)에 나오는 상품명·가격 조합으로 목업을 만든다.
        val SHAPES = listOf("정사각", "밀크티병", "납작", "단지", "원형")
        val VOLUMES = listOf("500ml", "300ml", "2000ml", "4000ml", "200ml", "260ml", "370ml")
        val PRICES = listOf(10_000, 12_000)

        val MOCK_PRODUCTS = (1..MOCK_PRODUCT_COUNT).map { number -> mockProduct(number) }

        fun mockProduct(number: Int): Product = Product(
            ProductId("p$number"),
            mockName(number),
            Money(pick(PRICES, number)),
            ProductImageUrl("https://picsum.photos/seed/p$number/600")
        )

        fun mockName(number: Int): ProductName {
            val shape = pick(SHAPES, number)
            val volume = pick(VOLUMES, number)
            return ProductName("PET보틀-$shape($volume)")
        }

        fun <T> pick(candidates: List<T>, number: Int): T = candidates[number % candidates.size]
    }
}
