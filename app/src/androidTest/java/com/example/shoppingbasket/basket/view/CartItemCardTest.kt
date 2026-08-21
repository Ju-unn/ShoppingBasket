package com.example.shoppingbasket.basket.view

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
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
        val product = Product(
            ProductId("p1"),
            ProductName("PET보틀-정사각(500ml)"),
            Money(10_000),
            ProductImageUrl("https://picsum.photos/seed/p1/600")
        )
        val cartItem = CartItem(product, Quantity(2))

        composeTestRule.setContent {
            CartItemCard(cartItem = cartItem, onRemove = {})
        }

        composeTestRule.onNodeWithText("PET보틀-정사각(500ml)").assertExists()
        composeTestRule.onNodeWithText("20,000원").assertExists()
    }
}
