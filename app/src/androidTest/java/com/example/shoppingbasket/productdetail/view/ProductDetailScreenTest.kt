package com.example.shoppingbasket.productdetail.view

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.shoppingbasket.common.model.Money
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
        val uiState = ProductDetailUiState(product = testProduct())

        composeTestRule.setContent {
            ProductDetailScreen(uiState = uiState, onClose = {}, onAddToCart = {})
        }

        composeTestRule.onNodeWithText("PET보틀-정사각(500ml)").assertExists()
        composeTestRule.onNodeWithText("10,000원").assertExists()
    }

    @Test
    fun clickingButtonInvokesCallback() {
        var addToCartClicked = false
        val uiState = ProductDetailUiState(product = testProduct())

        composeTestRule.setContent {
            ProductDetailScreen(
                uiState = uiState,
                onClose = {},
                onAddToCart = { addToCartClicked = true }
            )
        }
        composeTestRule.onNodeWithText("장바구니 담기").performClick()

        assert(addToCartClicked)
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
