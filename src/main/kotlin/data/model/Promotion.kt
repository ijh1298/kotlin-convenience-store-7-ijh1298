package data.model

import java.time.LocalDate

data class Promotion(
    val name: String,
    val buy: Int,
    val get: Int,
    val startDate: LocalDate,
    val endDate: LocalDate,
) {
    fun isDateValid(targetDate: LocalDate): Boolean {
        return (targetDate.isEqual(this.startDate) || targetDate.isAfter(this.startDate))
                && (targetDate.isEqual(this.endDate) || targetDate.isBefore(this.endDate))
    }
}