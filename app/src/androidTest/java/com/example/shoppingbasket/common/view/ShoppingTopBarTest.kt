package com.example.shoppingbasket.common.view

import androidx.compose.material3.Text
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test

class ShoppingTopBarTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun displaysTitle() {
        composeTestRule.setContent {
            ShoppingTopBar(title = "Shopping")
        }

        composeTestRule.onNodeWithText("Shopping").assertExists()
    }

    @Test
    fun displaysLeadingIconWhenProvided() {
        composeTestRule.setContent {
            ShoppingTopBar(title = "Cart", leadingIcon = { Text("Back") })
        }

        composeTestRule.onNodeWithText("Back").assertExists()
    }
}
