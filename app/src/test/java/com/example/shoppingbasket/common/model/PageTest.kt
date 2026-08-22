package com.example.shoppingbasket.common.model

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class PageTest {

    @Test
    @DisplayName("첫 페이지는 앞에서부터 페이지 크기만큼 잘라낸다")
    fun sliceOfFirstPage() {
        assertThat(Page.first(PAGE_SIZE).sliceOf(numbers(50))).containsExactlyElementsOf(1..20)
    }

    @Test
    @DisplayName("다음 페이지는 이전 페이지 다음 구간을 잘라낸다")
    fun sliceOfNextPage() {
        val secondPage = Page.first(PAGE_SIZE).next()
        assertThat(secondPage.sliceOf(numbers(50))).containsExactlyElementsOf(21..40)
    }

    @Test
    @DisplayName("마지막 페이지가 덜 찼으면 남은 개수만 잘라낸다")
    fun sliceOfPartialLastPage() {
        val lastPage = Page.first(PAGE_SIZE).next().next()
        assertThat(lastPage.sliceOf(numbers(50))).containsExactlyElementsOf(41..50)
    }

    @Test
    @DisplayName("빈 목록을 자르면 빈 결과를 반환한다")
    fun sliceOfEmptySource() {
        assertThat(Page.first(PAGE_SIZE).sliceOf(numbers(0))).isEmpty()
    }

    @Test
    @DisplayName("목록 범위를 벗어난 페이지를 잘라도 예외 없이 빈 결과를 반환한다")
    fun sliceOfOutOfRangePage() {
        val farPage = Page(10, PAGE_SIZE)
        assertThat(farPage.sliceOf(numbers(50))).isEmpty()
    }

    @Test
    @DisplayName("첫 페이지의 번호는 1이고 다음 페이지로 갈수록 1씩 늘어난다")
    fun number() {
        assertThat(Page.first(PAGE_SIZE).number()).isEqualTo(1)
        assertThat(Page.first(PAGE_SIZE).next().number()).isEqualTo(2)
    }

    @Test
    @DisplayName("첫 페이지에는 이전 페이지가 없다")
    fun hasPreviousOnFirstPage() {
        assertThat(Page.first(PAGE_SIZE).hasPrevious()).isFalse()
    }

    @Test
    @DisplayName("두 번째 페이지부터는 이전 페이지가 있다")
    fun hasPreviousAfterFirstPage() {
        assertThat(Page.first(PAGE_SIZE).next().hasPrevious()).isTrue()
    }

    @Test
    @DisplayName("첫 페이지에서 이전으로 가도 첫 페이지에 머무른다")
    fun previousStaysOnFirstPage() {
        assertThat(Page.first(PAGE_SIZE).previous().number()).isEqualTo(1)
    }

    @Test
    @DisplayName("남은 항목이 있으면 다음 페이지가 있다")
    fun hasNextWhenRemaining() {
        assertThat(Page.first(PAGE_SIZE).hasNextIn(50)).isTrue()
    }

    @Test
    @DisplayName("마지막 페이지에는 다음 페이지가 없다")
    fun hasNextOnLastPage() {
        val lastPage = Page.first(PAGE_SIZE).next().next()
        assertThat(lastPage.hasNextIn(50)).isFalse()
    }

    @Test
    @DisplayName("항목 수가 페이지 크기로 딱 나누어떨어지면 다음 페이지가 없다")
    fun hasNextOnExactlyFilledPage() {
        assertThat(Page.first(PAGE_SIZE).hasNextIn(20)).isFalse()
    }

    @Test
    @DisplayName("빈 목록에는 다음 페이지가 없다")
    fun hasNextOnEmptySource() {
        assertThat(Page.first(PAGE_SIZE).hasNextIn(0)).isFalse()
    }

    @Test
    @DisplayName("목록이 줄어 현재 페이지가 비면 내용이 남은 마지막 페이지로 당긴다")
    fun coercedIntoLastRemainingPage() {
        val thirdPage = Page.first(CART_PAGE_SIZE).next().next()
        assertThat(thirdPage.coercedIn(8).number()).isEqualTo(2)
    }

    @Test
    @DisplayName("현재 페이지에 내용이 남아 있으면 그 페이지를 유지한다")
    fun coercedKeepsValidPage() {
        val secondPage = Page.first(CART_PAGE_SIZE).next()
        assertThat(secondPage.coercedIn(8).number()).isEqualTo(2)
    }

    @Test
    @DisplayName("목록이 비면 첫 페이지로 당긴다")
    fun coercedIntoFirstPageWhenEmpty() {
        val thirdPage = Page.first(CART_PAGE_SIZE).next().next()
        assertThat(thirdPage.coercedIn(0).number()).isEqualTo(1)
    }

    @Test
    @DisplayName("페이지 색인은 음수일 수 없다")
    fun rejectsNegativeIndex() {
        assertThatThrownBy { Page(-1, PAGE_SIZE) }
            .isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    @DisplayName("페이지 크기는 1 미만일 수 없다")
    fun rejectsNonPositiveSize() {
        assertThatThrownBy { Page.first(0) }
            .isInstanceOf(IllegalArgumentException::class.java)
    }

    private fun numbers(count: Int): List<Int> = (1..count).toList()

    private companion object {
        const val PAGE_SIZE = 20
        const val CART_PAGE_SIZE = 5
    }
}
