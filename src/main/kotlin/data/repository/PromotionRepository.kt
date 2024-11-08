package data.repository

import data.model.Promotion
import validator.PromotionValidator
import java.io.BufferedReader
import java.io.InputStreamReader
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class PromotionRepository {
    private val promotionFile = readFile()

    fun fetchPromotions() = makePromotions(promotionFile)

    private fun readFile(): List<String> {
        val inputStream = this::class.java.classLoader.getResourceAsStream("promotions.md")
            ?: throw IllegalArgumentException("[ERROR] 'promotions.md' 파일을 찾을 수 없습니다.")

        return BufferedReader(InputStreamReader(inputStream)).use { it.readLines() }
    }

    private fun makePromotion(string: String): Promotion? {
        val promotionDetail = string.split(',').map { it }
        return promotionDetail.toPromotion()
    }

    private fun makePromotions(productFile: List<String>): List<Promotion> {
        return productFile.mapNotNull { makePromotion(it) }
    }

    companion object {
        private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        private fun List<String>.toPromotion(): Promotion? {
            if (!PromotionValidator.isValid(this))
                return null
            return Promotion(
                this[0],
                this[1].toInt(),
                this[2].toInt(),
                LocalDate.parse(this[3], formatter),
                LocalDate.parse(this[4], formatter)
            )
        }
    }
}
