package com.example.shoppingbasket.basket.view

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.shoppingbasket.basket.model.CartItem
import com.example.shoppingbasket.common.model.Money
import com.example.shoppingbasket.common.model.Quantity
import com.example.shoppingbasket.productlist.model.Product
import com.example.shoppingbasket.productlist.model.ProductId
import com.example.shoppingbasket.productlist.model.ProductImageUrl
import com.example.shoppingbasket.productlist.model.ProductName
import org.junit.Rule
import org.junit.Test

class CartItemCardTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun displaysNameAndTotalPrice() {
        val cartItem = testCartItem(Quantity(2))

        composeTestRule.setContent {
            CartItemCard(cartItem = cartItem, onRemove = {}, onIncrease = {}, onDecrease = {})
        }

        composeTestRule.onNodeWithText("PET보틀-정사각(500ml)").assertExists()
        composeTestRule.onNodeWithText("20,000원").assertExists()
    }

    @Test
    fun clickingIncreaseInvokesCallback() {
        var increaseClicked = false
        composeTestRule.setContent {
            CartItemCard(
                cartItem = testCartItem(Quantity(1)),
                onRemove = {},
                onIncrease = { increaseClicked = true },
                onDecrease = {}
            )
        }

        composeTestRule.onNodeWithContentDescription("수량 증가").performClick()

        assert(increaseClicked)
    }

    @Test
    fun clickingDecreaseInvokesCallback() {
        var decreaseClicked = false
        composeTestRule.setContent {
            CartItemCard(
                cartItem = testCartItem(Quantity(1)),
                onRemove = {},
                onIncrease = {},
                onDecrease = { decreaseClicked = true }
            )
        }

        composeTestRule.onNodeWithContentDescription("수량 감소").performClick()

        assert(decreaseClicked)
    }

    private fun testCartItem(quantity: Quantity): CartItem {
        val product = Product(
            ProductId("p1"),
            ProductName("PET보틀-정사각(500ml)"),
            Money(10_000),
            ProductImageUrl("https://picsum.photos/seed/p1/600")
        )
        return CartItem(product, quantity)
    }
}
