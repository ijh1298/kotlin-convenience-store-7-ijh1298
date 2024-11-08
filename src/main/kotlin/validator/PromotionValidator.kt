package validator

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

object PromotionValidator {
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    fun isValid(promotion: List<String>): Boolean {
        return try {
            require(promotion.size == 5) { "[ERROR] Promotion 문자열이 유효하지 않습니다." }
            require(promotion[1].all { it.isDigit() } && promotion[1].toInt() > 0) { "[ERROR] Promotion 구매 개수가 1 이상의 유효한 정수가 아닙니다." }
            require(promotion[2].all { it.isDigit() } && promotion[2].toInt() > 0) { "[ERROR] Promotion 증정 개수가 1 이상의 유효한 정수가 아닙니다." }
            require(isValidDate(promotion[3])) { "[ERROR] Promotion 시작 날짜가 유효하지 않습니다." }
            require(isValidDate(promotion[4])) { "[ERROR] Promotion 종료 날짜가 유효하지 않습니다." }
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }

    private fun isValidDate(string: String): Boolean {
        return try {
            LocalDate.parse(string, formatter)
            true
        } catch (e: DateTimeParseException) {
            false
        }
    }
}