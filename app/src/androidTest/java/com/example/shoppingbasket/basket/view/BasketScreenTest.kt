package com.example.shoppingbasket.basket.view

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.shoppingbasket.basket.model.CartItem
import com.example.shoppingbasket.basket.viewmodel.BasketUiState
import com.example.shoppingbasket.common.model.Money
import com.example.shoppingbasket.common.model.Quantity
import com.example.shoppingbasket.productlist.domain.model.Product
import com.example.shoppingbasket.productlist.domain.model.ProductId
import com.example.shoppingbasket.productlist.domain.model.ProductImageUrl
import com.example.shoppingbasket.productlist.domain.model.ProductName
import org.junit.Rule
import org.junit.Test

class BasketScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun displaysTitleAndCartItems() {
        setScreen(BasketUiState(cartItems = listOf(testCartItem("p1"))))

        composeTestRule.onNodeWithText("Cart").assertExists()
        composeTestRule.onNodeWithText("PET보틀-정사각(500ml)").assertExists()
    }

    @Test
    fun clickingRemoveInvokesCallbackWithKey() {
        var removedKey: String? = null
        setScreen(
            uiState = BasketUiState(cartItems = listOf(testCartItem("p1"))),
            onRemove = { key -> removedKey = key }
        )

        composeTestRule.onNodeWithContentDescription("삭제").performClick()

        assert(removedKey == "p1")
    }

    @Test
    fun clickingIncreaseInvokesCallbackWithKey() {
        var increasedKey: String? = null
        setScreen(
            uiState = BasketUiState(cartItems = listOf(testCartItem("p1"))),
            onIncrease = { key -> increasedKey = key }
        )

        composeTestRule.onNodeWithContentDescription("수량 증가").performClick()

        assert(increasedKey == "p1")
    }

    @Test
    fun clickingDecreaseInvokesCallbackWithKey() {
        var decreasedKey: String? = null
        setScreen(
            uiState = BasketUiState(cartItems = listOf(testCartItem("p1"))),
            onDecrease = { key -> decreasedKey = key }
        )

        composeTestRule.onNodeWithContentDescription("수량 감소").performClick()

        assert(decreasedKey == "p1")
    }

    @Test
    fun displaysEmptyMessageWhenCartIsEmpty() {
        setScreen(BasketUiState(cartItems = emptyList()))

        composeTestRule.onNodeWithText("장바구니에 담긴 상품이 없습니다").assertIsDisplayed()
    }

    @Test
    fun hidesEmptyMessageWhenCartHasItems() {
        setScreen(BasketUiState(cartItems = listOf(testCartItem("p1"))))

        composeTestRule.onNodeWithText("장바구니에 담긴 상품이 없습니다").assertDoesNotExist()
    }

    @Test
    fun hidesPagingControlWhenSinglePage() {
        setScreen(BasketUiState(cartItems = listOf(testCartItem("p1"))))

        composeTestRule.onNodeWithText("<").assertDoesNotExist()
        composeTestRule.onNodeWithText(">").assertDoesNotExist()
    }

    @Test
    fun displaysPagingControlWhenMorePagesRemain() {
        // 카드의 수량(Stepper)도 "1"을 표시할 수 있어 페이지 번호와 겹치지 않게 수량을 2로 둔다
        setScreen(
            BasketUiState(cartItems = listOf(testCartItem("p1", Quantity(2))), pageNumber = 1, canGoNext = true)
        )

        composeTestRule.onNodeWithText("1").assertIsDisplayed()
        composeTestRule.onNodeWithText(">").assertIsDisplayed()
    }

    @Test
    fun clickingNextPageInvokesCallback() {
        var nextCalled = false
        setScreen(
            uiState = BasketUiState(cartItems = listOf(testCartItem("p1")), canGoNext = true),
            onNextPage = { nextCalled = true }
        )

        composeTestRule.onNodeWithText(">").performClick()

        assert(nextCalled)
    }

    @Test
    fun clickingPreviousPageInvokesCallback() {
        var previousCalled = false
        setScreen(
            uiState = BasketUiState(cartItems = listOf(testCartItem("p1")), pageNumber = 2, canGoPrevious = true),
            onPreviousPage = { previousCalled = true }
        )

        composeTestRule.onNodeWithText("<").performClick()

        assert(previousCalled)
    }

    private fun setScreen(
        uiState: BasketUiState,
        onRemove: (String) -> Unit = {},
        onIncrease: (String) -> Unit = {},
        onDecrease: (String) -> Unit = {},
        onPreviousPage: () -> Unit = {},
        onNextPage: () -> Unit = {}
    ) {
        composeTestRule.setContent {
            BasketScreen(
                uiState = uiState,
                onBack = {},
                onRemove = onRemove,
                onIncrease = onIncrease,
                onDecrease = onDecrease,
                onPreviousPage = onPreviousPage,
                onNextPage = onNextPage
            )
        }
    }

    private fun testCartItem(id: String, quantity: Quantity = Quantity(1)): CartItem {
        val product = Product(
            ProductId(id),
            ProductName("PET보틀-정사각(500ml)"),
            Money(10_000),
            ProductImageUrl("https://picsum.photos/seed/$id/600")
        )
        return CartItem(product, quantity)
    }
}
