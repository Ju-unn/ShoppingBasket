package com.example.shoppingbasket

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

class MainActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun navigatesFromListToDetailAndBack() {
        composeTestRule.onNodeWithText("PET보틀-정사각(500ml)").performClick()

        composeTestRule.onNodeWithText("장바구니 담기").assertExists()

        composeTestRule.onNodeWithContentDescription("닫기").performClick()

        composeTestRule.onNodeWithText("Shopping").assertExists()
    }

    @Test
    fun navigatesToBasketFromCartIcon() {
        composeTestRule.onNodeWithContentDescription("장바구니").performClick()

        composeTestRule.onNodeWithText("Cart").assertExists()
    }
}
