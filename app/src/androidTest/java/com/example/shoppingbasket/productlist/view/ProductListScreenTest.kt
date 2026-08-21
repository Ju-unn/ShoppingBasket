package com.example.shoppingbasket.productlist.view

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.shoppingbasket.common.model.Money
import com.example.shoppingbasket.productlist.model.Product
import com.example.shoppingbasket.productlist.model.ProductId
import com.example.shoppingbasket.productlist.model.ProductImageUrl
import com.example.shoppingbasket.productlist.model.ProductName
import com.example.shoppingbasket.productlist.viewmodel.ProductListUiState
import org.junit.Rule
import org.junit.Test

class ProductListScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun displaysTopBarAndProducts() {
        val uiState = ProductListUiState(
            products = listOf(
                Product(
                    ProductId("p1"),
                    ProductName("PET보틀-정사각(500ml)"),
                    Money(10_000),
                    ProductImageUrl("https://picsum.photos/seed/p1/600")
                )
            )
        )

        composeTestRule.setContent {
            ProductListScreen(uiState = uiState, onProductClick = {}, onCartClick = {})
        }

        composeTestRule.onNodeWithText("Shopping").assertExists()
        composeTestRule.onNodeWithText("PET보틀-정사각(500ml)").assertExists()
        composeTestRule.onNodeWithText("10,000원").assertExists()
    }

    @Test
    fun clickingProductInvokesCallbackWithKey() {
        var clickedKey: String? = null
        val uiState = ProductListUiState(
            products = listOf(
                Product(
                    ProductId("p1"),
                    ProductName("PET보틀-정사각(500ml)"),
                    Money(10_000),
                    ProductImageUrl("https://picsum.photos/seed/p1/600")
                )
            )
        )

        composeTestRule.setContent {
            ProductListScreen(
                uiState = uiState,
                onProductClick = { key -> clickedKey = key },
                onCartClick = {}
            )
        }
        composeTestRule.onNodeWithText("PET보틀-정사각(500ml)").performClick()

        assert(clickedKey == "p1")
    }
}
