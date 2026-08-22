package com.example.shoppingbasket.common.ext

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import org.junit.Rule
import org.junit.Test

class ClickableModifierTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun invokesOnClick() {
        var clicked = false

        composeTestRule.setContent {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .testTag("target")
                    .noRippleClickable { clicked = true }
            )
        }
        composeTestRule.onNodeWithTag("target").performClick()

        assert(clicked)
    }
}
