package com.example.shoppingbasket.common.view

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test

class ShoppingButtonTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun displaysGivenLabel() {
        composeTestRule.setContent {
            ShoppingButton(text = "더보기", onClick = {})
        }

        composeTestRule.onNodeWithText("더보기").assertExists()
    }

    @Test
    fun clickingButtonInvokesCallback() {
        var clicked = false

        composeTestRule.setContent {
            ShoppingButton(text = "더보기", onClick = { clicked = true })
        }
        composeTestRule.onNodeWithText("더보기").performClick()

        assertThat(clicked).isTrue()
    }
}
