package validator

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource

class ProductValidatorTest {
    @ParameterizedTest
    @CsvSource(
        "콜라,1000,0",
    )
    @DisplayName("products.md 입력에서 인자가 4개가 아닐 때")
    fun `product 문자열 유효성 검사`(inputFirst: String, inputSecond: String, inputThird: String) {
        val test = listOf(inputFirst, inputSecond, inputThird)
        assertThat(ProductValidator.isValid(test)).isEqualTo(false)
    }

    @ParameterizedTest
    @CsvSource(
        "콜라,1000,-1,null",
        "콜라,콜라,콜라,콜라",
    )
    @DisplayName("products.md 입력에서 유효하지 정수를 받을 때")
    fun `상품 가격, 재고 정수 유효성 검사`(inputFirst: String, inputSecond: String, inputThird: String, inputFourth: String) {
        val test = listOf(inputFirst, inputSecond, inputThird, inputFourth)
        assertThat(ProductValidator.isValid(test)).isEqualTo(false)
    }
}