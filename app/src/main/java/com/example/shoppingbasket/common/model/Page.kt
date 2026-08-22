package com.example.shoppingbasket.common.model

import kotlin.math.max
import kotlin.math.min

// 목록을 일정 개수씩 잘라 읽기 위한 페이지 좌표.
// index 는 0부터 시작하는 내부 색인이고, 화면에 보여줄 번호는 number() 로 얻는다.
// 상품 목록(20개)과 장바구니(5개)가 크기만 다르게 해서 같은 객체를 재사용한다.
class Page(private val index: Int, private val size: Int) {

    init {
        require(index >= FIRST_INDEX) { "페이지 색인은 0 이상이어야 한다" }
        require(size >= 1) { "페이지 크기는 1 이상이어야 한다" }
    }

    fun <T> sliceOf(source: List<T>): List<T> {
        val fromIndex = min(index * size, source.size)
        val toIndex = min(fromIndex + size, source.size)
        return source.subList(fromIndex, toIndex)
    }

    fun next(): Page = Page(index + 1, size)

    fun previous(): Page = Page(max(index - 1, FIRST_INDEX), size)

    fun hasPrevious(): Boolean = index > FIRST_INDEX

    fun hasNextIn(totalCount: Int): Boolean = (index + 1) * size < totalCount

    // 목록이 줄어 현재 페이지가 비어버린 경우(예: 장바구니에서 마지막 페이지의 상품을 전부 삭제)
    // 내용이 남아 있는 마지막 페이지로 당긴다.
    fun coercedIn(totalCount: Int): Page {
        val lastIndex = max(lastIndexOf(totalCount), FIRST_INDEX)
        return Page(min(index, lastIndex), size)
    }

    // 화면 표시용 1부터 시작하는 페이지 번호
    fun number(): Int = index + 1

    private fun lastIndexOf(totalCount: Int): Int = (totalCount - 1) / size

    companion object {
        private const val FIRST_INDEX = 0

        fun first(size: Int): Page = Page(FIRST_INDEX, size)
    }
}
