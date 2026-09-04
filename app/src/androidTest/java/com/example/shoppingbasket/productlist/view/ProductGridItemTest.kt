package com.example.shoppingbasket.productlist.view

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.shoppingbasket.common.model.Money
import com.example.shoppingbasket.common.model.Quantity
import com.example.shoppingbasket.productlist.domain.model.Product
import com.example.shoppingbasket.productlist.domain.model.ProductId
import com.example.shoppingbasket.productlist.domain.model.ProductImageUrl
import com.example.shoppingbasket.productlist.domain.model.ProductName
import com.example.shoppingbasket.productlist.viewmodel.ProductListItem
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test

class ProductGridItemTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun displaysNameAndPrice() {
        setItem(cartQuantity = null)

        composeTestRule.onNodeWithText("PET보틀-정사각(500ml)").assertExists()
        composeTestRule.onNodeWithText("10,000원").assertExists()
    }

    @Test
    fun displaysAddButtonWhenNotInCart() {
        setItem(cartQuantity = null)

        composeTestRule.onNodeWithContentDescription("장바구니에 담기").assertExists()
    }

    @Test
    fun clickingAddButtonInvokesCallback() {
        var addCalled = false
        setItem(cartQuantity = null, onAdd = { addCalled = true })

        composeTestRule.onNodeWithContentDescription("장바구니에 담기").performClick()

        assertThat(addCalled).isTrue()
    }

    @Test
    fun displaysStepperWhenInCart() {
        setItem(cartQuantity = Quantity(2))

        composeTestRule.onNodeWithText("2").assertExists()
    }

    @Test
    fun clickingIncreaseInvokesCallback() {
        var increaseCalled = false
        setItem(cartQuantity = Quantity(2), onIncrease = { increaseCalled = true })

        composeTestRule.onNodeWithContentDescription("수량 증가").performClick()

        assertThat(increaseCalled).isTrue()
    }

    @Test
    fun clickingDecreaseInvokesCallback() {
        var decreaseCalled = false
        setItem(cartQuantity = Quantity(2), onDecrease = { decreaseCalled = true })

        composeTestRule.onNodeWithContentDescription("수량 감소").performClick()

        assertThat(decreaseCalled).isTrue()
    }

    private fun setItem(
        cartQuantity: Quantity?,
        onAdd: () -> Unit = {},
        onIncrease: () -> Unit = {},
        onDecrease: () -> Unit = {}
    ) {
        val product = Product(
            ProductId("p1"),
            ProductName("PET보틀-정사각(500ml)"),
            Money(10_000),
            ProductImageUrl("https://picsum.photos/seed/p1/600")
        )
        composeTestRule.setContent {
            ProductGridItem(
                item = ProductListItem(product, cartQuantity),
                onAdd = onAdd,
                onIncrease = onIncrease,
                onDecrease = onDecrease
            )
        }
    }
}
