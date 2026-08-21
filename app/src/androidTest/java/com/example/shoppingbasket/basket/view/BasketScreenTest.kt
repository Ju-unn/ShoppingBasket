package com.example.shoppingbasket.basket.view

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.shoppingbasket.basket.model.CartItem
import com.example.shoppingbasket.basket.viewmodel.BasketUiState
import com.example.shoppingbasket.common.model.Money
import com.example.shoppingbasket.common.model.Quantity
import com.example.shoppingbasket.productlist.model.Product
import com.example.shoppingbasket.productlist.model.ProductId
import com.example.shoppingbasket.productlist.model.ProductImageUrl
import com.example.shoppingbasket.productlist.model.ProductName
import org.junit.Rule
import org.junit.Test

class BasketScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun displaysTitleAndCartItems() {
        val uiState = BasketUiState(cartItems = listOf(testCartItem("p1")))

        composeTestRule.setContent {
            BasketScreen(uiState = uiState, onBack = {}, onRemove = {})
        }

        composeTestRule.onNodeWithText("Cart").assertExists()
        composeTestRule.onNodeWithText("PET보틀-정사각(500ml)").assertExists()
    }

    @Test
    fun clickingRemoveInvokesCallbackWithKey() {
        var removedKey: String? = null
        val uiState = BasketUiState(cartItems = listOf(testCartItem("p1")))

        composeTestRule.setContent {
            BasketScreen(uiState = uiState, onBack = {}, onRemove = { key -> removedKey = key })
        }
        composeTestRule.onNodeWithContentDescription("삭제").performClick()

        assert(removedKey == "p1")
    }

    private fun testCartItem(id: String): CartItem {
        val product = Product(
            ProductId(id),
            ProductName("PET보틀-정사각(500ml)"),
            Money(10_000),
            ProductImageUrl("https://picsum.photos/seed/$id/600")
        )
        return CartItem(product, Quantity(1))
    }
}
