package validator

import service.ConvenienceStoreService

object ProductValidator {
    fun isValid(product: List<String>): Boolean {
        return try {
            require(product.size == 4) { "[ERROR] Product 문자열이 유효하지 않습니다." }
            require(product[1].all { it.isDigit() }) { "[ERROR] Product 가격이 유효한 정수가 아닙니다." }
            require(product[2].all { it.isDigit() }) { "[ERROR] Product 재고가 유효한 정수가 아닙니다." }
            require(product[3] in ConvenienceStoreService.getPromotionNames()) { "[ERROR] Product Promotion이 존재하지 않습니다." }
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }
}