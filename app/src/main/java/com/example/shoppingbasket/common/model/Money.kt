package com.example.shoppingbasket.common.model

@JvmInline
value class Money(private val amountWon: Int) {

    init {
        require(amountWon >= 0) { "금액은 0 이상이어야 한다" }
    }

    fun toDisplayValue(): String = MoneyFormatter.format(amountWon)

    // Room 저장을 위한 원시값 노출 (4단계 결정 10번, 영속화 전용)
    fun toRawValue(): Int = amountWon

    fun plus(other: Money): Money = Money(amountWon + other.amountWon)
}
