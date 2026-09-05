package com.example.shoppingbasket.productdetail.view

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.shoppingbasket.common.model.Money
import com.example.shoppingbasket.common.model.Quantity
import com.example.shoppingbasket.productdetail.viewmodel.ProductDetailUiState
import com.example.shoppingbasket.productlist.domain.model.Product
import com.example.shoppingbasket.productlist.domain.model.ProductId
import com.example.shoppingbasket.productlist.domain.model.ProductImageUrl
import com.example.shoppingbasket.productlist.domain.model.ProductName
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test

class ProductDetailScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun displaysProductInfo() {
        setScreen(ProductDetailUiState(product = testProduct()))

        composeTestRule.onNodeWithText("PET보틀-정사각(500ml)").assertExists()
        composeTestRule.onNodeWithText("10,000원").assertExists()
    }

    @Test
    fun displaysTotalPriceMultipliedByQuantity() {
        setScreen(ProductDetailUiState(product = testProduct(), quantity = Quantity(3)))

        composeTestRule.onNodeWithText("30,000원").assertExists()
        composeTestRule.onNodeWithText("3").assertExists()
    }

    @Test
    fun clickingButtonInvokesCallback() {
        var addToCartClicked = false
        setScreen(
            uiState = ProductDetailUiState(product = testProduct()),
            onAddToCart = { addToCartClicked = true }
        )

        composeTestRule.onNodeWithText("장바구니 담기").performClick()

        assertThat(addToCartClicked).isTrue()
    }

    @Test
    fun clickingIncreaseInvokesCallback() {
        var increaseCalled = false
        setScreen(
            uiState = ProductDetailUiState(product = testProduct()),
            onIncrease = { increaseCalled = true }
        )

        composeTestRule.onNodeWithContentDescription("수량 증가").performClick()

        assertThat(increaseCalled).isTrue()
    }

    @Test
    fun clickingDecreaseInvokesCallback() {
        var decreaseCalled = false
        setScreen(
            uiState = ProductDetailUiState(product = testProduct()),
            onDecrease = { decreaseCalled = true }
        )

        composeTestRule.onNodeWithContentDescription("수량 감소").performClick()

        assertThat(decreaseCalled).isTrue()
    }

    @Test
    fun hidesLastViewedCardWhenAbsent() {
        setScreen(ProductDetailUiState(product = testProduct(), lastViewedProduct = null))

        composeTestRule.onNodeWithText("마지막으로 본 상품").assertDoesNotExist()
    }

    @Test
    fun displaysLastViewedCardWhenPresent() {
        setScreen(ProductDetailUiState(product = testProduct(), lastViewedProduct = testLastViewedProduct()))

        composeTestRule.onNodeWithText("마지막으로 본 상품").assertExists()
        composeTestRule.onNodeWithText("장바구니-보조가방").assertExists()
    }

    @Test
    fun clickingLastViewedCardInvokesCallbackWithKey() {
        var clickedKey: String? = null
        setScreen(
            uiState = ProductDetailUiState(product = testProduct(), lastViewedProduct = testLastViewedProduct()),
            onLastViewedClick = { key -> clickedKey = key }
        )

        composeTestRule.onNodeWithText("장바구니-보조가방").performClick()

        assertThat(clickedKey).isEqualTo("p2")
    }

    @Test
    fun showsNotFoundMessageWhenProductIsMissing() {
        setScreen(ProductDetailUiState(product = null))

        composeTestRule.onNodeWithText("상품을 찾을 수 없습니다").assertExists()
    }

    @Test
    fun keepsCloseIconWhenProductIsMissing() {
        setScreen(ProductDetailUiState(product = null))

        composeTestRule.onNodeWithContentDescription("닫기").assertExists()
    }

    @Test
    fun clickingCloseWhenProductIsMissingInvokesCallback() {
        var closeClicked = false
        setScreen(uiState = ProductDetailUiState(product = null), onClose = { closeClicked = true })

        composeTestRule.onNodeWithContentDescription("닫기").performClick()

        assertThat(closeClicked).isTrue()
    }

    @Test
    fun hidesAddToCartButtonWhenProductIsMissing() {
        setScreen(ProductDetailUiState(product = null))

        composeTestRule.onNodeWithText("장바구니 담기").assertDoesNotExist()
    }

    private fun setScreen(
        uiState: ProductDetailUiState,
        onClose: () -> Unit = {},
        onAddToCart: () -> Unit = {},
        onIncrease: () -> Unit = {},
        onDecrease: () -> Unit = {},
        onLastViewedClick: (String) -> Unit = {}
    ) {
        composeTestRule.setContent {
            ProductDetailScreen(
                uiState = uiState,
                onClose = onClose,
                onAddToCart = onAddToCart,
                onIncrease = onIncrease,
                onDecrease = onDecrease,
                onLastViewedClick = onLastViewedClick
            )
        }
    }

    private fun testProduct(): Product {
        return Product(
            ProductId("p1"),
            ProductName("PET보틀-정사각(500ml)"),
            Money(10_000),
            ProductImageUrl("https://picsum.photos/seed/p1/600")
        )
    }

    private fun testLastViewedProduct(): Product {
        return Product(
            ProductId("p2"),
            ProductName("장바구니-보조가방"),
            Money(5_000),
            ProductImageUrl("https://picsum.photos/seed/p2/600")
        )
    }
}
