package data.model

import java.time.LocalDateTime

data class Promotion(
    val name: String,
    val buy: Int,
    val get: Int,
    val startDate: LocalDateTime
)