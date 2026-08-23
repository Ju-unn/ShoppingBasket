package com.example.shoppingbasket.common.view

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

class StepperTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun displaysQuantityText() {
        setStepper(quantityText = "3")

        composeTestRule.onNodeWithText("3").assertExists()
    }

    @Test
    fun clickingIncreaseInvokesCallback() {
        var increaseCalled = false
        setStepper(onIncrease = { increaseCalled = true })

        composeTestRule.onNodeWithContentDescription("수량 증가").performClick()

        assert(increaseCalled)
    }

    @Test
    fun clickingDecreaseInvokesCallback() {
        var decreaseCalled = false
        setStepper(onDecrease = { decreaseCalled = true })

        composeTestRule.onNodeWithContentDescription("수량 감소").performClick()

        assert(decreaseCalled)
    }

    private fun setStepper(
        quantityText: String = "1",
        onIncrease: () -> Unit = {},
        onDecrease: () -> Unit = {}
    ) {
        composeTestRule.setContent {
            Stepper(quantityText = quantityText, onIncrease = onIncrease, onDecrease = onDecrease)
        }
    }
}
