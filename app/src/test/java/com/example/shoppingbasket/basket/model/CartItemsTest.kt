package com.example.shoppingbasket.basket.model

import com.example.shoppingbasket.common.fixture.testProduct
import com.example.shoppingbasket.common.model.Money
import com.example.shoppingbasket.common.model.Page
import com.example.shoppingbasket.common.model.Quantity
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class CartItemsTest {

    @Test
    @DisplayName("빈 목록 여부를 판단한다")
    fun isEmpty() {
        assertThat(CartItems(emptyList()).isEmpty()).isTrue()
    }

    @Test
    @DisplayName("새 상품을 담으면 항목이 추가된다")
    fun addedNewProduct() {
        val cartItems = CartItems(emptyList()).added(testProduct("p1", 10_000))

        assertThat(cartItems.items()).hasSize(1)
        assertThat(cartItems.items().first().displayQuantity()).isEqualTo("1")
    }

    @Test
    @DisplayName("이미 담긴 상품을 다시 담으면 수량만 늘어난다")
    fun addedExistingProductIncreasesQuantity() {
        val cartItems = CartItems(emptyList())
            .added(testProduct("p1", 10_000))
            .added(testProduct("p1", 10_000))

        assertThat(cartItems.items()).hasSize(1)
        assertThat(cartItems.items().first().displayQuantity()).isEqualTo("2")
    }

    @Test
    @DisplayName("key로 상품을 제거한다")
    fun removed() {
        val cartItems = CartItems(emptyList())
            .added(testProduct("p1", 10_000))
            .added(testProduct("p2", 12_000))
            .removed("p1")

        assertThat(cartItems.items()).hasSize(1)
        assertThat(cartItems.items().first().key()).isEqualTo("p2")
    }

    @Test
    @DisplayName("담긴 상품들의 총액을 합산한다")
    fun totalPrice() {
        val cartItems = CartItems(emptyList())
            .added(testProduct("p1", 10_000), Quantity(2))
            .added(testProduct("p2", 5_000))

        assertThat(cartItems.totalPrice()).isEqualTo(Money(25_000))
    }

    @Test
    @DisplayName("페이지 단위로 잘라낸 목록을 반환한다")
    fun sliceBy() {
        val firstPage = cartItemsOf(5).sliceBy(Page.first(PAGE_SIZE))

        assertThat(firstPage.items()).hasSize(2)
    }

    @Test
    @DisplayName("마지막 페이지가 덜 찼으면 남은 상품만 반환한다")
    fun sliceByPartialLastPage() {
        val lastPage = cartItemsOf(5).sliceBy(Page.first(PAGE_SIZE).next().next())

        assertThat(lastPage.items()).hasSize(1)
    }

    @Test
    @DisplayName("뒤에 남은 상품이 있으면 다음 페이지가 있다고 판단한다")
    fun hasPageAfter() {
        assertThat(cartItemsOf(5).hasPageAfter(Page.first(PAGE_SIZE))).isTrue()
    }

    @Test
    @DisplayName("마지막 페이지 뒤에는 다음 페이지가 없다")
    fun hasNoPageAfterLastPage() {
        val lastPage = Page.first(PAGE_SIZE).next().next()

        assertThat(cartItemsOf(5).hasPageAfter(lastPage)).isFalse()
    }

    @Test
    @DisplayName("상품이 줄어 현재 페이지가 비면 내용이 남은 마지막 페이지로 당긴다")
    fun coercePageIntoLastRemainingPage() {
        val thirdPage = Page.first(PAGE_SIZE).next().next()

        assertThat(cartItemsOf(3).coercePage(thirdPage).number()).isEqualTo(2)
    }

    @Test
    @DisplayName("현재 페이지에 상품이 남아 있으면 그 페이지를 유지한다")
    fun coercePageKeepsValidPage() {
        val secondPage = Page.first(PAGE_SIZE).next()

        assertThat(cartItemsOf(5).coercePage(secondPage).number()).isEqualTo(2)
    }

    @Test
    @DisplayName("장바구니가 비면 첫 페이지로 당긴다")
    fun coercePageIntoFirstPageWhenEmpty() {
        val thirdPage = Page.first(PAGE_SIZE).next().next()

        assertThat(CartItems(emptyList()).coercePage(thirdPage).number()).isEqualTo(1)
    }

    @Test
    @DisplayName("key로 상품 수량을 늘린다")
    fun increase() {
        val cartItems = CartItems(emptyList())
            .added(testProduct("p1", 10_000))
            .increase("p1")

        assertThat(cartItems.items().first().displayQuantity()).isEqualTo("2")
    }

    @Test
    @DisplayName("수량이 2 이상이면 하나 줄인다")
    fun decreaseAboveMinimum() {
        val cartItems = CartItems(emptyList())
            .added(testProduct("p1", 10_000), Quantity(2))
            .decrease("p1")

        assertThat(cartItems.items().first().displayQuantity()).isEqualTo("1")
    }

    @Test
    @DisplayName("수량이 1일 때 줄이면 장바구니에서 제거한다")
    fun decreaseAtMinimumRemovesItem() {
        val cartItems = CartItems(emptyList())
            .added(testProduct("p1", 10_000))
            .decrease("p1")

        assertThat(cartItems.isEmpty()).isTrue()
    }

    @Test
    @DisplayName("담긴 상품의 수량을 조회한다")
    fun quantityOfPresentProduct() {
        val product = testProduct("p1", 10_000)
        val cartItems = CartItems(emptyList()).added(product, Quantity(3))

        assertThat(cartItems.quantityOf(product)).isEqualTo(Quantity(3))
    }

    @Test
    @DisplayName("담기지 않은 상품은 null을 반환한다")
    fun quantityOfAbsentProduct() {
        assertThat(CartItems(emptyList()).quantityOf(testProduct("p1", 10_000))).isNull()
    }

    @Test
    @DisplayName("장바구니가 비어 있으면 총 수량은 0이다")
    fun totalQuantityDisplayValueWhenEmpty() {
        assertThat(CartItems(emptyList()).totalQuantityDisplayValue()).isEqualTo("0")
    }

    @Test
    @DisplayName("담긴 상품들의 수량을 모두 더한다")
    fun totalQuantityDisplayValue() {
        val cartItems = CartItems(emptyList())
            .added(testProduct("p1", 10_000), Quantity(2))
            .added(testProduct("p2", 5_000), Quantity(3))

        assertThat(cartItems.totalQuantityDisplayValue()).isEqualTo("5")
    }

    private fun cartItemsOf(count: Int): CartItems {
        return (1..count).fold(CartItems(emptyList())) { cartItems, number ->
            cartItems.added(testProduct("p$number"))
        }
    }

    private companion object {
        // 경계를 짧은 목록으로 검증하기 위해 실제 화면(5개)보다 작은 크기를 쓴다
        const val PAGE_SIZE = 2
    }
}
