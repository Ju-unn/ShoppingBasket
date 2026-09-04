package com.example.shoppingbasket.basket.view

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test

class CartPagingControlTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun displaysCurrentPageNumber() {
        setControl(pageNumber = 2)

        composeTestRule.onNodeWithText("2").assertExists()
    }

    @Test
    fun clickingNextInvokesCallbackWhenEnabled() {
        var nextCalled = false
        setControl(canGoNext = true, onNext = { nextCalled = true })

        composeTestRule.onNodeWithText(">").performClick()

        assertThat(nextCalled).isTrue()
    }

    @Test
    fun clickingPreviousInvokesCallbackWhenEnabled() {
        var previousCalled = false
        setControl(canGoPrevious = true, onPrevious = { previousCalled = true })

        composeTestRule.onNodeWithText("<").performClick()

        assertThat(previousCalled).isTrue()
    }

    @Test
    fun clickingNextDoesNothingOnLastPage() {
        var nextCalled = false
        setControl(canGoNext = false, onNext = { nextCalled = true })

        composeTestRule.onNodeWithText(">").performClick()

        assertThat(nextCalled).isFalse()
    }

    @Test
    fun clickingPreviousDoesNothingOnFirstPage() {
        var previousCalled = false
        setControl(canGoPrevious = false, onPrevious = { previousCalled = true })

        composeTestRule.onNodeWithText("<").performClick()

        assertThat(previousCalled).isFalse()
    }

    private fun setControl(
        pageNumber: Int = 1,
        canGoPrevious: Boolean = false,
        canGoNext: Boolean = false,
        onPrevious: () -> Unit = {},
        onNext: () -> Unit = {}
    ) {
        composeTestRule.setContent {
            CartPagingControl(
                pageNumber = pageNumber,
                canGoPrevious = canGoPrevious,
                canGoNext = canGoNext,
                onPrevious = onPrevious,
                onNext = onNext
            )
        }
    }
}
