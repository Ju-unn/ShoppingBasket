package com.example.shoppingbasket.basket.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.shoppingbasket.basket.model.CartRepository

class BasketViewModel : ViewModel() {

    private val _uiState = mutableStateOf(BasketUiState(cartItems = CartRepository.findAll().items()))
    val uiState: State<BasketUiState> = _uiState

    fun remove(key: String) {
        CartRepository.remove(key)
        _uiState.value = BasketUiState(cartItems = CartRepository.findAll().items())
    }
}
