package com.example.shoppingbasket.common.model

@JvmInline
value class Quantity(private val value: Int) {

    init {
        require(value >= 1) { "수량은 1개 이상이어야 한다" }
    }

    fun toDisplayValue(): String = value.toString()

    fun increasedBy(amount: Quantity): Quantity = Quantity(value + amount.value)

    fun decreased(): Quantity {
        check(!isMinimum()) { "최소 수량(1개)에서는 더 줄일 수 없다" }
        return Quantity(value - 1)
    }

    fun isMinimum(): Boolean = value == 1

    fun multiply(unit: Money): Money {
        return (1 until value).fold(unit) { total, _ -> total.plus(unit) }
    }

    companion object {
        val ONE = Quantity(1)
    }
}
