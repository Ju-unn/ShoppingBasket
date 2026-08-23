package com.example.shoppingbasket.productdetail.view

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.shoppingbasket.common.model.Money
import com.example.shoppingbasket.common.model.Quantity
import com.example.shoppingbasket.productdetail.viewmodel.ProductDetailUiState
import com.example.shoppingbasket.productlist.model.Product
import com.example.shoppingbasket.productlist.model.ProductId
import com.example.shoppingbasket.productlist.model.ProductImageUrl
import com.example.shoppingbasket.productlist.model.ProductName
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

        assert(addToCartClicked)
    }

    @Test
    fun clickingIncreaseInvokesCallback() {
        var increaseCalled = false
        setScreen(
            uiState = ProductDetailUiState(product = testProduct()),
            onIncrease = { increaseCalled = true }
        )

        composeTestRule.onNodeWithContentDescription("수량 증가").performClick()

        assert(increaseCalled)
    }

    @Test
    fun clickingDecreaseInvokesCallback() {
        var decreaseCalled = false
        setScreen(
            uiState = ProductDetailUiState(product = testProduct()),
            onDecrease = { decreaseCalled = true }
        )

        composeTestRule.onNodeWithContentDescription("수량 감소").performClick()

        assert(decreaseCalled)
    }

    private fun setScreen(
        uiState: ProductDetailUiState,
        onClose: () -> Unit = {},
        onAddToCart: () -> Unit = {},
        onIncrease: () -> Unit = {},
        onDecrease: () -> Unit = {}
    ) {
        composeTestRule.setContent {
            ProductDetailScreen(
                uiState = uiState,
                onClose = onClose,
                onAddToCart = onAddToCart,
                onIncrease = onIncrease,
                onDecrease = onDecrease
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
}
