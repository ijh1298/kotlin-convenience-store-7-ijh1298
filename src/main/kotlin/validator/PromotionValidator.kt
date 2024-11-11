package validator

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

object PromotionValidator {
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    fun isValid(promotion: List<String>): Boolean {
        return try {
            require(promotion.size == 5) { PromotionErrorMessage.INVALID_STRING }
            require(promotion[1].all { it.isDigit() } && promotion[1].toInt() > 0) { PromotionErrorMessage.INVALID_INT_BUY }
            require(promotion[2].all { it.isDigit() } && promotion[2].toInt() > 0) { PromotionErrorMessage.INVALID_INT_GET }
            require(isValidDate(promotion[3])) { PromotionErrorMessage.INVALID_START_DATE }
            require(isValidDate(promotion[4])) { PromotionErrorMessage.INVALID_END_DATE }
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