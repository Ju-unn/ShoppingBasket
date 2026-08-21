package com.example.shoppingbasket.common.model

@JvmInline
value class Money(private val amountWon: Int) {

    init {
        require(amountWon >= 0) { "금액은 0 이상이어야 한다" }
    }

    fun toDisplayValue(): String = MoneyFormatter.format(amountWon)
}
