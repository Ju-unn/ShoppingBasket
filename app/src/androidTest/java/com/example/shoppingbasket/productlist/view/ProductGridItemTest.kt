package com.example.shoppingbasket.productlist.view

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.shoppingbasket.common.model.Money
import com.example.shoppingbasket.productlist.model.Product
import com.example.shoppingbasket.productlist.model.ProductId
import com.example.shoppingbasket.productlist.model.ProductImageUrl
import com.example.shoppingbasket.productlist.model.ProductName
import org.junit.Rule
import org.junit.Test

class ProductGridItemTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun displaysNameAndPrice() {
        val product = Product(
            ProductId("p1"),
            ProductName("PET보틀-정사각(500ml)"),
            Money(10_000),
            ProductImageUrl("https://picsum.photos/seed/p1/600")
        )

        composeTestRule.setContent {
            ProductGridItem(product = product)
        }

        composeTestRule.onNodeWithText("PET보틀-정사각(500ml)").assertExists()
        composeTestRule.onNodeWithText("10,000원").assertExists()
    }
}
