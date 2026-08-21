package com.example.shoppingbasket.common.model

import java.text.NumberFormat
import java.util.Locale

// 현재는 원화(KRW) 전용. 다국가 통화 지원이 필요해지면 이 클래스만 확장한다.
object MoneyFormatter {

    fun format(amountWon: Int): String {
        val formatted = NumberFormat.getNumberInstance(Locale.KOREA).format(amountWon)
        return "${formatted}원"
    }
}
