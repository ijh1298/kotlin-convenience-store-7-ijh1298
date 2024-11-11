package validator

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class InputItemValidatorTest {
    @ParameterizedTest
    @ValueSource(
        strings = [
            "콜라-5",
            "(사이다-3)",
            "[콜라--5]"
        ]
    )
    fun `입력 형식 예외 처리`(input: String) {
        assertThrows<IllegalArgumentException> { InputItemValidator.validate(input) }
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "[제로카페인제로슈거콜라-1]",
            "[바닐라콘아이스크림-5]",
        ]
    )
    fun `존재하지 않는 상품 예외 처리`(input: String) {
        assertThrows<IllegalArgumentException> { InputItemValidator.validate(input) }
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "[콜라-0]",
            "[초코바-0]",
        ]
    )
    fun `0개 구매 예외 처리`(input: String) {
        assertThrows<IllegalArgumentException> { InputItemValidator.validate(input) }
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "[콜라-1],[콜라-1]",
            "[사이다-1],[사이다-1]",
        ]
    )
    fun `동일 상품 중복 입력 예외 처리`(input: String) {
        assertThrows<IllegalArgumentException> { InputItemValidator.validate(input) }
    }
}